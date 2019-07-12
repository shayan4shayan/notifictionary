package ir.shahinsoft.notifictionary.services

import android.annotation.TargetApi
import android.app.*
import android.content.*
import android.os.*
import android.preference.PreferenceManager
import android.speech.tts.TextToSpeech
import androidx.core.app.NotificationCompat
import android.util.Log
import ir.shahinsoft.notifictionary.*
import ir.shahinsoft.notifictionary.model.History
import ir.shahinsoft.notifictionary.receivers.LockReceiver
import ir.shahinsoft.notifictionary.services.learning.LearningService
import ir.shahinsoft.notifictionary.services.learning.database.PathProvider
import ir.shahinsoft.notifictionary.services.learning.models.Record
import ir.shahinsoft.notifictionary.tasks.CountLearnTask
import ir.shahinsoft.notifictionary.tasks.InsertHistoryTask
import ir.shahinsoft.notifictionary.tasks.RandomTranslateTask
import ir.shahinsoft.notifictionary.utils.NotificationUtil
import java.util.*

class NotifictionaryService : Service() {


    enum class State {
        START, NOTIFICATION_SEND, NOTIFICATION_RESPONSE_RECEIVED,
    }

    var state: State = State.START

    private lateinit var binder: Binder

    private var isStarted = false

    var mCurrentClip: String? = null

    private lateinit var speaker: TextToSpeech

    private lateinit var lockScreenReceiver: BroadcastReceiver

    private var isSpeakerReady = false

    private lateinit var usageTracker: UserDeviceUsageTracker

    private lateinit var learningService: LearningService

    private var lastPendingInt: PendingIntent? = null

    private val clipboardReceiver = ClipboardManager.OnPrimaryClipChangedListener {
        val manager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (manager.primaryClip?.itemCount ?: 0 > 0) {
            val canTranslate = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_translate_anywhere", true)
            val item = manager.primaryClip?.getItemAt(0)
            if (item != null) {
                val clip = item.text?.toString()?.replace("-\n", "")
                if (clip != null && clip != mCurrentClip) {
                    mCurrentClip = clip
                    translateAndToast(canTranslate, clip)
                }
            }
        }
    }

    private fun translateAndToast(canTranslate: Boolean, clip: String) {
        if (canTranslate) {
            val sp = getSharedPreferences(APP, Context.MODE_PRIVATE)
            val to = sp.getString(TARGET_SMALL, "fa")
            Translator.with(this).cancelAll()
            Translator.with(this).callback(object : Translator.TranslateListener {
                override fun onWordTranslated(translate: String?) {
                    if (translate != null) {
                        translateToast(translate)
                    }
                    addToDatabase(clip, translate ?: "")
                    mCurrentClip = null
                }

                override fun onFailedToTranslate(reason: String?) {
                    addToDatabase(clip, "")
                }
            }).TranslateTo(to).translate(clip)
        } else {
            addToDatabase(clip, "")
        }
    }

    private fun addToDatabase(clip: String, translate: String) {
        val isSaveEnable =
                PreferenceManager.getDefaultSharedPreferences(this)
                        .getBoolean("pref_save_translates", true)
        Log.d("NotifictionaryService", "" + isSaveEnable)
        if (isSaveEnable)
            InsertHistoryTask(getAppDatabase())
                    .execute(History().apply { word = clip;this.translate = translate })
    }

    fun isSmartNotificationActive() = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_smart_notifications", true)

    override fun onCreate() {
        super.onCreate()
        binder = Binder()
        initSpeaker()
        initLearningService()
        if (isSmartNotificationActive()) {
            initDeviceUsageReceiver()
        }
    }

    private fun initLearningService() {
        PathProvider.instance.init(this)
        learningService = LearningService()
        triggerNextNotificationTime()
    }

    private fun initDeviceUsageReceiver() {
        lockScreenReceiver = LockReceiver()
        IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
            registerReceiver(lockScreenReceiver, this)
        }
    }

    private fun turnOffSmartNotifications() {
        unregisterReceiver(lockScreenReceiver)
    }

    private fun initSpeaker() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            toast("android version is low")
            return
        }
        Handler(mainLooper).postDelayed({
            Log.d("NotifictionaryService", "initing text2Speech")
            speaker = TextToSpeech(this) {
                if (it == TextToSpeech.SUCCESS) {
                    Log.d("NotifictionaryService", "speech init success")
                    isSpeakerReady = true
                    speaker.language = Locale.US
                } else initSpeaker()
            }
        }, 1000)
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_INIT -> initServiceForeground()
            ACTION_STOP -> cancelNotification()
            ACTION_INIT_CLIPBOARD -> initClipBoard()
            ACTION_STOP_LISTEN_CLIPBOARD -> stopListenClipboard()
            ACTION_INIT_NOTIFICATIONS -> triggerNextNotificationTime()
            ACTION_SEND_NOTIFICTIONARY -> sendNotifictionary(intent)
            ACTION_MARK_TRANSLATE -> markAsRead(intent.getIntExtra(EXTRA_ID, -1))
            ACTION_SPEAK -> speak(intent.getStringExtra(EXTRA_WORD))
            ACTION_NEW_PERIOD -> triggerNextNotificationTime()
            ACTION_SMART_NOTIFICATION_ON -> initDeviceUsageReceiver()
            ACTION_SMART_NOTIFICATION_OFF -> turnOffSmartNotifications()
            ACTION_SEND_TRANSLATE_NOTIFICATION -> sendTranslateNotification(intent.getIntExtra(EXTRA_ID, -1), intent.getStringExtra(EXTRA_TRANSLATE), intent.getStringExtra(EXTRA_LANG), intent)
            ACTION_DISMISS_NOTIFICATION -> dismissNotification(intent.getIntExtra(EXTRA_ID, -1), intent.getBooleanExtra(EXTRA_HAS_LEARNED, false))
            ACTION_USER_DISMISSED_NOTIFICATION -> userDismissedNotification(intent.getIntExtra(EXTRA_ID, -1), intent)
            ACTION_FORCE_NOTIFICATION -> forceANotifictionary()
        }

        return START_STICKY
    }

    private fun forceANotifictionary() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(lastPendingInt)
        triggerNextNotificationTime()
    }

    private fun userDismissedNotification(id: Int, intent: Intent) {
        Log.d("NotifictionaryService", "user dismissed notification")
        learningService.reward(intent.getIntExtra("state_id", 0),
                Record.Action.values()[intent.getIntExtra("action", 0)], false)
        toast("agent received bad reward")
        triggerNextNotificationTime()
    }

    private fun dismissNotification(id: Int, hasLearned: Boolean) {
        NotificationUtil.cancelNotification(this, id)
        state = State.START
        triggerNextNotificationTime()

        if (hasLearned) {
            CountLearnTask(getAppDatabase(), id).execute()
        }
    }

    private fun sendTranslateNotification(id: Int, translate: String, translation: String?, intent: Intent) {
        Log.d("NotifictionaryService", "update translate $id")
        state = State.START
        if (id > 0) {
            NotificationUtil.sendTranslateNotification(this, id, translate, translation)
            learningService.reward(intent.getIntExtra("state_id", 0),
                    Record.Action.values()[intent.getIntExtra("action", 0)], true)
            toast("agent gets good reward")
        }
        //triggerNextNotificationTime()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun speak(word: String?) {
        Log.d("notifictionaryService", "text2speech called : $word")
        if (word != null && word.isNotEmpty()) {
            if (isSpeakerReady) {
                val speed = PreferenceManager.getDefaultSharedPreferences(this)
                        .getString("pref_speech_speed", "50")!!.toInt()
                speaker.setSpeechRate(speed.toFloat() / 50f)
                speaker.speak(word, TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
    }

    private fun markAsRead(id: Int) {
        if (id < 0) return
        NotificationUtil.cancelNotification(this, id)
    }

    private fun sendNotifictionary(intent: Intent) {
        if (!isStarted) return
        val goal = PreferenceManager.getDefaultSharedPreferences(this).getString("pref_learn_goal", "5")!!.toInt()
        RandomTranslateTask(getAppDatabase(), goal) {

            if (intent.getBooleanExtra("send_notification", true))
                NotificationUtil.sendNotification(this, intent, it)
            state = State.NOTIFICATION_SEND
        }.execute()
    }

    private fun triggerNextNotificationTime() {

        //checking if sending notification is active
        val isNotificationEnabled = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("notifications_new_message", true)
        if (!isNotificationEnabled) return

        //checking if we are in correct state
        if (state != State.START) return

        //send notification
        val smartNotification = isSmartNotificationActive()
        Log.d("notifictionaryService", "$smartNotification")
        val period = PreferenceManager.getDefaultSharedPreferences(this).getString("pref_notification_cycle", "20")!!.toInt()
        var inMills: Long
        if (smartNotification) {
            handleSmartNotification()
            return
        } else {
            inMills = getNormalTime(period)
        }
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(getNotificationPendingIntent(inMills))

        Log.d("notifictionaryService", "$inMills")
        Log.d("notifictionaryService", "$period")
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + if (inMills > 0) inMills else 1000 * 60 * 30, getNotificationPendingIntent(inMills))
    }

    private fun handleSmartNotification() {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        val pair = learningService.getNextNotificationTime(powerManager.isScreenOn)
        var time = pair.second
        toast("action ${pair.first.second} selected by agent")
        val state = pair.first.first
        val action = pair.first.second
        val intent = Intent(this, NotifictionaryService::class.java).apply {
            this.action = ACTION_SEND_NOTIFICTIONARY
            putExtra("state_id", state.id)
            putExtra("is_smart", true)
            putExtra("action", Record.Action.values().indexOf(action))
            putExtra("send_notification", time > 0)
        }

        if (action == Record.Action.DO_NOT_SEND) {
            time = 1000 * 60 * 60 * 2
        }

        toast(time)

        val pIntent = PendingIntent.getService(this, state.id, intent, PendingIntent.FLAG_ONE_SHOT)
        lastPendingInt = pIntent

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + time, pIntent)
    }

    private fun getNormalTime(period: Int): Long {
        return (period * 60 * 1000).toLong()
    }


    private fun getNotificationPendingIntent(inMills: Long): PendingIntent {
        val intent = Intent(this, NotifictionaryService::class.java)
        intent.action = ACTION_SEND_NOTIFICTIONARY
        intent.putExtra("send_notification", inMills > 0)
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
    }

    private fun stopListenClipboard() {
        val manager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        manager.removePrimaryClipChangedListener(clipboardReceiver)
    }

    private fun initClipBoard() {
        val manager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        manager.removePrimaryClipChangedListener(clipboardReceiver)
        manager.addPrimaryClipChangedListener(clipboardReceiver)
    }

    private fun initServiceForeground() {
        if (!isStarted) {
            isStarted = true
            Log.d("notifictionaryService", "initing service")
            initNotification()
            initLearningService()
        }
        initOtherPartsIfNeeded()
    }

    private fun initOtherPartsIfNeeded() {
        initClipBoard()
        initSpeaker()
    }

    private fun cancelNotification() {
        stopForeground(true)
        stopListenClipboard()
        isStarted = false
    }

    private fun initNotification() {
        val channelId = "Translate everywhere"
        if (isOreoPlus()) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val name = resources.getString(R.string.app_name)
            val importance = NotificationManager.IMPORTANCE_LOW
            NotificationChannel(channelId, name, importance).apply {
                enableLights(false)
                enableVibration(false)
                setShowBadge(false)
                notificationManager.createNotificationChannel(this)
            }
        }

//        val style = NotificationCompat.BigTextStyle()
//        style.bigText(getString(R.string.notifictionary_is_active))

        val notification = NotificationCompat.Builder(this, getString(R.string.app_name))
                .setContentTitle(getString(R.string.notifictionary_is_active))
                //.setContentText(getString(R.string.notifictionary_is_active))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .addAction(0, getString(R.string.close_notification), getCloseIntentPending())
                .addAction(0, getString(R.string.force_notification), getForceNotificationPendingIntent())
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setShowWhen(false)
//                .setStyle(style)
                .setChannelId(channelId)



        startForeground(10, notification.build())
    }

    private fun getForceNotificationPendingIntent(): PendingIntent {
        return PendingIntent.getService(this, 12345,
                Intent(this, NotifictionaryService::class.java).apply {
                    action = ACTION_FORCE_NOTIFICATION
                }
                , if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_UPDATE_CURRENT)

    }

    override fun onDestroy() {
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_smart_notifications", true)) {
            unregisterReceiver(lockScreenReceiver)
        }
        super.onDestroy()

    }


    private fun getCloseIntentPending(): PendingIntent? {
        val i = Intent(this, NotifictionaryService::class.java)
        i.action = ACTION_STOP
        return PendingIntent.getService(this, 0, i, PendingIntent.FLAG_ONE_SHOT)
    }

    private fun isOreoPlus(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    }

}
