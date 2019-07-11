package ir.shahinsoft.notifictionary.services.learning.models

import kotlin.math.min

data class TimeSpan(val id: Int, val start: Int, val end: Int) {

    fun getStartTime(): String {
        var mins = start*5
        val hour = mins/60
        mins %=60
        return "${if (hour >= 10) "$hour" else "0$hour"}:${if (mins>=10) "$mins" else "0$mins"}"
    }

    fun getEndTime(): String {
        var mins = end*5
        val hour = mins/60
        mins %=60;
        return "${if (hour >= 10) "$hour" else "0$hour"}:${if (mins>=10) "$mins" else "0$mins"}"
    }
}