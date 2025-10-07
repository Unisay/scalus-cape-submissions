package fibonacci

import scalus.*
import scalus.Compiler.compile
import java.nio.file.{Files, Paths}
import java.nio.charset.StandardCharsets

/** UPLC-CAPE Fibonacci Open Mode Implementation
  *
  * Open mode uses optimized iterative implementation with tail recursion.
  * This computes fibonacci(25) = 75025 as required by the UPLC-CAPE benchmark.
  */
@Compile
object FibonacciOpen:

    /** Iterative implementation with tail recursion */
    def fibonacci(n: BigInt): BigInt =
        if n <= 1 then
            if n == BigInt(0) then BigInt(0) else BigInt(1)
        else if n == BigInt(2) then BigInt(1)
        else
            def fibIter(a: BigInt, b: BigInt, count: BigInt): BigInt =
                if count <= BigInt(2) then b else fibIter(b, a + b, count - 1)
            fibIter(BigInt(1), BigInt(1), n)

    def fibonacci25: BigInt = fibonacci(BigInt(25))

@main def compileFibonacciOpen(): Unit =
    // Compile the FibonacciOpen.fibonacci25 function to UPLC
    val program = compile(FibonacciOpen.fibonacci25)
    val term = program.toUplc()

    // Write to scenarios/fibonacci/open/fibonacci.uplc file
    val uplcText = term.pretty.render(80)
    val outputPath = Paths.get("scenarios/fibonacci/open/fibonacci.uplc")
    Files.createDirectories(outputPath.getParent)
    Files.write(outputPath, uplcText.getBytes(StandardCharsets.UTF_8))

    println(s"✓ Successfully compiled FibonacciOpen to fibonacci.uplc")
    println(s"  Output: ${outputPath.toAbsolutePath}")
    println(s"  Size: ${uplcText.length} bytes")
    println(s"  Mode: Open (iterative with tail recursion)")
