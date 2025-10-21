# Factorial

**Scenario:** factorial
**Compiler:** Scalus 0.12.0

## Implementation

Optimized implementation using manual UPLC construction with pfix (Y-combinator):

```scala
val factorial = pfix: r =>
    λλ("x"): x =>
        !(!IfThenElse $ (LessThanEqualsInteger $ x $ 0) $
            ~1.asTerm $
            ~(MultiplyInteger $ x $ (r $ (SubtractInteger $ x $ 1))))
```

## Characteristics

- **Algorithm:** Manual UPLC construction with Y-combinator
- **Optimization:** Direct UPLC term construction, Case/Constr optimization pass
- **Output:** Parameterized lambda function accepting n as input

## Build

```bash
sbt "runMain factorial.compileFactorial"
```

## Output

`submissions/factorial/factorial.uplc`

## Notes

This implementation uses manual UPLC term construction for fine-grained control over the generated code. The `pfix` combinator enables recursion in the lambda calculus. The `CaseConstrApply` optimization pass further improves the generated UPLC.
