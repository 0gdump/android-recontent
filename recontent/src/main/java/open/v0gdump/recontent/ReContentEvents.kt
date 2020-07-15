package open.v0gdump.recontent

import org.jsoup.nodes.Document

data class ReContentEvents(
    val onLoadStart: ((url: String) -> Unit)? = null,
    val onPageStarted: ((url: String) -> Unit)? = null,
    val onPageReady: ((url: String) -> Unit)? = null,
    val beforeParse: ((url: String, doc: Document) -> Unit)? = null,
    val afterParse: ((url: String) -> Unit)? = null
)