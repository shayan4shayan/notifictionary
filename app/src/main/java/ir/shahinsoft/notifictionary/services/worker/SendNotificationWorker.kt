package ir.shahinsoft.notifictionary.services.worker

import android.content.Context
import android.content.Intent
import androidx.work.Worker
import androidx.work.WorkerParameters
import ir.shahinsoft.notifictionary.getAppDatabase
import ir.shahinsoft.notifictionary.services.NotifictionaryService
import ir.shahinsoft.notifictionary.utils.NotificationUtil

class SendNotificationWorker(appContext:Context,workerParameters: WorkerParameters )
    : Worker(appContext,workerParameters){

    override fun doWork(): Result {
        val randomTranslate = applicationContext.getAppDatabase().selectAllTranslates().random()
        val intent = Intent(applicationContext,NotifictionaryService::class.java).apply {
            this.action = inputData.getString("action")
            inputData.keyValueMap.keys.forEach { this.putExtra(it,inputData.keyValueMap[it].toString()) }
        }
        NotificationUtil.sendNotification(applicationContext,intent,randomTranslate)
        return Result.success()
    }

}