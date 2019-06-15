package ir.shahinsoft.notifictionary.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import ir.shahinsoft.notifictionary.getAppDatabase
import ir.shahinsoft.notifictionary.services.UserDeviceUsageTracker
import ir.shahinsoft.notifictionary.services.NotifictionaryService

class LockReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        context.startService(Intent(context, NotifictionaryService::class.java))
        Log.d("LockReceiver", "onReceive called")
        when (intent.action) {
            Intent.ACTION_SCREEN_OFF ->
                onScreenOff(context)
            Intent.ACTION_SCREEN_ON ->
                onScreenOn(context)
        }
    }

    private fun onScreenOn(context: Context) {
        UserDeviceUsageTracker.getInstance(context.getAppDatabase()).captureStart()
    }

    private fun onScreenOff(context: Context) {
        UserDeviceUsageTracker.getInstance(context.getAppDatabase()).captureEnd()
    }
}
