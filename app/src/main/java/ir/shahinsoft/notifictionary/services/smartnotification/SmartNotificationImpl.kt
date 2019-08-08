package ir.shahinsoft.notifictionary.services.smartnotification

import android.content.Context
import android.content.Intent
import android.os.PowerManager
import androidx.work.*
import ir.shahinsoft.notifictionary.ACTION_SEND_NOTIFICTIONARY
import ir.shahinsoft.notifictionary.services.learning.LearningService
import ir.shahinsoft.notifictionary.services.learning.models.Record
import ir.shahinsoft.notifictionary.services.worker.SendNotificationWorker
import ir.shahinsoft.notifictionary.toast
import ir.shahinsoft.notifictionary.utils.NotificationUtil
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

open class SmartNotificationImpl(val context: Context, private val learningService: LearningService) : SmartNotificationController {


    val tag = "smartNotification"

    override fun addNextNotificationWorker() {

        val hasWork = WorkManager.getInstance(context)
                .getWorkInfosByTag(tag).get().any { it.state != WorkInfo.State.SUCCEEDED }

        if (hasWork) return

        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val pair = learningService.getNextNotificationTime(powerManager.isScreenOn)
        var time = pair.second
        context.toast("action ${pair.first.second} selected by agent")

        val state = pair.first.first
        val action = pair.first.second

        val data = Data.Builder().apply {
            putString("action", ACTION_SEND_NOTIFICTIONARY)
            putInt("state_id", state.id)
            putBoolean("is_smart", true)
            putInt("action", Record.Action.values().indexOf(action))
            putBoolean("send_notification", time > 0)
        }.build()

        if (action == Record.Action.DO_NOT_SEND) {
            time = 1000 * 60 * 60 * 2
        }

        context.toast(time)

        val constraints = Constraints.Builder().setRequiresBatteryNotLow(true).build()

        WorkManager.getInstance(context).enqueueUniqueWork(tag, ExistingWorkPolicy.KEEP, OneTimeWorkRequest
                .Builder(SendNotificationWorker::class.java)
                .setConstraints(constraints)
                .setInputData(data)
                .addTag(tag)
                .addTag("sendTime: ${SimpleDateFormat("HH:mm:ss",Locale.US).format(Date(System.currentTimeMillis() + time))}")
                .setInitialDelay(time, TimeUnit.MILLISECONDS).build())
    }

    override fun sendSmartTranslationNotification(id: Int, translate: String, translation: String?, intent: Intent) {
        if (id > 0) {
            NotificationUtil.sendTranslateNotification(context, id, translate, translation)

            learningService.reward(intent.getIntExtra("state_id", 0),
                    Record.Action.values()[intent.getIntExtra("action", 0)], true)

            context.toast("agent gets good reward")
        }
    }

    override fun cancel() {
        WorkManager.getInstance(context).getWorkInfosByTag(tag).cancel(true)
    }
}