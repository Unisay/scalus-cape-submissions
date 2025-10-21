# Factorial Naive Recursion

**Scenario:** factorial_naive_recursion
**Compiler:** Scalus 0.12.0

## Implementation

Naive recursive implementation matching the mathematical definition:

```scala
def factorial(n: BigInt): BigInt =
    if n <= 0 then BigInt(1)
    else n * factorial(n - 1)
```

## Characteristics

- **Algorithm:** Simple recursive (linear time complexity O(n))
- **Optimization:** None (naive implementation)
- **Output:** Parameterized lambda function accepting n as input

## Build

```bash
sbt "runMain factorial_naive_recursion.compileFactorialNaiveRecursion"
```

## Output

`src/factorial_naive_recursion/factorial.uplc`

## Notes

This implementation prioritizes simplicity and readability over performance, directly translating the mathematical definition into code. The linear recursion depth may cause stack issues for very large values of n.
