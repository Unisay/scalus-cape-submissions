package htlc

import common.Util
import scalus.*
import scalus.cardano.ledger.MajorProtocolVersion
import scalus.compiler.{Compile, compile, Options}
import scalus.uplc.builtin.*
import scalus.uplc.builtin.Builtins.sha2_256
import scalus.cardano.onchain.plutus.prelude.*
import scalus.cardano.onchain.plutus.prelude.Option.{Some, None}
import scalus.cardano.onchain.plutus.v1.{
    Address,
    Credential,
    Interval,
    IntervalBoundType,
    PubKeyHash
}
import scalus.cardano.onchain.plutus.v3.*

/** UPLC-CAPE HTLC Scenario
  *
  * Hashed Time-Locked Contract validator. Compiles to a `Data -> Unit` spending validator that
  * accepts either Claim (recipient reveals preimage before timeout) or Refund (payer reclaims
  * after timeout). Uses the production-safe validity-range convention:
  *   - Claim: upper bound of txInfoValidRange must be finite and strictly less than timeout.
  *   - Refund: lower bound of txInfoValidRange must be finite and strictly greater than timeout.
  */

case class HTLCDatum(
    payer: Address,
    recipient: Address,
    secretHash: ByteString,
    timeout: BigInt
) derives FromData,
      ToData

@Compile object HTLCDatum

enum HTLCRedeemer derives FromData, ToData:
    case Claim(preimage: ByteString) // constructor tag 0
    case Refund                      // constructor tag 1

@Compile object HTLCRedeemer

@Compile object HtlcValidator:
    inline val MustBeSpending        = "Expected SpendingScript"
    inline val MissingDatum          = "Missing HTLC datum"
    inline val ExpectedPkhCredential = "Expected PubKeyCredential"
    inline val ExpectedScriptCred    = "Expected ScriptCredential"
    inline val ClaimTimeNotBefore    = "Claim not permitted at or after timeout"
    inline val RefundTimeNotAfter    = "Refund not permitted at or before timeout"
    inline val ClaimSigMissing       = "Missing recipient signature"
    inline val RefundSigMissing      = "Missing payer signature"
    inline val BadPreimage           = "Preimage does not match stored hash"
    inline val DoubleSatisfaction    = "Double satisfaction"
    inline val UpperUnbounded        = "Claim requires a finite upper bound"
    inline val LowerUnbounded        = "Refund requires a finite lower bound"
    inline val OwnInputNotFound      = "Own input not found"

    def validate(scData: Data): Unit =
        val ctx = scData.to[ScriptContext]
        ctx.scriptInfo match
            case ScriptInfo.SpendingScript(ownRef, datumOpt) =>
                val datum = datumOpt match
                    case Some(d) => d.to[HTLCDatum]
                    case None    => fail(MissingDatum)
                ctx.redeemer.to[HTLCRedeemer] match
                    case HTLCRedeemer.Claim(preimage) =>
                        validateClaim(ctx.txInfo, ownRef, datum, preimage)
                    case HTLCRedeemer.Refund =>
                        validateRefund(ctx.txInfo, ownRef, datum)
            case _ => fail(MustBeSpending)

    def validateClaim(
        tx: TxInfo,
        ownRef: TxOutRef,
        d: HTLCDatum,
        preimage: ByteString
    ): Unit =
        require(sha2_256(preimage) == d.secretHash, BadPreimage)
        require(tx.isSignedBy(extractPkh(d.recipient)), ClaimSigMissing)
        require(upperBoundExclusive(tx.validRange) < d.timeout, ClaimTimeNotBefore)
        require(
            countScriptInputs(tx, ownScriptHash(tx, ownRef)) == BigInt(1),
            DoubleSatisfaction
        )

    def validateRefund(tx: TxInfo, ownRef: TxOutRef, d: HTLCDatum): Unit =
        require(tx.isSignedBy(extractPkh(d.payer)), RefundSigMissing)
        require(lowerBoundInclusive(tx.validRange) > d.timeout, RefundTimeNotAfter)
        require(
            countScriptInputs(tx, ownScriptHash(tx, ownRef)) == BigInt(1),
            DoubleSatisfaction
        )

    def extractPkh(addr: Address): PubKeyHash =
        addr.credential match
            case Credential.PubKeyCredential(pkh) => pkh
            case _                                => fail(ExpectedPkhCredential)

    def extractScriptHash(addr: Address): ByteString =
        addr.credential match
            case Credential.ScriptCredential(h) => h
            case _                              => fail(ExpectedScriptCred)

    // Smallest POSIX time consistent with the interval's lower bound. Reject NegInf.
    def lowerBoundInclusive(r: Interval): BigInt =
        r.from.boundType match
            case IntervalBoundType.Finite(t) =>
                if r.from.isInclusive then t else t + BigInt(1)
            case _ => fail(LowerUnbounded)

    // Largest POSIX time consistent with the interval's upper bound. Reject PosInf.
    def upperBoundExclusive(r: Interval): BigInt =
        r.to.boundType match
            case IntervalBoundType.Finite(t) =>
                if r.to.isInclusive then t else t - BigInt(1)
            case _ => fail(UpperUnbounded)

    def countScriptInputs(tx: TxInfo, scriptHash: ByteString): BigInt =
        tx.inputs.count: (i: TxInInfo) =>
            i.resolved.address.credential match
                case Credential.ScriptCredential(h) => h == scriptHash
                case _                              => false

    def ownScriptHash(tx: TxInfo, ownRef: TxOutRef): ByteString =
        tx.findOwnInput(ownRef) match
            case Some(i) => extractScriptHash(i.resolved.address)
            case None    => fail(OwnInputNotFound)

@main def compileHtlc(): Unit =
    val sir = compile(HtlcValidator.validate)

    val program = common.Renamer.rename(
      sir.toUplcOptimized(using Options.release)().plutusV3
    )
    Util.writeUplc("htlc", "htlc.uplc", program.pretty.render(80))

    // vanRossem preview build (case-on-builtins, batch6, dropList)
    val vanRossem = Options.release.copy(targetProtocolVersion = MajorProtocolVersion.vanRossemPV)
    val programVR = common.Renamer.rename(
      sir.toUplcOptimized(using vanRossem)().plutusV3
    )
    Util.writeUplc("htlc", "htlc-preview.uplc", programVR.pretty.render(80))

    // Sanity-eval both variants on Claim and Refund redeemers against minimal ScriptContexts.
    HtlcHarness.checkBoth(program, programVR)
