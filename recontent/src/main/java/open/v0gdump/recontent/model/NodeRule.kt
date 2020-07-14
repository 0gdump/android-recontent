package open.v0gdump.recontent.model

import org.jsoup.nodes.Element

data class NodeRule(
    val selector: String,
    val callback: (element: Element, tag: String?) -> Unit,
    val tag: String? = null
)