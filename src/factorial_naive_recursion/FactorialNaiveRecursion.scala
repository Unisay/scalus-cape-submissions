package factorial_naive_recursion

import scalus.*
import scalus.Compiler.compile
import java.nio.file.{Files, Paths}
import java.nio.charset.StandardCharsets

/** UPLC-CAPE Factorial Naive Recursion Scenario
  *
  * Naive recursive implementation matching the mathematical definition.
  * Generates a lambda function that accepts n as a parameter.
  */
@Compile
object FactorialNaiveRecursion:

  /** Naive recursive implementation */
  def factorial(n: BigInt): BigInt =
    if n <= 0 then BigInt(1)
    else n * factorial(n - 1)

@main def compileFactorialNaiveRecursion(): Unit =
  // Compile the parameterized factorial function to UPLC Program
  val program = compile(FactorialNaiveRecursion.factorial).toUplcOptimized().plutusV3

  // Write to submissions/factorial_naive_recursion/factorial.uplc file
  val uplcText = program.pretty
    .render(80)
    .replace(".", "_") // Sanitize all dots to underscores
    .replace("$", "_") // Sanitize dollar signs to underscores
    .replace("1_1_0", "1.1.0") // Restore version number
  val outputPath = Paths.get("submissions/factorial_naive_recursion/factorial.uplc")
  Files.createDirectories(outputPath.getParent)
  Files.write(outputPath, uplcText.getBytes(StandardCharsets.UTF_8))

  println(s"✓ Successfully compiled FactorialNaiveRecursion to factorial.uplc")
  println(s"  Output: ${outputPath.toAbsolutePath}")
  println(s"  Size: ${uplcText.length} bytes")
  println(s"  Scenario: factorial_naive_recursion")
