package it.github.enricosecondulfo.sberqoolt

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.query.Criteria

class ComparisonOperationsTests {

    @Test
    fun `test equals criteria`() {
        val expr = "eq(name,c)"

        val criteria = RqlGrammarUtils.buildCriteria(expr)
        val expectedCriteria = Criteria.where("name").`is`("c")
        Assertions.assertEquals(criteria, expectedCriteria)
    }

    @Test
    fun `test not equals criteria`() {
        val expr = "ne(name,c)"

        val criteria = RqlGrammarUtils.buildCriteria(expr)
        val expectedCriteria = Criteria.where("name").ne("c")
        Assertions.assertEquals(criteria, expectedCriteria)
    }

    @Test
    fun `test less than criteria`() {
        val expr = "lt(name,20)"

        val criteria = RqlGrammarUtils.buildCriteria(expr)
        val expectedCriteria = Criteria.where("name").lt("20")
        Assertions.assertEquals(criteria, expectedCriteria)
    }

    @Test
    fun `test less than or equals criteria`() {
        val expr = "lte(name,20)"

        val criteria = RqlGrammarUtils.buildCriteria(expr)
        val expectedCriteria = Criteria.where("name").lte("20")
        Assertions.assertEquals(criteria, expectedCriteria)
    }

    @Test
    fun `test greater than criteria`() {
        val expr = "gt(name,20)"

        val criteria = RqlGrammarUtils.buildCriteria(expr)
        val expectedCriteria = Criteria.where("name").gt("20")
        Assertions.assertEquals(criteria, expectedCriteria)
    }

    @Test
    fun `test greater than or equals criteria`() {
        val expr = "gte(name,20)"

        val criteria = RqlGrammarUtils.buildCriteria(expr)
        val expectedCriteria = Criteria.where("name").gte("20")
        Assertions.assertEquals(criteria, expectedCriteria)
    }

    @Test
    fun `test like start operation criteria`() {
        val expr = "like(name,%c)"

        val criteria = RqlGrammarUtils.buildCriteria(expr)
        val expectedCriteria = Criteria.where("name").regex("/c$/")

        Assertions.assertEquals(criteria, expectedCriteria)
    }

    @Test
    fun `test like contains operation criteria`() {
        val expr = "like(name,%c%)"

        val criteria = RqlGrammarUtils.buildCriteria(expr)
        val expectedCriteria = Criteria.where("name").regex("/c/")

        Assertions.assertEquals(criteria, expectedCriteria)
    }

    @Test
    fun `test like end operation criteria`() {
        val expr = "like(name,c%)"

        val criteria = RqlGrammarUtils.buildCriteria(expr)
        val expectedCriteria = Criteria.where("name").regex("/^c/")

        Assertions.assertEquals(criteria, expectedCriteria)
    }
}
