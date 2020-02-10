package open.v0gdump.recontent.js

import open.v0gdump.recontent.js.JavaScriptInterface.Companion.JSI_NAME

internal object JavaScriptSources {
    const val JS_GET_DOC_SOURCE =
        """
(function() {
    return ('<html>' + document.getElementsByTagName('html')[0].innerHTML + '</html>');
})();
        """

    const val JS_READY_STATE_PARASITE =
        """
document.addEventListener('readystatechange', event => {
    if (event.target.readyState === "complete") {
        $JSI_NAME.documentComplete();
    }
});
        """
}