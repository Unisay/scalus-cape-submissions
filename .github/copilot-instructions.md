# GitHub Copilot Instructions

This file provides guidance to GitHub Copilot when working with code in this repository.

## Project Overview

This repository contains UPLC-CAPE benchmark submissions implemented using Scalus, a Scala-to-Plutus compiler. The project compiles Scala code to UPLC (Untyped Plutus Core) for Cardano blockchain execution.

**Key Technologies:**
- Scalus 0.18.2 - Scala-to-Plutus compiler (library + compiler plugin)
- Scala 3.3.7
- sbt 1.10.1
- Plutus Core 1.1.0 target

**Non-standard sbt layout:** `Compile / scalaSource` is set to `baseDirectory.value / "src"` in `build.sbt`, so sources live directly under `src/<scenario_name>/` (not `src/main/scala/...`).

## Development Environment

Use Nix flakes for reproducible environment:

```bash
# With direnv
direnv allow

# Or manually
nix develop
```

**Custom commands available in nix shell:**
- `build-scalus` - Compiles all Scalus scenario submissions to UPLC

## Building

**Compile Scala sources:**
```bash
sbt compile
```

**Generate UPLC programs:**
```bash
# Compile all submissions
build-scalus

# Or individually with full package path
sbt "runMain fibonacci_naive_recursion.compileFibonacciNaiveRecursion"
sbt "runMain fibonacci.compileFibonacci"
sbt "runMain fibonacci_prepacked.compileFibonacciPrepacked"
sbt "runMain factorial_naive_recursion.compileFactorialNaiveRecursion"
sbt "runMain factorial.compileFactorial"
sbt "runMain htlc.compileHtlc"
sbt "runMain two_party_escrow.compileTwoPartyEscrow"
sbt "runMain linear_vesting.compileLinearVesting"
```

**Important:** Main classes require the full package path (e.g., `fibonacci_naive_recursion.compileFibonacciNaiveRecursion`, not just `compileFibonacciNaiveRecursion`).

## Architecture

### Directory Organization

**Pattern:** `src/<scenario_name>/`

Each scenario directory contains:
- `<ScenarioName>.scala` - Source code implementation
- `<scenario>.uplc` - Compiled UPLC program (generated)
- `README.md` - Implementation description (optional)

Each scenario implementation:
1. Has a package matching the scenario name (e.g., `package fibonacci_naive_recursion`)
2. Either contains an `@Compile` annotated object with the core logic (most scenarios), or builds the UPLC `Term` directly for hand-optimized ones (`fibonacci`, `fibonacci_prepacked`, `factorial`)
3. Defines a `@main` function to compile (or assemble) and write UPLC output to the same directory
4. Generates a parameterized lambda function (not pre-applied)

**Example structure (`@Compile`-based scenario):**
```scala
package fibonacci_naive_recursion

@Compile
object FibonacciNaiveRecursion:
    def fibonacci(n: BigInt): BigInt = ...

@main def compileFibonacciNaiveRecursion(): Unit =
    val program = compile(FibonacciNaiveRecursion.fibonacci)
    val term = program.toUplc()
    // Write to src/fibonacci_naive_recursion/fibonacci.uplc
```

`src/common/` and `src/bench/` are shared helpers, not scenario submissions: `common/Renamer.scala` alpha-renames UPLC identifiers to short alphabetic names (Scalus can emit `NAME-NNNNrMMMM`-style names that some Plutus Core parsers reject), and `bench/Bench.scala` is a local ad-hoc runner for evaluating a compiled `.uplc` file against a sample input. Neither is part of the submission format.

### Current Scenarios

All scenarios are located in `src/`:

**Fibonacci:**
- `fibonacci/` - Optimized implementation using manual UPLC construction with fixed-point combinator
- `fibonacci_prepacked/` - Pre-packed ByteString lookup table (O(1) constant-time)
- `fibonacci_naive_recursion/` - Naive recursive implementation (exponential time)

**Factorial:**
- `factorial/` - Optimized factorial implementation
- `factorial_naive_recursion/` - Naive recursive factorial implementation

**HTLC (Hashed Time-Locked Contract):**
- `htlc/` - Spending validator (`Data -> Unit`) with `Claim(preimage)` / `Refund` redeemer. Uses the production-safe validity-range convention (claim checks upper bound of `txInfoValidRange`; refund checks lower bound).

**Two-Party Escrow:**
- `two_party_escrow/` - Spending validator (`Data -> Unit`) with a `Deposited -> Accepted | Refunded` state machine. Raw-integer redeemer (0=Deposit, 1=Accept, 2=Refund); buyer/seller keys, 75 ADA price and 1800s deadline baked in. Deposit records the (finite) upper bound of `txInfoValidRange` as `depositTime`; refund requires the lower bound strictly after `depositTime + 1800`.

**Linear Vesting:**
- `linear_vesting/` - Spending validator (`Data -> Unit`) releasing a native asset on an installment schedule. Nullary-constructor redeemer (`Constr 0 []`=PartialUnlock, `Constr 1 []`=FullUnlock); all parameters in the 7-field datum. Partial unlock enforces the `divCeil` schedule, datum preservation and a single-script-input anti-double-satisfaction guard, taking the first (most-recently-added) script output as the continuing UTxO.

### Verification and measurement

Correctness (all `measurements` accept, all `checks` reject) and metrics (CPU/memory/script size) are measured by the UPLC-CAPE framework, not locally: it evaluates the compiled `.uplc` against `scenarios/<scenario>/cape-tests.json` with the canonical evaluator. Do not build a local measurer.

### UPLC Compilation Process

Most scenarios go through the Scalus compiler plugin:
1. Scala code annotated with `@Compile` is processed by the Scalus compiler plugin
2. Scalus transforms Scala AST to Plutus IR
3. Output is rendered as UPLC text and written to the same `src/<scenario_name>/` directory
4. The compilation happens at Scala compile-time, producing a standalone UPLC program

The hand-optimized scenarios (`fibonacci`, `fibonacci_prepacked`, `factorial`) skip the plugin: the `@main` builds a `Term` directly, runs it through `common.Renamer` to alpha-rename identifiers, and writes the rendered UPLC text to the same directory.

**Key insight:** Functions are compiled as parameterized lambda functions that accept inputs (e.g., `\n -> ...`), not pre-applied with specific values. This allows the UPLC-CAPE benchmark framework to measure performance across different input values.

## Code Patterns

### Scenario Types

**Naive Implementations:**
- Match mathematical definitions directly
- No optimizations
- Example: Naive recursive fibonacci (exponential time)

**Optimized Implementations:**
- Performance-focused implementations
- May use tail recursion, iterative approaches, or manual UPLC construction
- Example: Iterative fibonacci with tail recursion (linear time)

### Adding New Scenarios

1. Create directory: `src/<scenario_name>/`
2. Create source file: `src/<scenario_name>/<ScenarioName>.scala`
3. Add `@Compile` annotation to the object containing the logic
4. Create a `@main` function to compile and write UPLC to `src/<scenario_name>/<scenario>.uplc`
5. Update `build-scalus` command in `flake.nix` to include new main class
6. Optionally add `README.md` describing the implementation

## UPLC-CAPE Submission Format

Each scenario directory in `src/` contains:
- `<ScenarioName>.scala` - Source code implementation
- `<scenario>.uplc` - The compiled UPLC program (parameterized lambda function)
- `README.md` - Implementation description (optional)

**Note:** Configuration and metadata files (config.json, metadata.json, metrics.json) are generated by the UPLC-CAPE benchmark framework and should not be committed to the repository.
