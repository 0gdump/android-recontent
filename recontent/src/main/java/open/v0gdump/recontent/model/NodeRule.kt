package open.v0gdump.recontent.model

import org.jsoup.nodes.Element

data class NodeRule(
    val selector: String,
    val matchCallback: ((element: Element, tag: String?) -> Unit)? = null,
    val treeParsedCallback: ((element: Element, tag: String?) -> Unit)? = null,
    val sectionRule: SectionRule? = null,
    val tag: String? = null
)