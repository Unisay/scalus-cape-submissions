package factorial_naive_recursion

import common.Util
import scalus.*
import scalus.cardano.ledger.MajorProtocolVersion
import scalus.compiler.{Compile, compile, Options}
import scalus.uplc.eval.PlutusVM

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
  val sir = compile(FactorialNaiveRecursion.factorial)

  val program = common.Renamer.rename(sir.toUplcOptimized(using Options.release)().plutusV3)
  locally:
    given PlutusVM = PlutusVM.makePlutusV3VM()
    Util.assertEvaluatesTo(program, input = 10, expected = 3628800)
  Util.writeUplc("factorial_naive_recursion", "factorial.uplc", program.pretty.render(80))

  // vanRossem preview build (case-on-builtins, batch6, dropList)
  val vanRossem = Options.release.copy(targetProtocolVersion = MajorProtocolVersion.vanRossemPV)
  val programVR = common.Renamer.rename(sir.toUplcOptimized(using vanRossem)().plutusV3)
  locally:
    given PlutusVM = PlutusVM.makePlutusV3VM(MajorProtocolVersion.vanRossemPV)
    Util.assertEvaluatesTo(programVR, input = 10, expected = 3628800)
  Util.writeUplc(
    "factorial_naive_recursion",
    "factorial-preview.uplc",
    programVR.pretty.render(80)
  )
