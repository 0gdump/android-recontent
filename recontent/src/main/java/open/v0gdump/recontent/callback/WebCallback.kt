package open.v0gdump.recontent.callback

import org.jsoup.nodes.Document

interface WebCallback {
    fun onNavigate(url: String)
    fun onDocumentLoadStarted(url: String)
    fun onDocumentLoadFinished(url: String)
    fun onDocumentReady(url: String, doc: Document)
}