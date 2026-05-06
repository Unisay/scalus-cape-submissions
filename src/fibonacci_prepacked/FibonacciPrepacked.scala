package fibonacci_prepacked

import common.Util
import scalus.*
import scalus.cardano.ledger.MajorProtocolVersion
import scalus.compiler.compile
import scalus.compiler.Options
import scalus.uplc.builtin.Builtins.*
import scalus.uplc.builtin.ByteString
import scalus.uplc.builtin.ByteString.given
import scalus.uplc.*
import scalus.uplc.Term.asTerm
import scalus.uplc.eval.*
import scalus.uplc.transform.{CaseConstrApply, Inliner}

import scala.annotation.tailrec

/** UPLC-CAPE Fibonacci Prepacked Scenario
  *
  * Optimized implementation using pre-computed Fibonacci numbers stored in a ByteString.
  * Based on Plutarch implementation by SeungheonOh.
  * Generates a lambda function that accepts n as a parameter.
  *
  * Performance characteristics:
  * - O(1) constant-time lookup
  * - Pre-computed values for fib(0) through fib(25)
  * - Each Fibonacci number encoded as 3 bytes in big-endian format
  */

// Helper to generate Fibonacci sequence as ByteString
// Each Fibonacci number is encoded as 3 bytes in big-endian format
def fibSeqByteString(n: Int): ByteString = {
    @tailrec
    def fib(a: Int, b: Int, index: Int, acc: Array[Byte]): Array[Byte] =
        if index >= n then acc
        else
            val offset = index * 3
            acc(offset) = ((a >> 16) & 0xff).toByte
            acc(offset + 1) = ((a >> 8) & 0xff).toByte
            acc(offset + 2) = (a & 0xff).toByte
            fib(b, a + b, index + 1, acc)

    ByteString.fromArray(fib(0, 1, 0, new Array[Byte](n * 3)))
}

// Pre-computed Fibonacci sequence up to fib(25)
val packedFibonacci = fibSeqByteString(26)

@main def compileFibonacciPrepacked(): Unit =
  // Compile Scalus function to lookup Fibonacci number from packed ByteString
  val fib = compile: (packedFibonacci: ByteString) =>
      (x: BigInt) =>
          if x <= BigInt(0) then x
          else byteStringToInteger(true, sliceByteString(x * 3, 3, packedFibonacci))

  def buildProgram(opts: Options): Program =
      val term      = fib.toUplc(using opts)() $ packedFibonacci.asTerm
      val optimized = term |> Inliner.apply |> CaseConstrApply.apply
      common.Renamer.rename(optimized.plutusV3)

  val program = buildProgram(Options.release)
  locally:
      given PlutusVM = PlutusVM.makePlutusV3VM()
      Util.assertEvaluatesTo(program, input = 10, expected = 55)
  Util.writeUplc("fibonacci_prepacked", "fibonacci.uplc", program.pretty.render(80))

  // vanRossem preview build (case-on-builtins, batch6, dropList)
  val vanRossem = Options.release.copy(targetProtocolVersion = MajorProtocolVersion.vanRossemPV)
  val programVR = buildProgram(vanRossem)
  locally:
      given PlutusVM = PlutusVM.makePlutusV3VM(MajorProtocolVersion.vanRossemPV)
      Util.assertEvaluatesTo(programVR, input = 10, expected = 55)
  Util.writeUplc(
    "fibonacci_prepacked",
    "fibonacci-vanrossem.uplc",
    programVR.pretty.render(80)
  )
