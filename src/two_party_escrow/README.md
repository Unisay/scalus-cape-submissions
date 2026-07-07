# Two-Party Escrow (Scalus)

Plutus V3 spending validator (`Data -> Unit`) for the UPLC-CAPE `two_party_escrow` scenario,
compiled from Scala with Scalus.

## Protocol

A buyer deposits 75 ADA into the script; the seller may accept (funds released to the seller) or
the buyer may refund after a deadline. State machine: `Deposited -> Accepted | Refunded`.

- **Redeemer**: a raw integer — `0` = Deposit, `1` = Accept, `2` = Refund. Anything else (other
  integers, constructors, bytestrings, lists, maps) is rejected.
- **Datum**: `Constr 0 [state, depositTime]` where `state = Constr {0|1|2} []` =
  Deposited | Accepted | Refunded.
- **Baked-in parameters**: buyer key (`0xAA`×32), seller key (`0xBB`×32), price 75 ADA, deadline
  1800 s, and the script credential (the ASCII bytes of the 58-character CAPE script hash).

## Validation rules

- **Deposit (0)**: signed by the buyer; exactly one output to the script address carrying exactly
  75 ADA and datum `Deposited` whose `depositTime` equals the *upper* bound of the validity range
  (which must be finite — an infinite upper bound is rejected).
- **Accept (1)**: current datum state is `Deposited`; signed by the seller; the seller receives
  exactly 75 ADA (summed across seller outputs); no funds remain at the script address.
- **Refund (2)**: current datum state is `Deposited`; signed by the buyer; the validity range is
  entirely after `depositTime + 1800` (finite lower bound, strictly greater); the buyer receives
  exactly 75 ADA.

## Implementation notes

Adapted from the Scalus `scalus.examples.cape.twopartyescrow` reference, corrected for the CAPE
evaluator's exact semantics: the deposit path has no own script input (the funding input is not the
script's own input), so the script credential is baked in and the deposit output is located by
credential; and `depositTime` is taken from the validity range's upper bound per the spec.

Two artifacts are produced: `two_party_escrow.uplc` (changPV, mainnet) and
`two_party_escrow-preview.uplc` (vanRossem preview). Both pass the full CAPE suite (all
`measurements` succeed, all `checks` error) and beat the Plinth production baseline on CPU, memory
and script size.
