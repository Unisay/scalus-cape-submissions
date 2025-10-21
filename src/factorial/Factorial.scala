package factorial

import scalus.*
import scalus.uplc.DefaultFun.*
import scalus.uplc.Term
import scalus.uplc.Term.*
import scalus.uplc.transform.CaseConstrApply

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

/** UPLC-CAPE Factorial Scenario
  *
  * Optimized implementation using manual UPLC construction and Case/Constr optimization.
  * Generates a lambda function that accepts n as a parameter.
  */
// Versioned factorial term for direct evaluation
def versionedFactorialTerm: Term =
  import scalus.uplc.TermDSL.given
  // pfix' implementation
  def pfix(f: Term => Term) = λλ("r") { r => r $ r } $ λλ("r") { r => f(r $ r) }

  // pfactorial using pfix'
  val factorial = pfix: r =>
    λλ("x"): x =>
      !(!IfThenElse $ (LessThanEqualsInteger $ x $ 0) $
        ~1.asTerm $
        ~(MultiplyInteger $ x $ (r $ (SubtractInteger $ x $ 1))))

  factorial

@main def compileFactorial(): Unit =
  val optimized = CaseConstrApply(versionedFactorialTerm)
  val program = optimized.plutusV3

  // Write to submissions/factorial/factorial.uplc file
  val uplcText = program.pretty
    .render(80)
    .replace(".", "_") // Sanitize all dots to underscores
    .replace("$", "_") // Sanitize dollar signs to underscores
    .replace("1_1_0", "1.1.0") // Restore version number
  val outputPath = Paths.get("submissions/factorial/factorial.uplc")
  Files.createDirectories(outputPath.getParent)
  Files.write(outputPath, uplcText.getBytes(StandardCharsets.UTF_8))

  println(s"✓ Successfully compiled Factorial to factorial.uplc")
  println(s"  Output: ${outputPath.toAbsolutePath}")
  println(s"  Size: ${uplcText.length} bytes")
  println(s"  Scenario: factorial")
