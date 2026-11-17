package two_party_escrow

import common.Util
import scalus.*
import scalus.compiler.{compile, Compile}
import scalus.cardano.onchain.plutus.prelude.*
import scalus.cardano.onchain.plutus.v1.IntervalBoundType
import scalus.cardano.onchain.plutus.v2.OutputDatum
import scalus.cardano.onchain.plutus.v3.*
import scalus.uplc.builtin.ByteString.*
import scalus.uplc.builtin.Data
import scalus.uplc.builtin.Data.{toData, FromData, ToData}

/** Revision 2 escrow datum: Constr 0 [I depositTime]. */
case class EscrowDatum(depositTime: BigInt) derives FromData, ToData

/** One Plutus V3 script for the minting and spending sides of the thread-token escrow.
  * Fixed parameters come from the CAPE specification; the policy/script hash comes from the
  * context. Rule identifiers in failures are preserved by the separate debug build.
  */
@Compile
object TwoPartyEscrowValidator {
    private inline def buyerKeyHash: PubKeyHash =
        PubKeyHash(hex"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
    private inline def sellerKeyHash: PubKeyHash =
        PubKeyHash(hex"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb")
    private inline def escrowPrice: Lovelace = BigInt(75_000_000)
    private inline def deadlineMillis: BigInt = BigInt(1_800_000)
    private inline def tokenName: TokenName = hex"657363726f77"

    inline def validate(scData: Data): Unit = {
        // The ledger supplies a typed V3 context; shape validation is outside the scenario.
        val sc = scData.to[ScriptContext]
        sc.scriptInfo match
            case ScriptInfo.MintingScript(policyId) =>
                val action = sc.redeemer.to[BigInt]
                if action == BigInt(0) then deposit(sc.txInfo, policyId)
                else if action == BigInt(1) || action == BigInt(2) then
                    require(singletonMint(sc.txInfo.mint, policyId, BigInt(-1)), "B1: exact burn")
                else fail("X1: invalid minting redeemer")
            case ScriptInfo.SpendingScript(txOutRef, datum) =>
                val action = sc.redeemer.to[BigInt]
                require(action == BigInt(1) || action == BigInt(2), "X1: invalid spending redeemer")
                spend(sc.txInfo, txOutRef, datum, action)
            case _ => fail("X2: unsupported purpose")
    }

    inline def deposit(txInfo: TxInfo, policyId: PolicyId): Unit = {
        requireSignedBy(txInfo.signatories, buyerKeyHash, "D1: buyer signature")
        val depositTime = txInfo.validRange.to.boundType match
            case IntervalBoundType.Finite(t) => t - BigInt(1)
            case _                          => fail("D2: finite upper bound")

        require(singletonMint(txInfo.mint, policyId, BigInt(1)), "D3: exact mint")
        val credential = Credential.ScriptCredential(policyId)
        val output = txInfo.outputs.filter(_.address.credential.toData == credential.toData) match
            case List.Cons(head, List.Nil) => head
            case _                        => fail("D4: exactly one escrow output")
        require(output.value.lovelaceAmount == escrowPrice, "D5: exact deposit lovelace")
        require(output.value.quantityOf(policyId, tokenName) == BigInt(1), "D6: output token")
        val expectedDatum = EscrowDatum(depositTime).toData
        output.datum match
            case OutputDatum.OutputDatum(datum) =>
                require(datum == expectedDatum, "D7: exact inline datum")
            case _ => fail("D7: inline datum required")
    }

    /** D3/B1 constrain this policy's entire entry, leaving every other policy alone. */
    def singletonMint(mint: Value, policyId: PolicyId, quantity: BigInt): Boolean =
        mint.toSortedMap.get(policyId) match
            case Option.Some(tokens) =>
                tokens.toList match
                    case List.Cons(pair, List.Nil) =>
                        pair._1 == tokenName && pair._2 == quantity
                    case _ => false
            case Option.None => false

    inline def spend(
        txInfo: TxInfo,
        txOutRef: TxOutRef,
        datum: Option[Data],
        action: BigInt
    ): Unit = {
        val ownInput = findOwnInput(txInfo.inputs, txOutRef)
        val policyId = ownInput.resolved.address.credential match
            case Credential.ScriptCredential(hash) => hash
            case _ => fail("S1: own input must be at script")
        val ownCredential = Credential.ScriptCredential(policyId).toData
        val ownInputCount = txInfo.inputs.foldLeft(BigInt(0)): (count, input) =>
            if input.resolved.address.credential.toData == ownCredential then count + BigInt(1)
            else count
        require(ownInputCount == BigInt(1), "S2: exactly one escrow input")
        // S3 names one quantity; B1 checks the complete mint entry in the other script run.
        require(txInfo.mint.quantityOf(policyId, tokenName) == BigInt(-1), "S3: token burn")

        if action == BigInt(1) then
            requireSignedBy(txInfo.signatories, sellerKeyHash, "A1: seller signature")
            require(paidTo(txInfo.outputs, sellerKeyHash) >= escrowPrice, "A2: seller payment")
        else
            requireSignedBy(txInfo.signatories, buyerKeyHash, "R1: buyer signature")
            val escrowDatum = datum.getOrFail("R2: datum required").to[EscrowDatum]
            val lower = txInfo.validRange.from.boundType match
                case IntervalBoundType.Finite(t) => t
                case _                          => fail("R2: finite lower bound")
            require(
              lower > escrowDatum.depositTime + deadlineMillis,
              "R2: refund after deadline"
            )
            require(paidTo(txInfo.outputs, buyerKeyHash) >= escrowPrice, "R3: buyer payment")
    }

    def findOwnInput(inputs: List[TxInInfo], ref: TxOutRef): TxInInfo = inputs match
        case List.Cons(head, tail) =>
            if head.outRef.toData == ref.toData then head else findOwnInput(tail, ref)
        case List.Nil => fail("S1: own input not found")

    def paidTo(outputs: List[TxOut], party: PubKeyHash): Lovelace = {
        val credential = Credential.PubKeyCredential(party).toData
        outputs.foldLeft(BigInt(0)): (sum, output) =>
            if output.address.credential.toData == credential then sum + output.value.lovelaceAmount
            else sum
    }

    def requireSignedBy(signatories: List[PubKeyHash], party: PubKeyHash, message: String): Unit =
        signatories match
            case List.Cons(head, tail) =>
                if head.toData == party.toData then () else requireSignedBy(tail, party, message)
            case List.Nil => fail(message)
}

@main def compileTwoPartyEscrow(): Unit =
    val sir = compile(TwoPartyEscrowValidator.validate)
    val program = common.Renamer.rename(sir.toUplcOptimized(using Util.release)().plutusV3)
    Util.writeUplc("two_party_escrow", "two_party_escrow.uplc", program.pretty.render(80))

/** Same optimizations and protocol as release, with rule messages retained. Not a submission. */
@main def compileTwoPartyEscrowDebug(): Unit =
    val sir = compile(TwoPartyEscrowValidator.validate)
    val options = Util.release.copy(generateErrorTraces = true, removeTraces = false)
    val program = common.Renamer.rename(sir.toUplcOptimized(using options)().plutusV3)
    val path = java.nio.file.Paths.get(".local", "two_party_escrow-debug.uplc")
    java.nio.file.Files.createDirectories(path.getParent)
    java.nio.file.Files.writeString(path, program.pretty.render(80))
    println(s"Debug UPLC: ${path.toAbsolutePath}")
