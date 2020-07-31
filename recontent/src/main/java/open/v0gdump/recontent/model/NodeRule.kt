package open.v0gdump.recontent.model

import org.jsoup.nodes.Element

data class NodeRule(
    val selector: String,
    val matchCallback: (element: Element, tag: String?) -> Unit,
    val tag: String? = null,
    val sectionRule: SectionRule? = null
)