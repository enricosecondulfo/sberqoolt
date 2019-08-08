package it.github.enricosecondulfo.sberqoolt

import it.github.enricosecondulfo.sberqoolt.dtos.Content
import it.github.enricosecondulfo.sberqoolt.utils.TypedAggregationUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.data.mongodb.core.aggregation.Aggregation.match
import org.springframework.data.mongodb.core.aggregation.TypedAggregation
import org.springframework.data.mongodb.core.query.*

class ComparisonOperationsTests {

    private val aggregationBuilder = AggregationBuilder()

    @Test
    fun `test equals criteria`() {
        val expression = "eq(name,c)"

        val aggregation: TypedAggregation<Content> = aggregationBuilder.build(expression)
        val expectedAggregation = TypedAggregationUtils.build<Content>(
            aggregations = *arrayOf(match(Content::name isEqualTo "c"))
        )

        Assertions.assertEquals(aggregation.toString(), expectedAggregation.toString())
    }

    @Test
    fun `test not equals criteria`() {
        val expression = "ne(name,c)"

        val aggregation: TypedAggregation<Content> = aggregationBuilder.build(expression)
        val expectedAggregation = TypedAggregationUtils.build<Content>(
            aggregations = *arrayOf(match(Content::name ne "c"))
        )

        Assertions.assertEquals(aggregation.toString(), expectedAggregation.toString())
    }

    @Test
    fun `test less than criteria`() {
        val expression = "lt(name,20)"

        val aggregation: TypedAggregation<Content> = aggregationBuilder.build(expression)
        val expectedAggregation = TypedAggregationUtils.build<Content>(
            aggregations = *arrayOf(match(Content::name lt "20"))
        )
        Assertions.assertEquals(aggregation.toString(), expectedAggregation.toString())
    }

    @Test
    fun `test less than or equals criteria`() {
        val expression = "lte(name,20)"

        val aggregation: TypedAggregation<Content> = aggregationBuilder.build(expression)
        val expectedAggregation = TypedAggregationUtils.build<Content>(
            aggregations = *arrayOf(match(Content::name lte "20"))
        )
        Assertions.assertEquals(aggregation.toString(), expectedAggregation.toString())
    }

    @Test
    fun `test greater than criteria`() {
        val expression = "gt(name,20)"

        val aggregation: TypedAggregation<Content> = aggregationBuilder.build(expression)
        val expectedAggregation = TypedAggregationUtils.build<Content>(
            aggregations = *arrayOf(match(Content::name gt "20"))
        )
        Assertions.assertEquals(aggregation.toString(), expectedAggregation.toString())
    }

    @Test
    fun `test greater than or equals criteria`() {
        val expression = "gte(name,20)"

        val aggregation: TypedAggregation<Content> = aggregationBuilder.build(expression)
        val expectedAggregation = TypedAggregationUtils.build<Content>(
            aggregations = *arrayOf(match(Content::name gte "20"))
        )
        Assertions.assertEquals(aggregation.toString(), expectedAggregation.toString())
    }

    @Test
    fun `test like start operation criteria`() {
        val expression = "like(name,%c)"

        val aggregation: TypedAggregation<Content> = aggregationBuilder.build(expression)
        val expectedAggregation = TypedAggregationUtils.build<Content>(
            aggregations = *arrayOf(match(Content::name.regex("/c$/")))
        )

        Assertions.assertEquals(aggregation.toString(), expectedAggregation.toString())
    }

    @Test
    fun `test like contains operation criteria`() {
        val expression = "like(name,%c%)"

        val aggregation: TypedAggregation<Content> = aggregationBuilder.build(expression)
        val expectedAggregation = TypedAggregationUtils.build<Content>(
            aggregations = *arrayOf(match(Content::name.regex("/c/")))
        )

        Assertions.assertEquals(aggregation.toString(), expectedAggregation.toString())
    }

    @Test
    fun `test like end operation criteria`() {
        val expression = "like(name,c%)"

        val aggregation: TypedAggregation<Content> = aggregationBuilder.build(expression)
        val expectedAggregation = TypedAggregationUtils.build<Content>(
            aggregations = *arrayOf(match(Content::name.regex("/^c/")))
        )

        Assertions.assertEquals(aggregation.toString(), expectedAggregation.toString())
    }
}
