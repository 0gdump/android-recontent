package open.v0gdump.recontent

import android.util.Patterns

internal object Validator {

    fun isValidUrl(url: String): Boolean =
        Patterns.WEB_URL.matcher(url).matches()
}