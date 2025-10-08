package factorial

import scalus.*
import scalus.Compiler.compile
import java.nio.file.{Files, Paths}
import java.nio.charset.StandardCharsets

/** UPLC-CAPE Factorial Base Mode Implementation
  *
  * Base mode uses naive recursive implementation matching the mathematical definition.
  * Generates a lambda function that accepts n as a parameter.
  */
@Compile
object FactorialBase:

  /** Naive recursive implementation */
  def factorial(n: BigInt): BigInt =
    if n <= 0 then BigInt(1)
    else n * factorial(n - 1)

@main def compileFactorialBase(): Unit =
  // Compile the parameterized factorial function to UPLC Program
  val program = compile(FactorialBase.factorial).toUplcOptimized().plutusV3

  // Write to submissions/factorial/base/factorial.uplc file
  val uplcText = program.pretty
    .render(80)
    .replace(".", "_") // Sanitize all dots to underscores
    .replace("$", "_") // Sanitize dollar signs to underscores
    .replace("1_1_0", "1.1.0") // Restore version number
  val outputPath = Paths.get("submissions/factorial/base/factorial.uplc")
  Files.createDirectories(outputPath.getParent)
  Files.write(outputPath, uplcText.getBytes(StandardCharsets.UTF_8))

  println(s"✓ Successfully compiled FactorialBase to factorial.uplc")
  println(s"  Output: ${outputPath.toAbsolutePath}")
  println(s"  Size: ${uplcText.length} bytes")
  println(s"  Mode: Base (naive recursive)")
