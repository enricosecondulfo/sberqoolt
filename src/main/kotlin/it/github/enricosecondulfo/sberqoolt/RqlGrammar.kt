package it.github.enricosecondulfo.sberqoolt

import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parser
import com.github.h0tk3y.betterParse.lexer.TokenMatch
import com.github.h0tk3y.betterParse.parser.Parser
import com.github.h0tk3y.betterParse.utils.Tuple2

object RqlGrammar : Grammar<Expression>() {

    private val LPAR by token("\\(")
    private val RPAR by token("\\)")

    /* Logical Operators Token */
    private val AND by token("&")
    private val OR by token("\\|")

    /* Comparison Operators Token */
    private val EQ by token("eq")
    private val NE by token("ne")
    private val LT by token("lt\\b")
    private val LTE by token("lte\\b")
    private val GT by token("gt\\b")
    private val GTE by token("gte\\b")
    private val LIKE by token("like")
    private val IN by token("in")
    private val FULL by token("full")

    /* Pagination Operators Token */
    private val LIMIT by token("limit")
    private val SORT by token("sort")

    private val MOD by token("%")
    private val MINUS by token("-")
    private val PLUS by token("\\+")

    private val COMMA by token(",")
    private val ID by token("[A-Za-z]\\w*")
    private val NUMBER by token("\\d+")

    private val variable by ID use { Variable(text) }
    private val numConst by NUMBER use { Const(text.toInt()) }

    private val propertyValue = variable or numConst

    private val comparisonOperatorsToken = EQ or NE or LT or LTE or GT or GTE or IN

    private val comparisonSignToKind = mapOf(
            EQ to Eq,
            NE to Ne,
            LT to Lt,
            LTE to Lte,
            GT to Gt,
            GTE to Gte
    )

    private val partialComparisonOperationsParser: Parser<Expression>
            by (comparisonOperatorsToken * -LPAR * parser(this::variable) * COMMA * parser(this::propertyValue) * -RPAR)
                    .map { (op, property, _, value) ->
                        when (comparisonSignToKind[op.type]) {
                            is Eq -> EqualOperation(property, value)
                            is Ne -> NotEqualOperation(property, value)
                            is Lt -> LessThanOperation(property, value)
                            is Lte -> LessThanOrEqualsOperation(property, value)
                            is Gt -> GreaterThanOperation(property, value)
                            is Gte -> GreaterThanOrEqualOperation(property, value)
                            else -> EqualOperation(property, value)
                        }
                    }


    private val likeOperationParser: Parser<Expression>
            by (LIKE * -LPAR * parser(this::variable) * COMMA * optional(MOD) * parser(this::propertyValue) * optional(MOD) * -RPAR)
                    .map { (_, property, _, start, value, end) -> println(start); LikeOperation(property, value, likePosition(start, end)) }

    private val fullOperationParser: Parser<Expression>
            by (FULL * -LPAR * parser(this::propertyValue) * -RPAR)
                    .map { (_, value) -> FullOperation(value = value) }

    private val comparisonOperationsParser = partialComparisonOperationsParser or likeOperationParser or fullOperationParser

    private val sortOperatorsToken = PLUS or MINUS
    private val sortOperationParser: Parser<Expression>
            by (SORT * -LPAR * separatedTerms(sortOperatorsToken * parser(this::variable), COMMA, acceptZero = false) * -RPAR)
                    .map { (_, args) -> args.map { println(it.t1.type) }; SortOperation(sortParts(args)) }

    private val term: Parser<Expression> by
    (ID map { Variable(it.text) }) or
            (NUMBER map { Const(it.text.toInt()) }) or
            comparisonOperationsParser or
            sortOperationParser

    private val andChain by leftAssociative(term, AND) { left, _, right -> AndOperation(left, right) }
    private val orChain by leftAssociative(andChain, OR) { left, _, right -> OrOperation(left, right) }

    private fun likePosition(start: TokenMatch?, end: TokenMatch?): LikePosition {
        return when {
            start != null && end == null -> LikePosition.START
            start != null && end != null -> LikePosition.CONTAINS
            else -> LikePosition.END
        }
    }

    private fun sortParts(result: List<Tuple2<TokenMatch, Variable>>): List<SortPart> {
        return result.map { t ->
            SortPart(t.t2, if (t.t1.type == PLUS) SortType.ASCENDING else SortType.DESCENDING)
        }
    }

    override val rootParser = orChain
}


