# Linear Vesting (Scalus)

Plutus V3 spending validator (`Data -> Unit`) for the UPLC-CAPE `linear_vesting` scenario, compiled
from Scala with Scalus.

## Protocol

A native asset is locked at the script and released to a beneficiary on a linear installment
schedule. The beneficiary either partially unlocks (leaving the remaining amount at the script) or
fully unlocks after the vesting period ends.

- **Redeemer**: a nullary constructor — `Constr 0 []` = PartialUnlock, `Constr 1 []` = FullUnlock.
  A raw integer redeemer is rejected (it fails to decode as the enum).
- **Datum**: `Constr 0 [beneficiary, vestingAsset, totalVestingQty, vestingPeriodStart,
  vestingPeriodEnd, firstUnlockPossibleAfter, totalInstallments]`. All parameters live in the datum
  (nothing is baked in).

## Validation rules

- **FullUnlock**: signed by the beneficiary; the validity range is entirely after
  `vestingPeriodEnd` (finite lower bound, strictly greater).
- **PartialUnlock**: signed by the beneficiary; validity range entirely after
  `firstUnlockPossibleAfter`; exactly one input from the script address (anti double-satisfaction);
  the continuing (first / most-recently-added) script output preserves the datum unchanged and holds
  a remaining quantity that is positive, strictly less than the input's, and equal to the schedule:

  ```
  divCeil(x, y)          = 1 + (x - 1) / y
  timeBetween            = divCeil(vestingPeriodEnd - vestingPeriodStart, totalInstallments)
  futureInstallments     = divCeil(vestingPeriodEnd - currentTime, timeBetween)
  expectedRemainingQty   = divCeil(futureInstallments * totalVestingQty, totalInstallments)
  ```

  where `currentTime` is the validity range's lower bound.

## Implementation notes

The continuing output is selected as the *first* script output (the CAPE evaluator prepends
outputs, so the head is the most-recently-added one); the schedule tests attach a second, stale
script output that must be ignored.

Two artifacts are produced: `linear_vesting.uplc` (changPV, mainnet) and
`linear_vesting-preview.uplc` (vanRossem preview). Both pass the full CAPE suite (all
`measurements` succeed, all `checks` error) and beat the Plinth production baseline on CPU, memory
and script size.
