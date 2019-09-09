package ir.shahinsoft.notifictionary.services.notification

interface NotificationController {
    fun addNextNotificationWorker()
    fun cancel()
    fun checkWorker()
}