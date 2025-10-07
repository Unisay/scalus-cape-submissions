# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This repository contains UPLC-CAPE benchmark submissions implemented using Scalus, a Scala-to-Plutus compiler. The project compiles Scala code to UPLC (Untyped Plutus Core) for Cardano blockchain execution.

**Key Technologies:**
- Scalus 0.12.0 - Scala-to-Plutus compiler
- Scala 3.3.6
- sbt 1.10.1
- Plutus Core 1.1.0 target

## Development Environment

Use Nix flakes for reproducible environment:

```bash
# With direnv
direnv allow

# Or manually
nix develop
```

**Custom commands available in nix shell:**
- `build-scalus` - Compiles all Scalus sources to UPLC (runs both compileFibonacciBase and compileFibonacciOpen)

## Building

**Compile Scala sources:**
```bash
sbt compile
```

**Generate UPLC programs:**
```bash
# Compile all scenarios
build-scalus

# Or individually with full package path
sbt "runMain fibonacci.compileFibonacciBase"
sbt "runMain fibonacci.compileFibonacciOpen"
```

**Important:** Main classes require the full package path (e.g., `fibonacci.compileFibonacciBase`, not just `compileFibonacciBase`).

## Architecture

### Source Organization

**Pattern:** `src/<scenario>/<ScenarioName><Mode>.scala`

Each scenario implementation:
1. Has a package matching the scenario name (e.g., `package fibonacci`)
2. Contains an `@Compile` annotated object with the core logic
3. Defines a `@main` function to compile and write UPLC output
4. Implements both Base and Open modes

**Example structure:**
```scala
package fibonacci

@Compile
object FibonacciBase:
    def fibonacci(n: BigInt): BigInt = ...
    def fibonacci25: BigInt = fibonacci(BigInt(25))

@main def compileFibonacciBase(): Unit =
    val program = compile(FibonacciBase.fibonacci25)
    val term = program.toUplc()
    // Write to scenarios/fibonacci/base/fibonacci.uplc
```

### Output Organization

**Pattern:** `scenarios/<scenario>/<mode>/fibonacci.uplc`

- `base/` - Base mode implementations (typically naive/unoptimized)
- `open/` - Open mode implementations (optimized)

### UPLC Compilation Process

1. Scala code annotated with `@Compile` is processed by the Scalus compiler plugin
2. Scalus transforms Scala AST to Plutus IR
3. Output is rendered as UPLC text and written to the scenarios directory
4. The compilation happens at Scala compile-time, producing a standalone UPLC program

**Key insight:** The `fibonacci25` function is fully applied at compile-time with `n = 25`, producing a UPLC program that computes the result when executed.

## Code Patterns

### Base vs Open Mode

**Base Mode:**
- Naive implementations matching mathematical definitions
- No optimizations
- Example: Naive recursive fibonacci (exponential time)

**Open Mode:**
- Optimized implementations
- Tail recursion, iterative approaches
- Example: Iterative fibonacci with tail recursion (linear time)

### Adding New Scenarios

1. Create `src/<scenario>/<ScenarioName>Base.scala` and `src/<scenario>/<ScenarioName>Open.scala`
2. Add `@Compile` annotation to the object containing the logic
3. Create a `@main` function to compile and write UPLC
4. Update `build-scalus` command in `flake.nix` to include new main classes
5. Create output directories: `scenarios/<scenario>/base/` and `scenarios/<scenario>/open/`

## UPLC-CAPE Submission Format

Each scenario submission directory should contain:
- `fibonacci.uplc` - The compiled UPLC program
- `README.md` - Implementation description
- `metadata.json` - Compiler and implementation details (optional)
- `metrics.json` - Performance measurements (optional)
