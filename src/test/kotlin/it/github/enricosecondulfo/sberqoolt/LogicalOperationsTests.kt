package it.github.enricosecondulfo.sberqoolt

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.query.Criteria

class LogicalOperationsTests {

    @Test
    fun `test and criteria`() {
        val expr = "eq(name,c)&ne(description,d)&sort(+name)"

        val criteria = RqlGrammarUtils.buildCriteria(expr)
        val expectedCriteria = Criteria().andOperator(
                Criteria.where("name").`is`("c"),
                Criteria.where("description").ne("d")
        )

        Assertions.assertEquals(criteria, expectedCriteria)
    }

}