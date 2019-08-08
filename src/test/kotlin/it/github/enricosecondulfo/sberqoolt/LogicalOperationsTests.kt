package it.github.enricosecondulfo.sberqoolt

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.Aggregation.match
import org.springframework.data.mongodb.core.aggregation.TypedAggregation
import org.springframework.data.mongodb.core.query.Criteria

class LogicalOperationsTests {

    private val aggregationBuilder = AggregationBuilder()

    /* @Test
    fun `test and criteria`() {
        val expr = "eq(name,c)&ne(description,d)&sort(+name)"

        val criteria = RqlGrammarUtils.buildCriteria(expr)
        val expectedCriteria = Criteria().andOperator(
                Criteria.where("name").`is`("c"),
                Criteria.where("description").ne("d")
        )

        Assertions.assertEquals(criteria, expectedCriteria)
    } */
}