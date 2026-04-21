package factorial

import common.Util
import scalus.*
import scalus.uplc.DefaultFun.*
import scalus.uplc.Term
import scalus.uplc.Term.*
import scalus.uplc.eval.PlutusVM
import scalus.uplc.transform.CaseConstrApply

/** UPLC-CAPE Factorial Scenario
  *
  * Optimized implementation using manual UPLC construction and Case/Constr optimization.
  * Generates a lambda function that accepts n as a parameter.
  */
// Versioned factorial term for direct evaluation
def versionedFactorialTerm: Term =
  import scalus.uplc.TermDSL.given
  // pfix' implementation
  def pfix(f: Term => Term) = Term.λ { r => r $ r } $ Term.λ { r => f(r $ r) }

  // pfactorial using pfix'
  val factorial = pfix: r =>
    Term.λ: x =>
      !(!IfThenElse $ (LessThanEqualsInteger $ x $ 0) $
        ~1.asTerm $
        ~(MultiplyInteger $ x $ (r $ (SubtractInteger $ x $ 1))))

  factorial

@main def compileFactorial(): Unit =
  val optimized = CaseConstrApply(versionedFactorialTerm)
  val program = optimized.plutusV3

  given PlutusVM = PlutusVM.makePlutusV3VM()
  Util.assertEvaluatesTo(program, input = 10, expected = 3628800)
  Util.writeUplc("factorial", "factorial.uplc", program.pretty.render(80))
