package factorial_naive_recursion

import common.Util
import scalus.*
import scalus.cardano.ledger.MajorProtocolVersion
import scalus.compiler.{Compile, compile}
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

  val program = common.Renamer.rename(sir.toUplcOptimized(using Util.release)().plutusV3)
  locally:
    given PlutusVM = PlutusVM.makePlutusV3VM(MajorProtocolVersion.vanRossemPV)
    Util.assertEvaluatesTo(program, input = 10, expected = 3628800)
  Util.writeUplc("factorial_naive_recursion", "factorial.uplc", program.pretty.render(80))
