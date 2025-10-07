package fibonacci

import scalus.*
import scalus.Compiler.compile
import java.nio.file.{Files, Paths}
import java.nio.charset.StandardCharsets

/** UPLC-CAPE Fibonacci Base Mode Implementation
  *
  * Base mode uses naive recursive implementation matching the mathematical definition.
  * This computes fibonacci(25) = 75025 as required by the UPLC-CAPE benchmark.
  */
@Compile
object FibonacciBase:

    /** Naive recursive implementation */
    def fibonacci(n: BigInt): BigInt =
        if n <= 1 then
            if n == BigInt(0) then BigInt(0) else BigInt(1)
        else if n == BigInt(2) then BigInt(1)
        else fibonacci(n - 1) + fibonacci(n - 2)

    def fibonacci25: BigInt = fibonacci(BigInt(25))

@main def compileFibonacciBase(): Unit =
    // Compile the FibonacciBase.fibonacci25 function to UPLC Program
    val program = compile(FibonacciBase.fibonacci25).toUplc().plutusV3

    // Write to scenarios/fibonacci/base/fibonacci.uplc file
    val uplcText = program.pretty.render(80)
    val outputPath = Paths.get("scenarios/fibonacci/base/fibonacci.uplc")
    Files.createDirectories(outputPath.getParent)
    Files.write(outputPath, uplcText.getBytes(StandardCharsets.UTF_8))

    println(s"✓ Successfully compiled FibonacciBase to fibonacci.uplc")
    println(s"  Output: ${outputPath.toAbsolutePath}")
    println(s"  Size: ${uplcText.length} bytes")
    println(s"  Mode: Base (naive recursive)")
