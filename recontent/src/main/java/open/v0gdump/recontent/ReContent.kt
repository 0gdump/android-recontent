package open.v0gdump.recontent

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.util.Log
import android.util.Patterns
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
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
    val web = WebView(context).apply {
        settings.javaScriptEnabled = true
        settings.blockNetworkImage = true
        settings.loadsImagesAutomatically = false

        // FIXME(CODE STYLE) Move WebViewClient declaration outside
        webViewClient = object : WebViewClient() {

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)

                // Filter page components errors by url
                if (request?.url.toString() != view?.url) return

                when {
                    error == null -> {
                        eventsHandler?.onError?.invoke(RuntimeException("Network exception"))
                    }
                    android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M -> {
                        eventsHandler?.onError?.invoke(RuntimeException("Error ${error.description}::${error.errorCode}"))
                    }
                    else -> {
                        eventsHandler?.onError?.invoke(RuntimeException("Network exception"))
                    }
                }
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                eventsHandler?.onPageStarted?.invoke(url!!)
                view?.evaluateJavascript(JSI_READY_STATE_PARASITE, null)
            }
        }

        addJavascriptInterface(
            ReContentJSI { onDocumentReady() },
            JSI_NAME
        )
    }

    var sectionsRules = listOf<SectionRule>()

    fun load(url: String) {

        throwWhenUrlIsIncorrect(url)

        if (sectionsRules.isEmpty()) {
            Log.w("recontent", "No rules found!")
        }

        web.loadUrl(url)
        eventsHandler?.onLoadStart?.invoke(url)
    }

    private fun throwWhenUrlIsIncorrect(url: String) =
        check(Patterns.WEB_URL.matcher(url).matches()) { "Url not valid: $url" }

    private fun onDocumentReady() {
        eventsHandler?.onPageReady?.invoke(web.url)
        Handler(web.context.mainLooper).post {
            web.evaluateJavascript(JSI_GET_DOCUMENT_SOURCE_CODE) { source ->
                documentSourcesReceived(source)
            }
        }
    }

    private fun documentSourcesReceived(source: String) {
        val normalized = StringEscapeUtils.unescapeJava(source)
        Jsoup.parse(normalized).let {
            eventsHandler?.beforeParse?.invoke(web.url, it)
            matchRules(it)
            eventsHandler?.afterParse?.invoke(web.url)
        }
    }

    private fun matchRules(doc: Document) {
        sectionsRules.forEach sectionsParse@{ sr ->
            val sectionNode = doc.selectFirst(sr.selector)
            val sectionChildren = sectionNode.childNodes()

            sectionChildren.forEach childMatch@{ child ->
                matchNode(sr, child)
            }
        }
    }

    private fun matchNode(sectionRule: SectionRule, element: Node) {
        when (element) {
            is Element -> {
                if (!matchElement(sectionRule, element)) {
                    sectionRule.specificNodesHandler?.unmatchedElementHandler?.invoke(element)
                }
            }
            is TextNode -> {
                sectionRule.specificNodesHandler?.textNodeHandler?.invoke(element)
            }
            is XmlDeclaration -> {
                sectionRule.specificNodesHandler?.xmlDeclarationHandler?.invoke(element)
            }
            is DocumentType -> {
                sectionRule.specificNodesHandler?.documentTypeHandler?.invoke(element)
            }
            is DataNode -> {
                sectionRule.specificNodesHandler?.dataNodeHandler?.invoke(element)
            }
            is Comment -> {
                sectionRule.specificNodesHandler?.commentHandler?.invoke(element)
            }
        }
    }

    private fun matchElement(sectionRule: SectionRule, element: Element): Boolean {
        var isMatched = false

        sectionRule.childRules.forEach { rule ->
            if (element.`is`(rule.selector)) {
                isMatched = true
                rule.callback(element, rule.tag)
            }
        }

        return isMatched
    }
}