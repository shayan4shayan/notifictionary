package ir.shahinsoft.notifictionary.services.smartnotification

import android.content.Intent

interface SmartNotificationController {
    fun sendSmartTranslationNotification(id:Int,translate:String,translation:String?,intent: Intent)
    fun addNextNotificationWorker()
    fun cancel()
}