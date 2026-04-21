# Fibonacci

**Scenario:** fibonacci

## Implementation

Optimized implementation using manual UPLC construction with pfix (Y-combinator):

```scala
val fib = pfix: r =>
    Term.λ: x =>
        !(!IfThenElse $ x <= 1 $
            ~x $
            ~((r $ x - 1) + (r $ x - 2)))
```

## Characteristics

- **Algorithm:** Manual UPLC construction with Y-combinator
- **Optimization:** Direct UPLC term construction, Case/Constr optimization pass
- **Output:** Parameterized lambda function accepting n as input

## Build

```bash
sbt "runMain fibonacci.compileFibonacci"
```

## Output

`src/fibonacci/fibonacci.uplc`

## Notes

This implementation uses manual UPLC term construction for fine-grained control over the generated code. The `pfix` combinator enables recursion in the lambda calculus. The `CaseConstrApply` optimization pass further improves the generated UPLC.
