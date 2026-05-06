package htlc

import scalus.cardano.ledger.MajorProtocolVersion
import scalus.cardano.onchain.plutus.prelude.{List as PList, Option as POption, SortedMap}
import scalus.cardano.onchain.plutus.v1.{
    Address,
    Credential,
    Interval,
    IntervalBound,
    IntervalBoundType,
    PubKeyHash,
    Value
}
import scalus.cardano.onchain.plutus.v2.{OutputDatum, TxOut}
import scalus.cardano.onchain.plutus.v3.{
    ScriptContext,
    ScriptInfo,
    TxId,
    TxInInfo,
    TxInfo,
    TxOutRef
}
import scalus.uplc.builtin.{ByteString, Builtins, Data, ToData}
import scalus.uplc.eval.{PlutusVM, Result}
import scalus.uplc.{Constant, Program, Term}

/** Off-chain harness to evaluate the HTLC validator on a minimal `ScriptContext`
  * for both `Claim` and `Refund` redeemers, against `changPV` and `vanRossemPV`.
  *
  * The context is constructed with empty defaults for any field the validator
  * does not read (governance, mint, certificates, redeemers map, etc.).
  */
object HtlcHarness:

    private val zero32 =
        ByteString.fromHex("0000000000000000000000000000000000000000000000000000000000000000")
    private val payerPkh28     = ByteString.fromHex("00" * 28)
    private val recipientPkh28 = ByteString.fromHex("11" * 28)
    private val scriptHash28   = ByteString.fromHex("22" * 28)
    private val preimage       = ByteString.fromString("htlc-preimage")
    private val secretHash     = Builtins.sha2_256(preimage)

    private val timeout: BigInt = BigInt(1000)

    private val payerAddr     = Address(Credential.PubKeyCredential(PubKeyHash(payerPkh28)), POption.None)
    private val recipientAddr = Address(Credential.PubKeyCredential(PubKeyHash(recipientPkh28)), POption.None)
    private val ownAddr       = Address(Credential.ScriptCredential(scriptHash28), POption.None)

    private val ownRef = TxOutRef(TxId(zero32), BigInt(0))
    private val ownInput =
        TxInInfo(ownRef, TxOut(ownAddr, Value.zero, OutputDatum.NoOutputDatum, POption.None))

    private val datum = HTLCDatum(
      payer = payerAddr,
      recipient = recipientAddr,
      secretHash = secretHash,
      timeout = timeout
    )

    // Claim is allowed iff upper bound (exclusive) < timeout.
    private val claimValidRange = Interval(
      from = IntervalBound(IntervalBoundType.NegInf, isInclusive = true),
      to = IntervalBound(IntervalBoundType.Finite(timeout - 1), isInclusive = true)
    )

    // Refund is allowed iff lower bound (inclusive) > timeout.
    private val refundValidRange = Interval(
      from = IntervalBound(IntervalBoundType.Finite(timeout + 1), isInclusive = true),
      to = IntervalBound(IntervalBoundType.PosInf, isInclusive = true)
    )

    private def mkTxInfo(signer: ByteString, validRange: Interval): TxInfo = TxInfo(
      inputs = PList.Cons(ownInput, PList.Nil),
      referenceInputs = PList.Nil,
      outputs = PList.Nil,
      fee = BigInt(0),
      mint = Value.zero,
      certificates = PList.Nil,
      withdrawals = SortedMap.empty,
      validRange = validRange,
      signatories = PList.Cons(PubKeyHash(signer), PList.Nil),
      redeemers = SortedMap.empty,
      data = SortedMap.empty,
      id = TxId(zero32),
      votes = SortedMap.empty,
      proposalProcedures = PList.Nil,
      currentTreasuryAmount = POption.None,
      treasuryDonation = POption.None
    )

    private val datumData: Data = summon[ToData[HTLCDatum]].apply(datum)
    private val spendingScriptInfo: ScriptInfo =
        ScriptInfo.SpendingScript(ownRef, POption.Some(datumData))

    private val claimRedeemerData: Data =
        summon[ToData[HTLCRedeemer]].apply(HTLCRedeemer.Claim(preimage))
    private val refundRedeemerData: Data =
        summon[ToData[HTLCRedeemer]].apply(HTLCRedeemer.Refund)

    private val claimContext: ScriptContext = ScriptContext(
      txInfo = mkTxInfo(recipientPkh28, claimValidRange),
      redeemer = claimRedeemerData,
      scriptInfo = spendingScriptInfo
    )
    private val refundContext: ScriptContext = ScriptContext(
      txInfo = mkTxInfo(payerPkh28, refundValidRange),
      redeemer = refundRedeemerData,
      scriptInfo = spendingScriptInfo
    )

    private def asTerm(d: Data): Term = Term.Const(Constant.Data(d))

    private val ctxToData: ToData[ScriptContext] = summon[ToData[ScriptContext]]
    private val claimCtxTerm  = asTerm(ctxToData.apply(claimContext))
    private val refundCtxTerm = asTerm(ctxToData.apply(refundContext))

    /** Evaluate `program` on the Claim and Refund contexts using the given VM. */
    def check(label: String, program: Program)(using PlutusVM): Unit =
        for (variant, term) <- Seq("claim" -> claimCtxTerm, "refund" -> refundCtxTerm) do
            (program $ term).term.evaluateDebug match
                case Result.Success(_, budget, _, _) =>
                    println(
                      s"✓ htlc/$label/$variant  cpu=${budget.steps} mem=${budget.memory}"
                    )
                case Result.Failure(ex, _, _, _) =>
                    sys.error(s"htlc/$label/$variant evaluation failed: $ex")

    /** Evaluate both the changPV (`program`) and vanRossemPV (`programVR`) builds. */
    def checkBoth(program: Program, programVR: Program): Unit =
        locally:
            given PlutusVM = PlutusVM.makePlutusV3VM()
            check("changPV  ", program)
        locally:
            given PlutusVM = PlutusVM.makePlutusV3VM(MajorProtocolVersion.vanRossemPV)
            check("vanRossem", programVR)
