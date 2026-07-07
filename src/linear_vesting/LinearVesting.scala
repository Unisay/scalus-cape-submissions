package linear_vesting

import common.Util
import scalus.*
import scalus.cardano.ledger.MajorProtocolVersion
import scalus.compiler.{compile, Compile, Options}
import scalus.cardano.onchain.plutus.prelude.*
import scalus.cardano.onchain.plutus.prelude.Option.*
import scalus.cardano.onchain.plutus.v2.OutputDatum
import scalus.cardano.onchain.plutus.v3.*
import scalus.uplc.builtin.{ByteString, Data}
import scalus.uplc.builtin.Data.{toData, FromData, ToData}

/** UPLC-CAPE Linear Vesting Scenario
  *
  * Compiles to a `Data -> Unit` spending validator releasing a native asset to a beneficiary on an
  * installment schedule. All parameters live in the datum (nothing baked in).
  *
  * Redeemer is a nullary constructor: `Constr 0 []` = PartialUnlock, `Constr 1 []` = FullUnlock
  * (a raw integer redeemer is rejected because it fails to decode as the enum).
  *
  * Datum is `Constr 0 [beneficiary, vestingAsset, totalVestingQty, vestingPeriodStart,
  * vestingPeriodEnd, firstUnlockPossibleAfter, totalInstallments]`.
  */

case class VestingAsset(currencySymbol: ByteString, tokenName: ByteString) derives FromData, ToData

case class VestingDatum(
    beneficiary: Address,
    vestingAsset: VestingAsset,
    totalVestingQty: BigInt,
    vestingPeriodStart: BigInt,
    vestingPeriodEnd: BigInt,
    firstUnlockPossibleAfter: BigInt,
    totalInstallments: BigInt
) derives FromData,
      ToData

enum VestingRedeemer derives FromData, ToData:
    case PartialUnlock
    case FullUnlock

@Compile
object LinearVestingValidator {

    inline def validate(scData: Data): Unit = {
        val sc = scData.to[ScriptContext]
        sc.scriptInfo match
            case ScriptInfo.SpendingScript(txOutRef, datumOpt) =>
                val datumData = datumOpt.getOrFail("Datum not found")
                val datum = datumData.to[VestingDatum]
                sc.redeemer.to[VestingRedeemer] match
                    case VestingRedeemer.PartialUnlock =>
                        handlePartial(datum, datumData, sc.txInfo, txOutRef)
                    case VestingRedeemer.FullUnlock =>
                        handleFull(datum, sc.txInfo)
            case _ => fail("Only spending scripts are supported by this validator")
    }

    inline def handleFull(datum: VestingDatum, txInfo: TxInfo): Unit = {
        require(txInfo.isSignedBy(beneficiaryPkh(datum)), "Beneficiary must sign")
        // Lower bound finite and strictly after the vesting period end.
        require(
          txInfo.validRange.isEntirelyAfter(datum.vestingPeriodEnd),
          "Vesting period has not ended"
        )
    }

    inline def handlePartial(
        datum: VestingDatum,
        datumData: Data,
        txInfo: TxInfo,
        txOutRef: TxOutRef
    ): Unit = {
        require(txInfo.isSignedBy(beneficiaryPkh(datum)), "Beneficiary must sign")
        // Lower bound finite and strictly after the first-unlock time (also rejects an infinite
        // lower bound, so `getValidityStartTime` below sees a finite value).
        require(
          txInfo.validRange.isEntirelyAfter(datum.firstUnlockPossibleAfter),
          "First unlock time not reached"
        )
        val currentTime = txInfo.getValidityStartTime

        val ownInput = txInfo.findOwnInputOrFail(txOutRef)
        val ownCredential = ownInput.resolved.address.credential

        // Exactly one input from this script address (anti double-satisfaction).
        require(
          txInfo.findOwnInputsByCredential(ownCredential).length == BigInt(1),
          "Exactly one script input allowed"
        )

        // The continuing output is the first (most-recently-added) script output.
        val continuing = txInfo.findOwnOutputsByCredential(ownCredential) match
            case List.Cons(head, _) => head
            case List.Nil           => fail("Missing continuing script output")

        val cs = datum.vestingAsset.currencySymbol
        val tn = datum.vestingAsset.tokenName
        val oldRemaining = ownInput.resolved.value.quantityOf(cs, tn)
        val newRemaining = continuing.value.quantityOf(cs, tn)

        require(newRemaining > BigInt(0), "Remaining quantity must be positive (use FullUnlock)")
        require(newRemaining < oldRemaining, "Remaining quantity must strictly decrease")
        require(
          newRemaining == expectedRemaining(datum, currentTime),
          "Remaining quantity must match the vesting schedule"
        )

        // Datum must be preserved unchanged on the continuing output.
        continuing.datum match
            case OutputDatum.OutputDatum(d) => require(d == datumData, "Datum must be preserved")
            case _                          => fail("Continuing output must carry an inline datum")
    }

    def expectedRemaining(datum: VestingDatum, currentTime: BigInt): BigInt = {
        val vestingPeriodLength = datum.vestingPeriodEnd - datum.vestingPeriodStart
        val timeBetweenTwoInstallments = divCeil(vestingPeriodLength, datum.totalInstallments)
        val vestingTimeRemaining = datum.vestingPeriodEnd - currentTime
        val futureInstallments = divCeil(vestingTimeRemaining, timeBetweenTwoInstallments)
        divCeil(futureInstallments * datum.totalVestingQty, datum.totalInstallments)
    }

    def divCeil(x: BigInt, y: BigInt): BigInt = BigInt(1) + (x - BigInt(1)) / y

    def beneficiaryPkh(datum: VestingDatum): PubKeyHash =
        datum.beneficiary.credential match
            case Credential.PubKeyCredential(pkh) => pkh
            case _                                => fail("Beneficiary must be a public key credential")
}

@main def compileLinearVesting(): Unit =
    val sir = compile(LinearVestingValidator.validate)

    val program = common.Renamer.rename(sir.toUplcOptimized(using Options.release)().plutusV3)
    Util.writeUplc("linear_vesting", "linear_vesting.uplc", program.pretty.render(80))

    // vanRossem preview build (case-on-builtins, batch6, dropList)
    val vanRossem = Options.release.copy(targetProtocolVersion = MajorProtocolVersion.vanRossemPV)
    val programVR = common.Renamer.rename(sir.toUplcOptimized(using vanRossem)().plutusV3)
    Util.writeUplc("linear_vesting", "linear_vesting-preview.uplc", programVR.pretty.render(80))
    // Verification + metrics are measured by UPLC-CAPE via poreus://UPLC-CAPE/measure-artifact.
