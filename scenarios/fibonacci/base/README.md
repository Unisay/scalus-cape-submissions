# Fibonacci Base Mode

**Mode:** Base (naive recursive)
**Result:** fibonacci(25) = 75025

## Implementation

Naive recursive implementation matching the mathematical definition:

```scala
def fibonacci(n: BigInt): BigInt =
    if n <= 1 then
        if n == BigInt(0) then BigInt(0) else BigInt(1)
    else if n == BigInt(2) then BigInt(1)
    else fibonacci(n - 1) + fibonacci(n - 2)
```

## Characteristics

- **Algorithm:** Simple recursive (exponential time complexity)
- **Optimization:** None (as prescribed by base mode)
- **Compilation:** Fully applied with n = 25

## Build

```bash
sbt "runMain fibonacci.compileFibonacciBase"
```

## Output

`scenarios/fibonacci/base/fibonacci.uplc`
