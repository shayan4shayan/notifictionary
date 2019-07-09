package ir.shahinsoft.notifictionary.services.learning.models

import ir.shahinsoft.notifictionary.services.learning.database.TimeSpans
import org.json.JSONObject

data class ApproximateState(val timeSpan: TimeSpan, val isWeekEnd: Boolean) {

    companion object {
        fun parseJSON(json:JSONObject) : ApproximateState {
            return ApproximateState(TimeSpans.instance!!.spans[json.getInt("time_span")],json.getBoolean("is_weekend"))
        }
    }

    val id
        get() = "${if (isWeekEnd) 1 else 0}${timeSpan.id}".toInt()

    fun toJSON(): JSONObject {
        return JSONObject().apply {
            put("time_span", timeSpan.id)
            put("is_weekend", isWeekEnd)
        }
    }
}