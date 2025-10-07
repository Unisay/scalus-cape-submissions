package factorial

import scalus.*
import scalus.Compiler.compile
import java.nio.file.{Files, Paths}
import java.nio.charset.StandardCharsets

/** UPLC-CAPE Factorial Open Mode Implementation
  *
  * Open mode uses optimized iterative implementation with tail recursion.
  * Generates a lambda function that accepts n as a parameter.
  */
@Compile
object FactorialOpen:

  /** Iterative implementation with tail recursion */
  def factorial(n: BigInt): BigInt =
    if n <= 0 then BigInt(1)
    else
      def factIter(acc: BigInt, count: BigInt): BigInt =
        if count <= 0 then acc else factIter(acc * count, count - 1)
      factIter(BigInt(1), n)

@main def compileFactorialOpen(): Unit =
  // Compile the parameterized factorial function to UPLC Program
  val program = compile(FactorialOpen.factorial).toUplc().plutusV3

  // Write to submissions/factorial/open/factorial.uplc file
  val uplcText = program.pretty
    .render(80)
    .replace(".", "_") // Sanitize all dots to underscores
    .replace("$", "_") // Sanitize dollar signs to underscores
    .replace("1_1_0", "1.1.0") // Restore version number
  val outputPath = Paths.get("submissions/factorial/open/factorial.uplc")
  Files.createDirectories(outputPath.getParent)
  Files.write(outputPath, uplcText.getBytes(StandardCharsets.UTF_8))

  println(s"✓ Successfully compiled FactorialOpen to factorial.uplc")
  println(s"  Output: ${outputPath.toAbsolutePath}")
  println(s"  Size: ${uplcText.length} bytes")
  println(s"  Mode: Open (iterative with tail recursion)")
