package common

import scalus.uplc.{NamedDeBruijn, Program, Term}
import scalus.uplc.Term.*

object Renamer:

    /** Alpha-rename every bound/used name in `program.term` to a short
      * alphabetic identifier (a, b, …, z, aa, ab, …, zz, aaa, …). Purely
      * alphabetic — guaranteed valid as a plutus-core unquoted identifier
      * (no `-`, no digits, no interaction with the name-unique parser split).
      *
      * Scalus's original names are already globally unique, so a single
      * flat oldName → newName table suffices (no scoping/capture concerns).
      */
    def rename(program: Program): Program =
        val mapping = buildMapping(program.term)
        program.copy(term = rewrite(mapping, program.term))

    private def buildMapping(root: Term): Map[String, String] =
        val seen = scala.collection.mutable.LinkedHashSet.empty[String]
        def walk(t: Term): Unit = t match
            case Var(NamedDeBruijn(n, _), _) => seen += n
            case LamAbs(n, body, _)          => seen += n; walk(body)
            case Apply(f, a, _)              => walk(f); walk(a)
            case Force(x, _)                 => walk(x)
            case Delay(x, _)                 => walk(x)
            case Constr(_, args, _)          => args.foreach(walk)
            case Case(scrut, branches, _)    => walk(scrut); branches.foreach(walk)
            case _                           => () // Const, Builtin, Error
        walk(root)
        seen.iterator.zip(freshNames).toMap

    private def rewrite(m: Map[String, String], t: Term): Term = t match
        case Var(NamedDeBruijn(n, i), ann) => Var(NamedDeBruijn(m.getOrElse(n, n), i), ann)
        case LamAbs(n, body, ann)          => LamAbs(m.getOrElse(n, n), rewrite(m, body), ann)
        case Apply(f, a, ann)              => Apply(rewrite(m, f), rewrite(m, a), ann)
        case Force(x, ann)                 => Force(rewrite(m, x), ann)
        case Delay(x, ann)                 => Delay(rewrite(m, x), ann)
        case Constr(tag, args, ann)        => Constr(tag, args.map(rewrite(m, _)), ann)
        case Case(scrut, bs, ann)          => Case(rewrite(m, scrut), bs.map(rewrite(m, _)), ann)
        case leaf                          => leaf

    private def freshNames: Iterator[String] =
        def toLetters(n: Int): String =
            val sb = new StringBuilder
            var k = n
            while k > 0 do
                val d = (k - 1) % 26
                sb.append(('a' + d).toChar)
                k = (k - 1) / 26
            sb.reverse.toString
        Iterator.from(1).map(toLetters)
