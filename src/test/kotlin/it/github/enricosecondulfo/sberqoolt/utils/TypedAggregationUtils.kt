package it.github.enricosecondulfo.sberqoolt.utils

import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.aggregation.Aggregation.*
import org.springframework.data.mongodb.core.aggregation.AggregationOperation
import org.springframework.data.mongodb.core.aggregation.TypedAggregation

class TypedAggregationUtils {

    companion object {
        inline fun <reified I : Any> build(
            maxElements: Long = 100L,
            elementsToSkip: Long = 0L,
            sort: Sort = Sort.by(
                Sort.Order.asc("_id")
            ),
            vararg aggregations: AggregationOperation

        ): TypedAggregation<I> {
            val defaultAggregations = mutableListOf(
                limit(maxElements),
                skip(elementsToSkip),
                sort(sort)
            )

            return TypedAggregation(
                I::class.java,
                aggregations.toMutableList() + defaultAggregations
            )
        }
    }
}