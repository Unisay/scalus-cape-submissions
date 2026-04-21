package fibonacci_naive_recursion

import common.Util
import scalus.*
import scalus.Compiler.compile
import scalus.uplc.Term.asTerm
import scalus.uplc.eval.*

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

    given PlutusVM = PlutusVM.makePlutusV3VM()
    Util.assertEvaluatesTo(program, input = 10, expected = 55)
    Util.writeUplc("fibonacci_naive_recursion", "fibonacci.uplc", program.pretty.render(80))
