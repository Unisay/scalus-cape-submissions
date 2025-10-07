# Fibonacci Open Mode

**Mode:** Open (optimized iterative)
**Result:** fibonacci(25) = 75025

## Implementation

Iterative implementation with tail recursion:

```scala
def fibonacci(n: BigInt): BigInt =
    if n <= 1 then
        if n == BigInt(0) then BigInt(0) else BigInt(1)
    else if n == BigInt(2) then BigInt(1)
    else
        def fibIter(a: BigInt, b: BigInt, count: BigInt): BigInt =
            if count <= BigInt(2) then b else fibIter(b, a + b, count - 1)
        fibIter(BigInt(1), BigInt(1), n)
```

## Characteristics

- **Algorithm:** Iterative with tail recursion (linear time complexity)
- **Optimization:** Space and time efficient
- **Compilation:** Fully applied with n = 25

## Build

```bash
sbt "runMain fibonacci.compileFibonacciOpen"
```

## Output

`scenarios/fibonacci/open/fibonacci.uplc`
