# Scalus UPLC-CAPE Submissions

This repository contains [UPLC-CAPE](https://github.com/IntersectMBO/UPLC-CAPE) submissions implemented in [Scalus](https://scalus.org).

## Setup

This project uses Nix flakes for reproducible development environment:

```bash
# Allow direnv (one-time)
direnv allow

# Or manually enter nix shell
nix develop
```

## Project Structure

```
.
├── src/                    # Scala source code
│   └── fibonacci/         # Fibonacci scenario implementations
│       ├── FibonacciBase.scala  # Base mode (naive recursive)
│       └── FibonacciOpen.scala  # Open mode (optimized iterative)
├── scenarios/             # UPLC-CAPE submission outputs
│   └── fibonacci/         # Fibonacci submission directory
│       ├── base/          # Base mode submission
│       └── open/          # Open mode submission
└── build.sbt              # sbt build configuration
```

## Building

Compile all scenarios:

```bash
sbt compile
```

Generate UPLC programs:

```bash
# Build all scenarios (recommended)
build-scalus

# Or individually:
# Base mode (naive recursive)
sbt "runMain fibonacci.compileFibonacciBase"

# Open mode (optimized iterative)
sbt "runMain fibonacci.compileFibonacciOpen"
```

## Scenarios

### Fibonacci

Computes `fibonacci(25) = 75025` in two modes:

#### Base Mode

Naive recursive implementation matching mathematical definition.

**Build:** `sbt "runMain fibonacci.compileFibonacciBase"`

**Output:** `scenarios/fibonacci/base/fibonacci.uplc`

#### Open Mode

Optimized iterative implementation with tail recursion.

**Build:** `sbt "runMain fibonacci.compileFibonacciOpen"`

**Output:** `scenarios/fibonacci/open/fibonacci.uplc`

## UPLC-CAPE Submission Format

Each scenario directory should contain:

- `*.uplc` - Compiled UPLC program
- `metadata.json` - Compiler and implementation details
- `metrics.json` - Performance measurements
- `README.md` - Implementation description
