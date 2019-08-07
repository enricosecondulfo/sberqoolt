package it.github.enricosecondulfo.sberqoolt

import org.springframework.data.mongodb.core.query.Criteria

class CriteriaBuilder {

    private var sortOperation: SortOperation? = null

    fun build(expression: Expression): Criteria? {
        return retrieveOperation(expression)
    }

    fun retrieveOperation(expression: Expression): Criteria? {
        return when (expression) {
            is LogicalOperation -> retrieveLogicalOperation(expression)
            is ComparisonOperation -> retrieveComparisonOperation(expression)
            else -> null
        }
    }

    fun retrieveLogicalOperation(operation: LogicalOperation): Criteria? {
        val leftExpression = operation.left
        val rightExpression = operation.right

        if (leftExpression is SortOperation && rightExpression is SortOperation) {
            return null

        } else if (leftExpression is SortOperation && rightExpression !is SortOperation) {
            this.sortOperation = leftExpression
            return retrieveOperation(rightExpression)

        } else if (leftExpression !is SortOperation && rightExpression is SortOperation) {
            this.sortOperation = rightExpression
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

    fun retrieveComparisonOperation(operation: ComparisonOperation): Criteria? {
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
}