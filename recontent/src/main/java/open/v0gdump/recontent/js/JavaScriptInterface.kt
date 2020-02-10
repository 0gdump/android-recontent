package open.v0gdump.recontent.js

import android.webkit.JavascriptInterface

internal class JavaScriptInterface(
    private val fullLoadingCallback: () -> Unit
) {
    companion object {
        const val JSI_NAME = "Android"
    }

    @JavascriptInterface
    fun documentComplete() = fullLoadingCallback.invoke()
}