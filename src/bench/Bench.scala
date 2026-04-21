package bench

import scalus.uplc.{Program, Term}
import scalus.uplc.Term.asTerm
import scalus.uplc.eval.*

import java.nio.file.{Files, Paths}

@main def benchUplc(path: String, input: Int): Unit =
    val text = new String(Files.readAllBytes(Paths.get(path)))
    val program = Program.parseUplc(text) match
        case Right(p) => p
        case Left(err) =>
            System.err.println(s"parse error in $path: $err")
            sys.exit(1)

    given PlutusVM = PlutusVM.makePlutusV3VM()
    val applied = program $ BigInt(input).asTerm
    applied.term.evaluateDebug match
        case Result.Success(term, budget, costs, logs) =>
            println(s"$path input=$input term=$term cpu=${budget.steps} mem=${budget.memory}")
        case Result.Failure(ex, budget, costs, logs) =>
            println(s"$path input=$input FAILED cpu=${budget.steps} mem=${budget.memory} err=$ex")
