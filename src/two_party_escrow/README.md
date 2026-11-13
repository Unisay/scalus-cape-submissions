# Two-Party Escrow (Scalus)

One Plutus V3 script (`Data -> Unit`) implements revision 2 of the UPLC-CAPE escrow scenario.
The minting purpose handles deposits and token burns; the spending purpose handles acceptance
and refunds. The script derives its own hash from the context.

## Parameters and encoding

- Buyer and seller key hashes are respectively 28 bytes of `aa` and `bb`.
- The price is 75 ADA (`75000000` lovelace), and the refund delay is 1800000 milliseconds.
- The thread token has policy id equal to the script hash and token name `escrow` (`657363726f77`).
- The inline datum is exactly `Constr 0 [I depositTime]`.
- Redeemers are raw Data integers: Minting 0 deposits; Minting 1 or 2 burns; Spending 1 accepts;
  Spending 2 refunds. Every other purpose or redeemer fails.

## Rules

Deposit requires the buyer's signature, a finite upper validity bound `t`, and an own-policy
mint entry containing exactly one `escrow` token. Exactly one output has the script payment
credential; it must hold exactly 75 ADA and one thread token, with inline datum recording `t-1`.
Other assets, staking credentials and reference scripts are allowed.

Burn requires the own-policy mint entry to contain exactly `{escrow: -1}`.

Accept and Refund find the own input using its output reference, derive the script hash from
its payment credential, require exactly one input at that credential, and require a token burn.
Accept requires the seller's signature and payments totalling at least 75 ADA to the seller.
Refund requires the buyer's signature, a finite lower validity bound strictly greater than
`depositTime + 1800000`, and payments totalling at least 75 ADA to the buyer. Payments compare
whole payment credentials and sum all matching outputs. Remaining script outputs are allowed.

The ledger's value conservation and the minting branch establish token provenance; the
spending branch does not repeat the token quantity check on its own input. Validity closure
flags are ignored because the ledger supplies inclusive lower and exclusive upper bounds.

## Building and validation

```sh
nix develop --command sbt 'runMain two_party_escrow.compileTwoPartyEscrow'
nix develop --command sbt 'runMain two_party_escrow.compileTwoPartyEscrowDebug'
```

The debug main retains rule-labelled error traces in `.local/two_party_escrow-debug.uplc`.
Non-integer redeemers and malformed contexts can fail in `unIData` / `unConstrData` without
a program trace; these failures map to X1 and X3 respectively. This diagnostic artifact is separate from
the release submission. The UPLC-CAPE canonical evaluator
runs the scenario suite and measures CPU, memory, serialized script size and term size.

The release artifact is `two_party_escrow.uplc`, compiled with `common.Util.release` for
van Rossem (protocol version 11) on Scalus 1.3.0 / Scala 3.3.8.
