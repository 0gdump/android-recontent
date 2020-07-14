package open.v0gdump.recontent

import android.webkit.JavascriptInterface

internal class ReContentJSI(
    private val onDocumentReady: () -> Unit
) {
    companion object {

        const val JSI_NAME = "Android"

        const val JSI_GET_DOCUMENT_SOURCE_CODE =
            """
(function() {
    return ('<html>' + document.getElementsByTagName('html')[0].innerHTML + '</html>');
})();
        """

        const val JSI_READY_STATE_PARASITE =
            """
document.addEventListener('readystatechange', event => {
    if (event.target.readyState === "complete") {
        $JSI_NAME.onDocumentReady();
    }
});
        """
    }

    @JavascriptInterface
    fun onDocumentReady() = onDocumentReady.invoke()
}