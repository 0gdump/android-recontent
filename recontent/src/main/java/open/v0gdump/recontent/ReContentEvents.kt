package open.v0gdump.recontent

import org.jsoup.nodes.Document

abstract class ReContentEvents {
    abstract fun onLoadStart(url: String)
    abstract fun onPageStarted(url: String)
    abstract fun onPageReady(url: String)
    abstract fun beforeParse(url: String, doc: Document)
    abstract fun afterParse(url: String)
}