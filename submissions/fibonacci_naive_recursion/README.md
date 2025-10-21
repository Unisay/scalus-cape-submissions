# Fibonacci Naive Recursion

**Scenario:** fibonacci_naive_recursion
**Compiler:** Scalus 0.12.0

## Implementation

Naive recursive implementation matching the mathematical definition:

```scala
def fibonacci(n: BigInt): BigInt =
    if n <= 1 then n
    else if n == BigInt(2) then BigInt(1)
    else fibonacci(n - 1) + fibonacci(n - 2)
```

## Characteristics

- **Algorithm:** Simple recursive (exponential time complexity O(2^n))
- **Optimization:** None (naive implementation)
- **Output:** Parameterized lambda function accepting n as input

## Build

```bash
sbt "runMain fibonacci_naive_recursion.compileFibonacciNaiveRecursion"
```

## Output

`submissions/fibonacci_naive_recursion/fibonacci.uplc`

## Notes

This implementation prioritizes simplicity and readability over performance, directly translating the mathematical definition into code. The exponential time complexity makes it unsuitable for large values of n.
