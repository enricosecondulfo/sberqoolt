package it.github.enricosecondulfo.sberqoolt

import it.github.enricosecondulfo.sberqoolt.dtos.Content
import it.github.enricosecondulfo.sberqoolt.utils.TypedAggregationUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.aggregation.Aggregation.*
import org.springframework.data.mongodb.core.aggregation.TypedAggregation
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.data.mongodb.core.query.ne

class PaginationOperationsTests {

    private val aggregationBuilder = AggregationBuilder()

    @Test
    fun `test sort operation`() {
        val expression = "eq(name,c)&sort(+name,-description)"

        val sortOrders = arrayListOf(
            Sort.Order.asc("name"),
            Sort.Order.desc("description")
        )

        val aggregation: TypedAggregation<Content> = aggregationBuilder.build(expression)
        val expectedAggregation = TypedAggregationUtils.build<Content>(
            maxElements = 100L,
            sort = Sort.by(sortOrders),
            aggregations = *arrayOf(match(Content::name isEqualTo "c"))
        )

        Assertions.assertEquals(aggregation.toString(), expectedAggregation.toString())
    }

    @Test
    fun `test limit operation`() {
        val expression = "eq(name,c)&limit(10)"

        val aggregation: TypedAggregation<Content> = aggregationBuilder.build(expression)
        val expectedAggregation = TypedAggregationUtils.build<Content>(
            maxElements = 10L,
            aggregations = *arrayOf(match(Content::name isEqualTo "c"))
        )

        Assertions.assertEquals(aggregation.toString(), expectedAggregation.toString())
    }

    @Test
    fun `test skip operation`() {
        val expression = "eq(name,c)&skip(10)"

        val aggregation: TypedAggregation<Content> = aggregationBuilder.build(expression)
        val expectedAggregation = TypedAggregationUtils.build<Content>(
            elementsToSkip = 10L,
            aggregations = * arrayOf(match(Content::name isEqualTo "c"))
        )

        Assertions.assertEquals(aggregation.toString(), expectedAggregation.toString())
    }

    @Test
    fun `test limit, skip and sort operations`() {
        val expression = "eq(name,c)&limit(200)&skip(10)&sort(+name)"

        val aggregation: TypedAggregation<Content> = aggregationBuilder.build(expression)
        val expectedAggregation = TypedAggregationUtils.build<Content>(
            maxElements = 200L,
            elementsToSkip = 10L,
            sort = Sort.by(Sort.Order.asc("name")),
            aggregations = *arrayOf(match(Content::name isEqualTo "c"))
        )

        Assertions.assertEquals(aggregation.toString(), expectedAggregation.toString())
    }


}