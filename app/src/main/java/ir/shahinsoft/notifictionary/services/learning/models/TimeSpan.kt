package ir.shahinsoft.notifictionary.services.learning.models

data class TimeSpan(val id: Int, val start: Int, val end: Int) {

    fun getStartTime(): String {
        val hour = start / 2
        return "${if (hour >= 10) "$hour" else "0$hour"}:${if (start % 2 == 1) "30" else "00"}"
    }

    fun getEndTime(): String {
        val hour = end / 2
        return "${if (hour >= 10) "$hour" else "0$hour"}:${if (end % 2 == 1) "30" else "00"}"
    }
}