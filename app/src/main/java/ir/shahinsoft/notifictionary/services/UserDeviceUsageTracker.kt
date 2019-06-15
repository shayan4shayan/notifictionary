package ir.shahinsoft.notifictionary.services

import android.util.Log
import ir.shahinsoft.notifictionary.database.DatabaseWrapper
import ir.shahinsoft.notifictionary.model.PhoneUsage
import ir.shahinsoft.notifictionary.tasks.InsertUsageTast
import ir.shahinsoft.notifictionary.tasks.LoadUsageTask
import java.text.SimpleDateFormat
import java.util.*

class UserDeviceUsageTracker(private val databaseWrapper: DatabaseWrapper) {

    companion object {
        var instance: UserDeviceUsageTracker? = null
        fun getInstance(databaseWrapper: DatabaseWrapper) = if (instance == null) {
            instance = UserDeviceUsageTracker(databaseWrapper); instance!!
        } else instance!!
    }

    val callbacks = ArrayList<() -> Unit>()

    private val mainData = ArrayList<PhoneUsage>()

    private val addData = ArrayList<PhoneUsage>()

    private val removeData = ArrayList<PhoneUsage>()

    private var usage = PhoneUsage()

    private var isStartCaptured = false

    var isInitialized = false

    init {
        loadLastData()
    }


    fun addCallback(callback: () -> Unit) {
        callbacks.add(callback)
        if (isInitialized) {
            call()
        }
    }

    private fun call() {
        callbacks.forEach {
            it.invoke()
        }
    }

    fun removeCallback(callback: () -> Unit) = callbacks.remove(callback)

    private fun getDataBase() = databaseWrapper

    private fun loadLastData() {
        LoadUsageTask(getDataBase()) {
            if (it == null || it.isEmpty()) {
                addFirstElement()
            } else {
                buildData(it)
            }
            isInitialized
            call()
        }.execute()
    }

    private fun buildData(data: ArrayList<PhoneUsage>) {
        val validData = data.filter { it.isValid }
        mainData.add(getFirstElement())
        validData.forEach { updateMainDataWith(it) }
    }

    private fun updateMainDataWith(usage: PhoneUsage) {
        Log.d("hello", "" + mainData.size)
        mainData.forEach {
            if (it.hasCollision(usage)) {
                Log.d("hello", "has Collision")
                updateMainData(it, usage)
            }
        }
        mainData.removeAll(removeData)
        mainData.addAll(addData)
        Log.d("hello", "main size:" + mainData.size)
        addData.clear()
        removeData.clear()
    }

    private fun updateMainData(it: PhoneUsage, usage: PhoneUsage) {
        when {
            it.isInside(usage) -> {
                Log.d("hello", "inside $usage")
                Log.d("hello", "inside $it")
                val u1 = createUsage(it.startTime, usage.startTime, it.score)
                val u2 = createUsage(usage.endTime, it.endTime, it.score)
                usage.score += it.score
                removeData.add(it)
                if (u1.isValid)
                    addData.add(u1)
                else
                    usage.merge(u1)
                if (u2.isValid)
                    addData.add(u2)
                else
                    usage.merge(u2)
                if (usage.isValid)
                    addData.add(usage)
            }
            usage.isInside(it) -> {
                Log.d("hello", "inside $usage")
                Log.d("hello", "inside $it")
                val u1 = createUsage(usage.startTime, it.startTime, usage.score)
                val u2 = createUsage(it.endTime, usage.endTime, usage.score)
                it.score += usage.score
                if (u1.isValid)
                    addData.add(u1)
                else
                    it.merge(u1)
                if (u2.isValid)
                    addData.add(u2)
                else
                    it.merge(u2)
            }
            it.hasValidCollisionFromSide(usage) -> {
                Log.d("hello", "right $it")
                Log.d("hello", "right $usage")
                val u = createUsage(usage.startTime, it.endTime, usage.score + it.score)
                val u1 = createUsage(it.startTime, usage.startTime, it.score)
                val u2 = createUsage(it.endTime, usage.endTime, usage.score)
                removeData.add(it)
                if (u1.isValid)
                    addData.add(u1)
                else
                    u.merge(u1)
                if (u.isValid)
                    addData.add(u)
                else
                    u2.merge(u)
                if (u2.isValid)
                    addData.add(u2)
            }
            else -> {
                Log.d("hello", "inside $usage")
                Log.d("hello", "inside $it")
                val u = createUsage(it.startTime, usage.endTime, usage.score + it.score)
                val u1 = createUsage(usage.startTime, it.startTime, usage.score)
                val u2 = createUsage(usage.endTime, it.endTime, it.score)
                removeData.add(it)
                if (u1.isValid)
                    addData.add(u1)
                else
                    u.merge(u1)
                if (u.isValid)
                    addData.add(u)
                else
                    u2.merge(u)
                if (u2.isValid)
                    addData.add(u2)
            }
        }
    }

    private fun createUsage(startTime: String, endTime: String, score: Int): PhoneUsage {
        return PhoneUsage().apply {
            this.startTime = startTime
            this.endTime = endTime
            this.score = score
        }
    }

    private fun addFirstElement() {
        mainData.add(getFirstElement())
        Log.d("hello1", "" + mainData.size)
//        getDataBase().addPhoneUsage(getFirstElement().apply {
//            id = getDataBase().getPhoneUsageId()
//        })
    }

    private fun getFirstElement(): PhoneUsage {
        return PhoneUsage().apply {
            val dateFormat = SimpleDateFormat("HH:MM:ss", Locale.US)

            startTime = "00:00:00"
            endTime = "23:59:59"
            score = 0//usage with no score
        }
    }

    fun getDataString() = mainData.toString()

    fun addUsage(usage: PhoneUsage) {
        if (usage.isValid)
            updateMainDataWith(usage)
    }

    fun captureStart() {
        Log.d("hello", "capturing start")
        usage = PhoneUsage()
        val format = SimpleDateFormat("HH:mm:ss", Locale.US)
        usage.startTime = format.format(Date(System.currentTimeMillis()))
        isStartCaptured = true
    }

    fun captureEnd() {
        Log.d("hello", "capturing end")
        if (!isStartCaptured) return
        isStartCaptured = false
        val format = SimpleDateFormat("HH:mm:ss", Locale.US)
        usage.endTime = format.format(Date(System.currentTimeMillis()))
        addUsageToDatabase()
        addUsage(usage)
    }

    private fun addUsageToDatabase() {
        InsertUsageTast(getDataBase()).execute()
    }

    fun getNextNotificationTime(period: Int): Long {
        val maxRate = mainData.map { it.score }.max()
        val minRate = mainData.map { it.score }.min()
        val avg = (maxRate!! + minRate!!) / 2
        val validatedList = mainData.filter { it.score >= avg }.sortedBy { it.startTimeInt }
        if (validatedList.isEmpty()) return (period * 60 * 1000).toLong()
        val currentTime = getCurrentTime()
        val p = period * 60 * 1000
        validatedList.forEach {
            if (it.isInside(currentTime + p)) return p.toLong()
            if (currentTime + p < it.startTimeInt) return it.startTimeInt - currentTime
        }
        return (period * 60 * 1000).toLong()
    }

    private fun getCurrentTime(): Int {
        val format = SimpleDateFormat("HH:mm:ss", Locale.US)
        val time = format.format(Date(System.currentTimeMillis()))
        val vals = time.split(":")
        val hour = Integer.parseInt(vals[0])
        val min = Integer.parseInt(vals[1])
        val sec = Integer.parseInt(vals[2])
        return hour * (1000 * 60 * 60) + min * (1000 * 60) + sec * 1000
    }
}