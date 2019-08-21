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
import ir.shahinsoft.notifictionary.services.notification.NotificationController
import ir.shahinsoft.notifictionary.services.notification.NotificationImpl
import ir.shahinsoft.notifictionary.services.smartnotification.SmartNotificationController
import ir.shahinsoft.notifictionary.services.smartnotification.SmartNotificationImpl
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

    var mCurrentClip: String? = null

    private lateinit var speaker: TextToSpeech

    private var isSpeakerReady = false

    private lateinit var learningService: LearningService

    private var smartNotificationController: SmartNotificationController? = null

    private lateinit var notificationController: NotificationController

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
                    if (translate != null && translate.length < COPY_MAX_TOAST_SIZE) {
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
        PathProvider.instance.init(this)
        learningService = LearningService()
        smartNotificationController = SmartNotificationImpl(this, learningService)
        notificationController = NotificationImpl(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_INIT -> initServiceForeground()
            ACTION_STOP -> cancelNotification()
            ACTION_INIT_CLIPBOARD -> initClipBoard()
            ACTION_STOP_LISTEN_CLIPBOARD -> stopListenClipboard()
            ACTION_INIT_NOTIFICATIONS -> triggerNextNotificationTime()
            ACTION_SPEAK -> speak(intent.getStringExtra(EXTRA_WORD))
            ACTION_NEW_PERIOD -> triggerNextNotificationTime()
            ACTION_SEND_TRANSLATE_NOTIFICATION -> sendTranslateNotification(intent.getIntExtra(EXTRA_ID, -1), intent.getStringExtra(EXTRA_TRANSLATE), intent.getStringExtra(EXTRA_LANG), intent)
            ACTION_DISMISS_NOTIFICATION -> dismissNotification(intent.getIntExtra(EXTRA_ID, -1), intent.getBooleanExtra(EXTRA_HAS_LEARNED, false))
            ACTION_USER_DISMISSED_NOTIFICATION -> userDismissedNotification(intent.getIntExtra(EXTRA_ID, -1), intent)
            ACTION_FORCE_NOTIFICATION -> forceANotifictionary()
        }

        return START_STICKY
    }

    private fun initLearningService() {
        PathProvider.instance.init(this)
        learningService = LearningService()
        smartNotificationController = SmartNotificationImpl(this, learningService)
        notificationController = NotificationImpl(this)
        triggerNextNotificationTime()
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

    private fun forceANotifictionary() {
        notificationController.cancel()
        smartNotificationController?.cancel()
        triggerNextNotificationTime()
    }

    private fun userDismissedNotification(id: Int, intent: Intent) {
        Log.d("NotifictionaryService", "user dismissed notification")
        if (isSmartNotificationActive()) {
            learningService.reward(intent.getIntExtra("state_id", 0),
                    Record.Action.values()[intent.getIntExtra("action", 0)], false)
            toast("agent received bad reward")
        }
        state = State.START
        triggerNextNotificationTime()
    }

    private fun dismissNotification(id: Int, hasLearned: Boolean) {
        NotificationUtil.cancelNotification(this)
        state = State.START
        triggerNextNotificationTime()

        if (hasLearned) {
            CountLearnTask(getAppDatabase(), id).execute()
        }
    }

    private fun sendTranslateNotification(id: Int, translate: String, translation: String?, intent: Intent) {
        Log.d("NotifictionaryService", "update translate $id")
        state = State.START
        if (smartNotificationController == null) {
            initLearningService()
        }
        smartNotificationController?.sendSmartTranslationNotification(id, translate, translation, intent)

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

    private fun triggerNextNotificationTime() {

        //if (!isForeground()) return

        //checking if sending notification is active
        val isNotificationEnabled = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("notifications_new_message", true)
        if (!isNotificationEnabled) return

        //checking if we are in correct state
        if (state != State.START) return

        //send notification
        val smartNotification = isSmartNotificationActive()
        if (smartNotification) {
            handleSmartNotification()
            return
        } else {
            handleNotification()
        }
    }

    private fun isForeground(): Boolean {
        val i = Intent(this, NotifictionaryService::class.java)
        return PendingIntent.getService(this, 0, i, PendingIntent.FLAG_NO_CREATE) != null
    }

    private fun handleNotification() {
        notificationController.addNextNotificationWorker()
    }

    private fun handleSmartNotification() {
        smartNotificationController?.addNextNotificationWorker()
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
        if (!isForeground()) {
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
    }

    private fun initNotification() {
        val channelId = "Notifictionary default channel"
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

        val notification = NotificationCompat.Builder(this, getString(R.string.app_name))
                .setContentTitle(getString(R.string.notifictionary_is_active))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .addAction(0, getString(R.string.close_notification), getCloseIntentPending())
                .addAction(0, getString(R.string.force_notification), getForceNotificationPendingIntent())
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setShowWhen(false)
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


    private fun getCloseIntentPending(): PendingIntent? {
        val i = Intent(this, NotifictionaryService::class.java)
        i.action = ACTION_STOP
        return PendingIntent.getService(this, 0, i, PendingIntent.FLAG_CANCEL_CURRENT)
    }

    private fun isOreoPlus(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    }

}
