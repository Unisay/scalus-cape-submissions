package common

import scalus.uplc.{Constant, Program, Term}
import scalus.uplc.Term.asTerm
import scalus.uplc.eval.{PlutusVM, Result}

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

object Util:

    /** Write UPLC text to `src/<scenario>/<fileName>` and print a summary. */
    def writeUplc(scenario: String, fileName: String, uplcText: String): Unit =
        val outputPath = Paths.get(s"src/$scenario/$fileName")
        Files.createDirectories(outputPath.getParent)
        Files.write(outputPath, uplcText.getBytes(StandardCharsets.UTF_8))
        println(s"✓ Successfully compiled $scenario to $fileName")
        println(s"  Output: ${outputPath.toAbsolutePath}")
        println(s"  Size: ${uplcText.length} bytes")
        println(s"  Scenario: $scenario")

    /** Apply `program` to `input` and require the result equals `Const(Integer(expected))`.
      * Fails the process on evaluation error or mismatch.
      */
    def assertEvaluatesTo(program: Program, input: BigInt, expected: BigInt)(using PlutusVM): Unit =
        (program $ input.asTerm).term.evaluateDebug match
            case Result.Success(Term.Const(Constant.Integer(got), _), budget, _, _) =>
                if got != expected then
                    sys.error(s"assertion failed: f($input) = $got, expected $expected")
                println(s"✓ f($input) = $got  cpu=${budget.steps} mem=${budget.memory}")
            case Result.Success(term, _, _, _) =>
                sys.error(s"assertion failed: f($input) = $term, expected Const(Integer($expected))")
            case Result.Failure(ex, _, _, _) =>
                sys.error(s"assertion failed: f($input) threw $ex")
