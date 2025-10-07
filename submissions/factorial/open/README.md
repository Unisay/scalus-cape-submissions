# Factorial Open Mode

**Mode:** Open (optimized iterative)

## Implementation

Iterative implementation with tail recursion:

```scala
def factorial(n: BigInt): BigInt =
  if n <= 0 then BigInt(1)
  else
    def factIter(acc: BigInt, count: BigInt): BigInt =
      if count <= 0 then acc else factIter(acc * count, count - 1)
    factIter(BigInt(1), n)
```

## Characteristics

- **Algorithm:** Iterative with tail recursion (linear time complexity)
- **Optimization:** Space efficient through accumulator pattern
- **Edge case:** factorial(n) = 1 for n ≤ 0

## Build

```bash
sbt "runMain factorial.compileFactorialOpen"
```

## Output

`submissions/factorial/open/factorial.uplc`
