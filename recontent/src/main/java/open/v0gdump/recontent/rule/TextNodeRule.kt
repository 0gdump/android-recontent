package open.v0gdump.recontent.rule

import org.jsoup.nodes.TextNode

data class TextNodeRule(
    val callback: (node: TextNode) -> Unit
)