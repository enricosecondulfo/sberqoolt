package it.github.enricosecondulfo.sberqoolt

sealed class Expression

sealed class OperationKind
sealed class Operation(val kind: OperationKind) : Expression()

data class Const(val value: Int) : Expression()
data class Variable(val value: String) : Expression()

/* Logical operators */
sealed class LogicalOperation(kind: OperationKind) : Operation(kind) {
    abstract val left: Expression
    abstract val right: Expression
}

object And : OperationKind()
object Or : OperationKind()

data class AndOperation(
        override val left: Expression,
        override val right: Expression
) : LogicalOperation(And)

data class OrOperation(
        override val left: Expression,
        override val right: Expression
) : LogicalOperation(Or)

/* Comparison operations */
sealed class ComparisonOperation(kind: OperationKind) : Operation(kind) {
    abstract val property: Expression?
    abstract val value: Expression
}

object Eq : OperationKind()
object Ne : OperationKind()
object Lt : OperationKind()
object Lte : OperationKind()
object Gt : OperationKind()
object Gte : OperationKind()
object Like : OperationKind()
object In : OperationKind()
object Full : OperationKind()

data class EqualOperation(
        override val property: Expression,
        override val value: Expression
) : ComparisonOperation(Eq)

data class NotEqualOperation(
        override val property: Expression,
        override val value: Expression
) : ComparisonOperation(Ne)

data class LessThanOperation(
        override val property: Expression,
        override val value: Expression
) : ComparisonOperation(Lt)

data class LessThanOrEqualsOperation(
        override val property: Expression,
        override val value: Expression
) : ComparisonOperation(Lte)

data class GreaterThanOperation(
        override val property: Expression,
        override val value: Expression
) : ComparisonOperation(Gt)

data class GreaterThanOrEqualOperation(
        override val property: Expression,
        override val value: Expression
) : ComparisonOperation(Gte)

enum class LikePosition {
    START,
    CONTAINS,
    END
}

data class LikeOperation(
        override val property: Expression,
        override val value: Expression,
        val position: LikePosition
) : ComparisonOperation(Like)

data class InOperation(
        override val property: Expression,
        override val value: Expression
) : ComparisonOperation(In)

data class FullOperation(
        override val property: Expression? = null,
        override val value: Expression
): ComparisonOperation(Full)

/* Pagination operations */
sealed class PaginationOperation(kind: OperationKind): Operation(kind)
object Limit: OperationKind()
object Sort: OperationKind()

enum class SortType {
    ASCENDING,
    DESCENDING
}

data class SortPart(val property: Expression, val type: SortType)

data class LimitOperation(val start: Int, val count: Int): PaginationOperation(Limit)
data class SortOperation(val parts: List<SortPart>): PaginationOperation(Sort)