package it.github.enricosecondulfo.sberqoolt

import com.github.h0tk3y.betterParse.grammar.tryParseToEnd
import com.github.h0tk3y.betterParse.parser.Parsed
import com.github.h0tk3y.betterParse.st.SyntaxTree
import com.github.h0tk3y.betterParse.st.liftToSyntaxTreeGrammar
import org.springframework.data.mongodb.core.query.Criteria

class RqlGrammarUtils {

    companion object {
        fun buildCriteria(expr: String): Criteria? {
            val result = RqlGrammar
                    .liftToSyntaxTreeGrammar()
                    .tryParseToEnd(expr)

            val syntaxTree = result as Parsed<SyntaxTree<Expression>>

            println(syntaxTree);

            return CriteriaBuilder().build(syntaxTree.value.item)
        }
    }
}