package it.github.enricosecondulfo.sberqoolt

import com.github.h0tk3y.betterParse.grammar.tryParseToEnd
import com.github.h0tk3y.betterParse.parser.ErrorResult
import com.github.h0tk3y.betterParse.parser.Parsed
import com.github.h0tk3y.betterParse.st.SyntaxTree
import com.github.h0tk3y.betterParse.st.liftToSyntaxTreeGrammar
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.aggregation.Aggregation.*
import org.springframework.data.mongodb.core.aggregation.TypedAggregation
import org.springframework.data.mongodb.core.query.Criteria

class AggregationBuilder(
    private var maxElements: Long? = 100L,
    private var sort: Sort? = Sort.by(Sort.Order.asc("_id"))
) {

    private var elementsToSkip: Long? = 0L

    fun <D> build(expression: String, type: Class<D>): TypedAggregation<D> =
        TypedAggregation(
            type,
            match(parseExpression(expression)!!),
            limit(maxElements!!),
            skip(elementsToSkip!!),
            sort(sort!!)
        )

    private fun parseExpression(expression: String): Criteria? {
        val result = RqlGrammar
            .liftToSyntaxTreeGrammar()
            .tryParseToEnd(expression)

        return when (result) {
            is ErrorResult -> throw ParseException("$result")
            is Parsed<SyntaxTree<Expression>> -> retrieveOperation(result.value.item)
            else -> throw ParseException("unknown error")
        }
    }

    private fun retrieveOperation(expression: Expression): Criteria? {
        return when (expression) {
            is LogicalOperation -> retrieveLogicalOperation(expression)
            is ComparisonOperation -> retrieveComparisonOperation(expression)
            else -> null
        }
    }

    private fun retrieveLogicalOperation(operation: LogicalOperation): Criteria? {
        val leftExpression = operation.left
        val rightExpression = operation.right

        if (leftExpression is PaginationOperation && rightExpression is PaginationOperation) {
            return null

        } else if (leftExpression is PaginationOperation && rightExpression !is PaginationOperation) {
            buildPaginationOperation(leftExpression)
            return retrieveOperation(rightExpression)

        } else if (leftExpression !is SortOperation && rightExpression is PaginationOperation) {
            buildPaginationOperation(rightExpression)
            return retrieveOperation(leftExpression)

        } else {
            return when (operation.kind) {
                is And -> Criteria().andOperator(
                    retrieveOperation(leftExpression),
                    retrieveOperation(rightExpression)
                )
                is Or -> Criteria().orOperator(
                    retrieveOperation(leftExpression),
                    retrieveOperation(rightExpression)
                )
                else -> null
            }
        }
    }

    private fun retrieveComparisonOperation(operation: ComparisonOperation): Criteria? {
        val property = retrieveVariableOrConst(operation.property!!)
        val value = retrieveVariableOrConst(operation.value)

        return when (operation) {
            is EqualOperation -> Criteria.where(property).`is`(value)
            is NotEqualOperation -> Criteria.where(property).ne(value)
            is LessThanOperation -> Criteria.where(property).lt(value)
            is LessThanOrEqualsOperation -> Criteria.where(property).lte(value)
            is GreaterThanOperation -> Criteria.where(property).gt(value)
            is GreaterThanOrEqualOperation -> Criteria.where(property).gte(value)
            is LikeOperation -> Criteria.where(property).regex(buildLike(value, operation.position))
            else -> null
        }
    }

    private fun buildLike(value: String, likePosition: LikePosition): String {
        return when (likePosition) {
            LikePosition.START -> "/$value$/"
            LikePosition.CONTAINS -> "/$value/"
            LikePosition.END -> "/^$value/"
        }
    }

    private fun retrieveVariableOrConst(expression: Expression): String {
        return when (expression) {
            is Variable -> expression.value
            is Const -> expression.value.toString()
            else -> "" // Error
        }
    }

    private fun buildSort(sortOperation: SortOperation? = null): Sort? {
        return sortOperation?.let {
            val sortOrders = sortOperation.parts.map { part ->
                val property = part.property as Variable
                if (part.type == SortType.ASCENDING) Sort.Order.asc(property.value) else Sort.Order.desc(property.value)
            }

            Sort.by(sortOrders)
        }
    }

    private fun buildPaginationOperation(operation: Expression) {
        when (operation) {
            is LimitOperation -> this.maxElements = operation.maxElements
            is SkipOperation -> this.elementsToSkip = operation.elementsToSkip
            is SortOperation -> this.sort = buildSort(operation)
        }
    }
}

inline fun <reified D : Any> AggregationBuilder.build(expression: String): TypedAggregation<D> =
    build(expression, D::class.java)