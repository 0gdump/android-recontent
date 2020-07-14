package open.v0gdump.recontent

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.util.Log
import android.util.Patterns
import android.webkit.WebView
import android.webkit.WebViewClient
import open.v0gdump.recontent.ReContentJSI.Companion.JSI_GET_DOCUMENT_SOURCE_CODE
import open.v0gdump.recontent.ReContentJSI.Companion.JSI_NAME
import open.v0gdump.recontent.ReContentJSI.Companion.JSI_READY_STATE_PARASITE
import open.v0gdump.recontent.model.SectionRule
import org.apache.commons.text.StringEscapeUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.*

class ReContent(
    context: Context,
    val eventsHandler: ReContentEvents? = null
) {

    @SuppressLint("SetJavaScriptEnabled")
    private val web = WebView(context).apply {
        settings.javaScriptEnabled = true
        settings.blockNetworkImage = true
        settings.loadsImagesAutomatically = false

        webViewClient = webClient

        addJavascriptInterface(
            ReContentJSI { onDocumentReady() },
            JSI_NAME
        )
    }
    private val webClient = object : WebViewClient() {

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            eventsHandler?.onPageStarted(url!!)
            view?.evaluateJavascript(JSI_READY_STATE_PARASITE, null)
        }

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            url: String
        ): Boolean {
            return !url.endsWith(".css")
        }
    }

    private var sectionsRules = listOf<SectionRule>()

    fun load(url: String) {

        throwWhenUrlIsIncorrect(url)

        if (sectionsRules.isEmpty()) {
            Log.w("recontent", "No rules found!")
        }

        web.loadUrl(url)
        eventsHandler?.onLoadStart(url)
    }

    private fun throwWhenUrlIsIncorrect(url: String) =
        check(Patterns.WEB_URL.matcher(url).matches()) { "Url not valid: $url" }

    private fun onDocumentReady() {
        eventsHandler?.onPageReady(web.url)
        Handler(web.context.mainLooper).post {
            web.evaluateJavascript(JSI_GET_DOCUMENT_SOURCE_CODE) { source ->
                documentSourcesReceived(source)
            }
        }
    }

    private fun documentSourcesReceived(source: String) {
        val normalized = StringEscapeUtils.unescapeJava(source)
        Jsoup.parse(normalized).let {
            eventsHandler?.beforeParse(web.url, it)
            matchRules(it)
            eventsHandler?.afterParse(web.url)
        }
    }

    private fun matchRules(doc: Document) {
        sectionsRules.forEach sectionsParse@{ sr ->

            val sectionNode = doc.selectFirst(sr.selector)
            val sectionChildren = sectionNode.childNodes()

            sectionChildren.forEach childMatch@{ child ->
                when (child) {
                    is Element -> {
                        sr.childRules.forEach { rule ->
                            if (child.`is`(rule.selector)) {
                                rule.callback(child, rule.tag)
                            }
                        }
                    }
                    is TextNode -> {
                        sr.specificNodesHandler?.textNodeHandler(child)
                    }
                    is XmlDeclaration -> {
                        sr.specificNodesHandler?.xmlDeclarationHandler(child)
                    }
                    is DocumentType -> {
                        sr.specificNodesHandler?.documentTypeHandler(child)
                    }
                    is DataNode -> {
                        sr.specificNodesHandler?.dataNodeHandler(child)
                    }
                    is Comment -> {
                        sr.specificNodesHandler?.commentHandler(child)
                    }
                }
            }
        }
    }
}