package open.v0gdump.recontent

import open.v0gdump.recontent.rule.Rules
import org.apache.commons.text.StringEscapeUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode

internal object Parser {

    fun parseSource(source: String): Document {
        val normalSource = StringEscapeUtils.unescapeJava(source)
        return Jsoup.parse(normalSource)
    }

    fun matchRules(rules: Rules, doc: Document) {
        rules.forEach { rp ->
            val root = doc.selectFirst(rp.rootSelector)

            root.childNodes().forEach nodesMatching@{ node ->
                if (node is TextNode) {
                    rp.textNodeRule.callback(node)
                    return@nodesMatching
                }

                val el = node as Element
                rp.rules.forEach { rule ->
                    if (el.`is`(rule.selector)) rule.callback(el, rule.tag)
                }
            }
        }
    }
}