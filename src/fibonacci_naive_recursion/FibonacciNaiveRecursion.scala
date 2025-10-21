package fibonacci_naive_recursion

import scalus.*
import scalus.Compiler.compile
import java.nio.file.{Files, Paths}
import java.nio.charset.StandardCharsets

/** UPLC-CAPE Fibonacci Naive Recursion Scenario
  *
  * Naive recursive implementation matching the mathematical definition.
  * Generates a lambda function that accepts n as a parameter.
  */
@Compile
object FibonacciNaiveRecursion:

    /** Naive recursive implementation */
    def fibonacci(n: BigInt): BigInt =
        if n <= 1 then n
        else if n == BigInt(2) then BigInt(1)
        else fibonacci(n - 1) + fibonacci(n - 2)

@main def compileFibonacciNaiveRecursion(): Unit =
    // Compile the parameterized fibonacci function to UPLC Program
    val program = compile(FibonacciNaiveRecursion.fibonacci).toUplcOptimized().plutusV3

    // Write to submissions/fibonacci_naive_recursion/fibonacci.uplc file
    val uplcText = program.pretty
      .render(80)
      .replace(".", "_") // Sanitize all dots to underscores
      .replace("$", "_") // Sanitize dollar signs to underscores
      .replace("1_1_0", "1.1.0") // Restore version number
    val outputPath = Paths.get("submissions/fibonacci_naive_recursion/fibonacci.uplc")
    Files.createDirectories(outputPath.getParent)
    Files.write(outputPath, uplcText.getBytes(StandardCharsets.UTF_8))

    println(s"✓ Successfully compiled FibonacciNaiveRecursion to fibonacci.uplc")
    println(s"  Output: ${outputPath.toAbsolutePath}")
    println(s"  Size: ${uplcText.length} bytes")
    println(s"  Scenario: fibonacci_naive_recursion")
