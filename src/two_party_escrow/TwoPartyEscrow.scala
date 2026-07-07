package two_party_escrow

import common.Util
import scalus.*
import scalus.cardano.ledger.MajorProtocolVersion
import scalus.compiler.{compile, Compile, Options}
import scalus.cardano.onchain.plutus.prelude.*
import scalus.cardano.onchain.plutus.prelude.Option.*
import scalus.cardano.onchain.plutus.v1.IntervalBoundType
import scalus.cardano.onchain.plutus.v2.OutputDatum
import scalus.cardano.onchain.plutus.v3.*
import scalus.uplc.builtin.ByteString.*
import scalus.uplc.builtin.Data
import scalus.uplc.builtin.Data.{toData, FromData, ToData}

/** UPLC-CAPE Two-Party Escrow Scenario
  *
  * Compiles to a `Data -> Unit` spending validator implementing a buyer/seller escrow with a
  * `Deposited -> Accepted | Refunded` state machine. All parameters are baked in (buyer/seller
  * keys, 75 ADA price, 1800s deadline) per the CAPE spec.
  *
  * Redeemer is a raw integer: 0 = Deposit, 1 = Accept, 2 = Refund.
  *
  * Datum is `Constr 0 [state, depositTime]` with `state = Constr {0|1|2} []` =
  * Deposited | Accepted | Refunded.
  *
  * Adapted from the Scalus `scalus.examples.cape.twopartyescrow` reference, corrected for the
  * faithful CAPE evaluator semantics:
  *   - Deposit has no own script input (the funding input is `is_own_input:false`), so the script
  *     credential is baked in rather than derived from `findOwnInput`.
  *   - Deposit records `depositTime` as the *upper* bound of the validity range (finite; an
  *     infinite upper bound is rejected), per the production-safe convention in the spec.
  *   - The CAPE evaluator encodes the script hash as the ASCII bytes of "1"*58 (0x31 x 58), not a
  *     hex-decoded 28-byte hash.
  */

// state: Constr(0, []) = Deposited, Constr(1, []) = Accepted, Constr(2, []) = Refunded
enum EscrowState derives FromData, ToData:
    case Deposited
    case Accepted
    case Refunded

case class EscrowDatum(state: EscrowState, depositTime: BigInt) derives FromData, ToData

@Compile
object TwoPartyEscrowValidator {

    // CAPE parameters baked in as top-level inline defs so they are properly inlined
    // into the @Compile object.
    private inline def buyerKeyHash: PubKeyHash =
        PubKeyHash(hex"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
    private inline def sellerKeyHash: PubKeyHash =
        PubKeyHash(hex"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb")
    // The CAPE evaluator builds the script address from the ASCII bytes of the 58-character
    // "1111..." string (fromString), i.e. 0x31 repeated 58 times.
    private inline def ownScriptCredential: Credential =
        Credential.ScriptCredential(
          hex"31313131313131313131313131313131313131313131313131313131313131313131313131313131313131313131313131313131313131313131"
        )
    private inline def escrowPrice: Lovelace = BigInt(75_000_000)
    private inline def deadlineSeconds: BigInt = BigInt(1800)

    inline def validate(scData: Data): Unit = {
        val sc = scData.to[ScriptContext]
        sc.scriptInfo match
            case ScriptInfo.SpendingScript(txOutRef, datum) =>
                spend(datum, sc.redeemer, sc.txInfo, txOutRef)
            case _ => fail("Only spending scripts are supported by this validator")
    }

    inline def spend(
        datum: Option[Data],
        redeemer: Data,
        txInfo: TxInfo,
        txOutRef: TxOutRef
    ): Unit = {
        val action = redeemer.to[BigInt]
        if action == BigInt(0) then handleDeposit(txInfo)
        else if action == BigInt(1) then handleAccept(datum, txInfo, txOutRef)
        else if action == BigInt(2) then handleRefund(datum, txInfo, txOutRef)
        else fail("Invalid redeemer")
    }

    inline def handleDeposit(txInfo: TxInfo): Unit = {
        requireSignedBy(txInfo.signatories, buyerKeyHash, "Buyer must sign deposit")

        // depositTime is recorded as the (finite) upper bound of the validity range.
        val depositTime = upperBoundTime(txInfo.validRange)

        val expectedDatum = EscrowDatum(
          state = EscrowState.Deposited,
          depositTime = depositTime
        ).toData

        val expectedOutput = TxOut(
          address = Address(ownScriptCredential, Option.None),
          value = Value.lovelace(escrowPrice),
          datum = OutputDatum.OutputDatum(expectedDatum),
          referenceScript = Option.None
        )

        val output = findOutputsByCredential(txInfo.outputs, ownScriptCredential) match
            case List.Cons(head, List.Nil) => head
            case _                         => fail("Expected exactly one script output")
        require(output.toData == expectedOutput.toData, "Output must match expected deposit output")
    }

    inline def handleAccept(
        datum: Option[Data],
        txInfo: TxInfo,
        txOutRef: TxOutRef
    ): Unit = {
        val escrowDatum = datum.getOrFail("Datum not found").to[EscrowDatum]
        escrowDatum.state match
            case EscrowState.Deposited => ()
            case _                     => fail("Escrow must be in Deposited state")

        requireSignedBy(txInfo.signatories, sellerKeyHash, "Seller must sign accept")

        // The own input pins the escrow (must exist) and yields the script credential.
        val ownCredential = findOwnInputOrFail(txInfo.inputs, txOutRef).resolved.address.credential

        // Verify seller receives exactly the escrow price (summed across all seller outputs).
        val outputs = txInfo.outputs
        val sellerCred = Credential.PubKeyCredential(sellerKeyHash).toData
        val sellerAda = outputs.foldLeft(BigInt(0)): (sum, out) =>
            if out.address.credential.toData == sellerCred
            then sum + out.value.lovelaceAmount
            else sum
        require(sellerAda == escrowPrice, "Seller must receive exactly escrow price")

        // No funds should remain in the script (complete withdrawal).
        require(
          findOutputsByCredential(outputs, ownCredential).isEmpty,
          "No funds should remain in script"
        )
    }

    inline def handleRefund(
        datum: Option[Data],
        txInfo: TxInfo,
        txOutRef: TxOutRef
    ): Unit = {
        val escrowDatum = datum.getOrFail("Datum not found").to[EscrowDatum]
        escrowDatum.state match
            case EscrowState.Deposited => ()
            case _                     => fail("Escrow must be in Deposited state")

        requireSignedBy(txInfo.signatories, buyerKeyHash, "Buyer must sign refund")

        // Valid range must be entirely after the deadline (finite lower bound, strictly greater).
        val deadline = escrowDatum.depositTime + deadlineSeconds
        require(txInfo.validRange.isEntirelyAfter(deadline), "Deadline has not passed")

        val ownCredential = findOwnInputOrFail(txInfo.inputs, txOutRef).resolved.address.credential

        // Verify buyer receives exactly the escrow price (summed across all buyer outputs).
        val outputs = txInfo.outputs
        val buyerCred = Credential.PubKeyCredential(buyerKeyHash).toData
        val buyerAda = outputs.foldLeft(BigInt(0)): (sum, out) =>
            if out.address.credential.toData == buyerCred
            then sum + out.value.lovelaceAmount
            else sum
        require(buyerAda == escrowPrice, "Buyer must receive exactly escrow price")

        require(
          findOutputsByCredential(outputs, ownCredential).isEmpty,
          "No funds should remain in script"
        )
    }

    // Largest POSIX time consistent with the interval's upper bound. Rejects an infinite bound.
    def upperBoundTime(interval: Interval): BigInt =
        interval.to.boundType match
            case IntervalBoundType.Finite(t) =>
                if interval.to.isInclusive then t else t - BigInt(1)
            case _ => fail("Deposit requires a finite upper bound")

    def findOwnInputOrFail(inputs: List[TxInInfo], txOutRef: TxOutRef): TxInInfo = {
        def go(inputs: List[TxInInfo]): TxInInfo = inputs match
            case List.Cons(head, tail) =>
                if head.outRef.toData == txOutRef.toData then head
                else go(tail)
            case List.Nil => fail("Own input not found")
        go(inputs)
    }

    def findOutputsByCredential(outputs: List[TxOut], cred: Credential): List[TxOut] =
        outputs.filter(_.address.credential.toData == cred.toData)

    def requireSignedBy(
        signatories: List[PubKeyHash],
        party: PubKeyHash,
        message: String
    ): Unit = {
        def go(signatories: List[PubKeyHash]): Unit = signatories match
            case List.Nil              => fail(message)
            case List.Cons(head, tail) => if head.toData == party.toData then () else go(tail)
        go(signatories)
    }
}

@main def compileTwoPartyEscrow(): Unit =
    val sir = compile(TwoPartyEscrowValidator.validate)

    val program = common.Renamer.rename(sir.toUplcOptimized(using Options.release)().plutusV3)
    Util.writeUplc("two_party_escrow", "two_party_escrow.uplc", program.pretty.render(80))

    // vanRossem preview build (case-on-builtins, batch6, dropList)
    val vanRossem = Options.release.copy(targetProtocolVersion = MajorProtocolVersion.vanRossemPV)
    val programVR = common.Renamer.rename(sir.toUplcOptimized(using vanRossem)().plutusV3)
    Util.writeUplc(
      "two_party_escrow",
      "two_party_escrow-preview.uplc",
      programVR.pretty.render(80)
    )
    // Verification + metrics are measured by UPLC-CAPE via poreus://UPLC-CAPE/measure-artifact.
