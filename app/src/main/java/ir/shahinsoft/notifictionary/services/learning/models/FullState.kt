package ir.shahinsoft.notifictionary.services.learning.models

import ir.shahinsoft.notifictionary.services.learning.database.TimeSpans
import org.json.JSONObject
import java.sql.Time
import java.util.*

data class FullState(val date: Date, val day: WeekDay, val isScreenOn: Boolean) {

    companion object{
        fun parseJSON(json:JSONObject) : FullState{
            return FullState(Date(json.getLong("date")),WeekDay.values()[json.getInt("week_day")],json.getBoolean("is_screen_on"))
        }
    }

    enum class WeekDay { SAT, SUN, MON, TUS, WED, THU, FRI }

    val id
        get() = "$day${TimeSpans.instance!!.getTimeSpan(date).id}".toInt()

    fun approximate() = ApproximateState(TimeSpans.instance!!.getTimeSpan(date), day >= WeekDay.THU)

    fun toJSON() : JSONObject {
        return JSONObject().apply {
            put("week_day",day)
            put("date",date.time)
            put("is_screen_on",isScreenOn)
        }
    }
}