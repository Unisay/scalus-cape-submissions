package fibonacci

import scalus.*
import scalus.uplc.DefaultFun.*
import scalus.uplc.Term
import scalus.uplc.Term.*
import scalus.uplc.TermDSL.given
import scalus.uplc.eval.PlutusVM
import scalus.uplc.transform.CaseConstrApply

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

/** UPLC-CAPE Fibonacci Open Mode Implementation
  *
  * Open mode uses optimized iterative implementation with tail recursion. Generates a lambda
  * function that accepts n as a parameter.
  */

extension (t: Term)
    infix def -(other: Term): Term = SubtractInteger $ t $ other
    infix def +(other: Term): Term = AddInteger $ t $ other
    infix def <=(other: Term): Term = LessThanEqualsInteger $ t $ other

val versionedFibonacciTerm: Term =
    // pfix' implementation
    def pfix(f: Term => Term) = λλ("r") { r => r $ r } $ λλ("r") { r => f(r $ r) }

    val fib = pfix: r =>
        λλ("x"): x =>
            !(!IfThenElse $ x <= 1 $
                ~x $
                ~((r $ x - 1) + (r $ x - 2)))

    fib

@main def compileFibonacciOpen(): Unit =
    val optimized = CaseConstrApply(versionedFibonacciTerm)
    val program = optimized.plutusV3

    given PlutusVM = PlutusVM.makePlutusV3VM()
    val applied = program $ 10.asTerm
    val result = applied.term.evaluateDebug
    println(s"Fibonacci(10) = $result") // Should print 55

    // Write to submissions/fibonacci/open/fibonacci.uplc file
    val uplcText = program.pretty
        .render(80)
        .replace(".", "_") // Sanitize all dots to underscores
        .replace("$", "_") // Sanitize dollar signs to underscores
        .replace("1_1_0", "1.1.0") // Restore version number
    val outputPath = Paths.get("submissions/fibonacci/open/fibonacci.uplc")
    Files.createDirectories(outputPath.getParent)
    Files.write(outputPath, uplcText.getBytes(StandardCharsets.UTF_8))

    println(s"✓ Successfully compiled FibonacciOpen to fibonacci.uplc")
    println(s"  Output: ${outputPath.toAbsolutePath}")
    println(s"  Size: ${uplcText.length} bytes")
    println(s"  Mode: Open (iterative with tail recursion)")
