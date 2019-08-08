package ir.shahinsoft.notifictionary.services.notification

import android.content.Context
import android.preference.PreferenceManager
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import ir.shahinsoft.notifictionary.services.worker.SendNotificationWorker
import java.util.concurrent.TimeUnit

class NotificationImpl(val context: Context) : NotificationController {


    private val tag = "smartNotification"


    override fun addNextNotificationWorker() {
        val delay = getDelay()

        WorkManager.getInstance(context).enqueueUniqueWork(tag,ExistingWorkPolicy.KEEP,
                OneTimeWorkRequest.Builder(SendNotificationWorker::class.java)
                        .setInitialDelay(delay.toLong(),TimeUnit.MINUTES)
                        .setInputData(Data.Builder()
                                .putBoolean("is_smart",false)

                                .build())
                        .build())
    }

    override fun cancel() {
        WorkManager.getInstance(context).getWorkInfosByTag(tag).cancel(true)
    }

    private fun getDelay(): Int {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt("pref_notification_cycle",20)
    }

}