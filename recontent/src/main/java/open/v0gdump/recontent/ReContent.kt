package open.v0gdump.recontent

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.webkit.WebView
import android.webkit.WebViewClient
import open.v0gdump.recontent.callback.ParserCallback
import open.v0gdump.recontent.callback.WebCallback
import open.v0gdump.recontent.js.JavaScriptInterface
import open.v0gdump.recontent.js.JavaScriptInterface.Companion.JSI_NAME
import open.v0gdump.recontent.js.JavaScriptSources.JS_GET_DOC_SOURCE
import open.v0gdump.recontent.js.JavaScriptSources.JS_READY_STATE_PARASITE
import open.v0gdump.recontent.rule.Rules

class ReContent {

    private var isInitialized = false

    private lateinit var context: Context
    private lateinit var web: WebView

    private lateinit var rules: Rules

    private var webCallback: WebCallback? = null
    private var parserCallback: ParserCallback? = null

    /**
     * Инициализирует и настраивает библиотеку
     */
    fun init(context: Context, callback: WebCallback? = null): ReContent {

        alreadyCreated()

        this.webCallback = callback

        setupWebView(context)

        return this
    }

    /**
     * Инициализирует систему правил
     */
    fun setupRules(
        initializer: (rules: Rules) -> Unit,
        callback: ParserCallback? = null
    ): ReContent {

        rulesAvailable()

        rules = Rules()
        parserCallback = callback

        initializer(rules)

        return this
    }

    //region Validators

    /**
     * Начинает загрузку данных с сервера
     */
    fun load(url: String) {
        rulesNotInitialized()
        notValidUrl(url)

        web.loadUrl(url)
        webCallback?.onNavigate(url)
    }

    /**
     * Вызывает исключение, если ReContent был ранее инициализирован
     */
    private fun alreadyCreated() =
        check(!isInitialized) { "ReContent was already initialized" }

    /**
     * Вызывает исключение, если url не валиден
     */
    private fun notValidUrl(url: String) =
        check(Validator.isValidUrl(url)) { "Url not valid: $url" }

    /**
     * Вызывает исключение, если ранее были созданы правила
     */
    private fun rulesAvailable() =
        check(!::rules.isInitialized) { "Rules was already initialized" }

    /**
     * Вызывает исключение, если правила не были созданы ранее
     */
    private fun rulesNotInitialized() =
        check(::rules.isInitialized) { "Rules wasn't initialized" }

    //endregion

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView(context: Context) {
        this.context = context

        web = WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.blockNetworkImage = true
            settings.loadsImagesAutomatically = false
        }

        setupJSI()
        setupWebViewClient()
    }

    private fun setupJSI() {
        web.addJavascriptInterface(
            JavaScriptInterface { fullLoadingCallback() },
            JSI_NAME
        )
    }

    private fun setupWebViewClient() {
        web.webViewClient = object : WebViewClient() {

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)

                /*
                    Привязываемся к событию полной загрузки документа (state = ready)
                    Как только документ загрузится полностью, произойдёт вызов fullLoadingCallback
                    Через JSI
                */
                view?.evaluateJavascript(JS_READY_STATE_PARASITE, null)

                webCallback?.onDocumentLoadStarted(url!!)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                webCallback?.onDocumentLoadFinished(url!!)
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                url: String
            ): Boolean {
                return !url.endsWith(".css")
            }
        }
    }

    private fun fullLoadingCallback() {
        Handler(context.mainLooper).post {
            web.evaluateJavascript(JS_GET_DOC_SOURCE) { source ->
                documentSourcesReceived(source)
            }
        }
    }

    private fun documentSourcesReceived(source: String) {
        val doc = Parser.parseSource(source)

        webCallback?.onDocumentReady(web.url, doc)

        parserCallback?.onParserStart()
        Parser.matchRules(rules, doc)
        parserCallback?.onParserEnd()
    }
}