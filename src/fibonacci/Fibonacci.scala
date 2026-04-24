package fibonacci

import common.Util
import scalus.*
import scalus.uplc.DefaultFun.*
import scalus.uplc.Term
import scalus.uplc.Term.*
import scalus.uplc.TermDSL.given
import scalus.uplc.eval.PlutusVM
import scalus.uplc.transform.CaseConstrApply

/** UPLC-CAPE Fibonacci Scenario
  *
  * Optimized implementation using manual UPLC construction with tail recursion.
  * Generates a lambda function that accepts n as a parameter.
  */

extension (t: Term)
    infix def -(other: Term): Term = SubtractInteger $ t $ other
    infix def +(other: Term): Term = AddInteger $ t $ other
    infix def <=(other: Term): Term = LessThanEqualsInteger $ t $ other

val versionedFibonacciTerm: Term =
    // pfix' implementation
    def pfix(f: Term => Term) = Term.λ { r => r $ r } $ Term.λ { r => f(r $ r) }

    val fib = pfix: r =>
        Term.λ: x =>
            !(!IfThenElse $ x <= 1 $
                ~x $
                ~((r $ x - 1) + (r $ x - 2)))

    fib

@main def compileFibonacci(): Unit =
    val optimized = CaseConstrApply(versionedFibonacciTerm)
    val program = common.Renamer.rename(optimized.plutusV3)

    given PlutusVM = PlutusVM.makePlutusV3VM()
    Util.assertEvaluatesTo(program, input = 10, expected = 55)
    Util.writeUplc("fibonacci", "fibonacci.uplc", program.pretty.render(80))
