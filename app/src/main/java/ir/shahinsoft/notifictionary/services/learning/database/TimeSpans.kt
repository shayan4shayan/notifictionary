package ir.shahinsoft.notifictionary.services.learning.database

import ir.shahinsoft.notifictionary.services.learning.models.TimeSpan
import java.lang.NumberFormatException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TimeSpans {

    companion object {
        var instance: TimeSpans? = null
            get() {
                if (field == null) instance = TimeSpans()
                return field
            }
    }

    val spans = ArrayList<TimeSpan>()

    init {
        createSpans()
    }

    private fun createSpans() {
        (0 until 288).forEach {
            spans.add(TimeSpan(it, it,(it+1)%288))
        }
    }

    fun getCurrentSpan(): TimeSpan {
        return getTimeSpan(Date(System.currentTimeMillis()))
    }

    fun getTimeSpan(date: Date): TimeSpan {
        try {
            val dateFormat = SimpleDateFormat("HH:mm", Locale.US)
            val time = dateFormat.format(date)
            val hour = time.split(':')[0].toInt()
            val min = time.split(':')[1].toInt()
            val index = hour * 2 + min / 30;
            return spans[index]
        } catch (e: NumberFormatException) {
            throw e
        }

    }
}