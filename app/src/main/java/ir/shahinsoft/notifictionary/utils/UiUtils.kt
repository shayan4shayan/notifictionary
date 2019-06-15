package ir.shahinsoft.notifictionary.utils

import android.content.Context
import android.util.TypedValue


class UiUtils {
    companion object {
        fun convertDpToPx(context: Context,value:Float): Float {
            val r = context.resources
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, r.displayMetrics)
        }
    }
}
