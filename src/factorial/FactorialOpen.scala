package factorial

import scalus.*
import scalus.uplc.DefaultFun.*
import scalus.uplc.Term
import scalus.uplc.Term.*
import scalus.uplc.transform.CaseConstrApply

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

/** UPLC-CAPE Factorial Open Mode Implementation
  *
  * Open mode uses manual UPLC construction and Case/Constr optimization.
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

@main def compileFactorialOpen(): Unit =
  val optimized = CaseConstrApply(versionedFactorialTerm)
  val program = optimized.plutusV3

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
