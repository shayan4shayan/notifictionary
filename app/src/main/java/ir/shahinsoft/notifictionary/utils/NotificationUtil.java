package ir.shahinsoft.notifictionary.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import ir.shahinsoft.notifictionary.ConstantsKt;
import ir.shahinsoft.notifictionary.R;
import ir.shahinsoft.notifictionary.activities.AssayActivity;
import ir.shahinsoft.notifictionary.model.Translate;
import ir.shahinsoft.notifictionary.services.NotifictionaryService;

import static android.app.Notification.DEFAULT_ALL;
import static ir.shahinsoft.notifictionary.ConstantsKt.ACTION_DISMISS_NOTIFICATION;
import static ir.shahinsoft.notifictionary.ConstantsKt.EXTRA_HAS_LEARNED;
import static ir.shahinsoft.notifictionary.ConstantsKt.EXTRA_ID;
import static ir.shahinsoft.notifictionary.ConstantsKt.EXTRA_LANG;
import static ir.shahinsoft.notifictionary.ConstantsKt.EXTRA_TRANSLATE;
import static ir.shahinsoft.notifictionary.ConstantsKt.EXTRA_WORD;

/**
 * Created by shayan4shayan on 4/4/18.
 */

public class NotificationUtil {

    public static final String ID = "notifictionary word reminder";
    public static final String NAME = "Remember";
    public static final int REMINDER_ID = 0x12000;
    public static final int NOTIFICATION_ID = 5;

    public static void sendNotification(Context context, Intent intent, Translate translate) {

        boolean isNotificationsEnabled = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("notifications_new_message", true);
        if (!isNotificationsEnabled) return;

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(ID, NAME, NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }

        //android wear action
        NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.mipmap.ic_flip,
                context.getString(R.string.see_translation),
                getDisplayTranslatePendingIntent(context, intent, translate)
        ).build();


        boolean isSoundEnable = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean("pref_notification_sound", true);
        boolean isVibrateEnable = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean("pref_notification_vibration", true);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, ID);
        builder.setSmallIcon(R.drawable.ic_launcher_foreground)
                .setChannelId(ID)
                .setAutoCancel(false)
                .setContent(getNotificationContent(context, intent, translate))
                .setContentText(translate.getName())
                .setDeleteIntent(getReadPendingIntent(context, intent, translate.getId()))
                .extend(new NotificationCompat.WearableExtender().addAction(action))
                .setPriority(100);

        if (isSoundEnable) {
            builder.setDefaults(Notification.DEFAULT_SOUND);
        }

        if (isVibrateEnable) {
            builder.setDefaults(Notification.DEFAULT_VIBRATE);
        }

        Notification notification = builder.build();
        if (manager != null) {
            manager.notify(ID, NOTIFICATION_ID, notification);
        }

    }


    public static void sendTranslateNotification(Context context, int id, String translate, String translation) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //manager.cancel(id);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(ID, NAME, NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }

        //android wear actions


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, ID);
        builder.setSmallIcon(R.drawable.ic_launcher_foreground)
                .setChannelId(ID)
                .setAutoCancel(true)
                .setContent(getNotificationTranslateContentContent(context, translate, translation, id))
                .setDeleteIntent(getForgetPendingIntent(context,id))
                .setPriority(100);

        Notification notification = builder.build();
        manager.notify(ID, NOTIFICATION_ID, notification);
    }

    private static RemoteViews getNotificationTranslateContentContent(Context context, String translate, String translation, int id) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.notification_translate_view);
        views.setTextViewText(R.id.textTranslate, translate);
        views.setImageViewResource(R.id.logo, R.drawable.ic_launcher_foreground);
        views.setImageViewResource(R.id.btnLearn, R.mipmap.ic_check);
        views.setImageViewResource(R.id.btnForget, R.mipmap.ic_close);
        views.setOnClickPendingIntent(R.id.btnLearn, getLearnPendingIntent(context, id));
        views.setOnClickPendingIntent(R.id.btnForget, getForgetPendingIntent(context, id));
        views.setTextViewText(R.id.text_translation, translation);
        return views;
    }

    private static PendingIntent getForgetPendingIntent(Context context, int id) {
        Intent intent = new Intent(context, NotifictionaryService.class);
        intent.putExtra(EXTRA_ID, id);
        intent.setAction(ACTION_DISMISS_NOTIFICATION);
        intent.putExtra(EXTRA_HAS_LEARNED, false);
        return PendingIntent.getService(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static PendingIntent getLearnPendingIntent(Context context, int id) {
        Intent intent = new Intent(context, NotifictionaryService.class);
        intent.putExtra(EXTRA_ID, id);
        intent.setAction(ACTION_DISMISS_NOTIFICATION);
        intent.putExtra(EXTRA_HAS_LEARNED, true);
        return PendingIntent.getService(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static RemoteViews getNotificationContent(Context context, Intent intent, Translate translate) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.notification);
        views.setImageViewResource(R.id.logo, R.drawable.ic_launcher_foreground);
        views.setTextViewText(R.id.textWord, translate.getName());
        views.setImageViewResource(R.id.btnFlip, R.mipmap.ic_flip);
        views.setOnClickPendingIntent(R.id.btnFlip, getDisplayTranslatePendingIntent(context, intent, translate));
        views.setTextViewText(R.id.text_translation, translate.getLang());
        return views;
    }

    private static PendingIntent getDisplayTranslatePendingIntent(Context context, Intent intent, Translate translate) {
        Intent actionIntent = new Intent(context, NotifictionaryService.class);
        actionIntent.setAction(ConstantsKt.ACTION_SEND_TRANSLATE_NOTIFICATION);
        actionIntent.putExtra(EXTRA_ID, translate.getId());
        actionIntent.putExtra(EXTRA_WORD, translate.getName());
        actionIntent.putExtra(EXTRA_TRANSLATE, translate.getTranslate());
        actionIntent.putExtra(EXTRA_LANG, translate.getLang());
        actionIntent.putExtra("state_id", intent.getIntExtra("state_id", 0));
        actionIntent.putExtra("is_smart", intent.getBooleanExtra("is_smart", true));
        actionIntent.putExtra("action", intent.getIntExtra("action", 0));
        return PendingIntent.getService(context, translate.getId(), actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static PendingIntent getReadPendingIntent(Context context, Intent intent, int translateId) {
        Log.d("notificationUtil", "notificationId:" + translateId);
        Intent actionIntent = new Intent(context, NotifictionaryService.class);
        actionIntent.setAction(ConstantsKt.ACTION_USER_DISMISSED_NOTIFICATION);
        actionIntent.putExtra(EXTRA_ID, translateId);
        actionIntent.putExtra("state_id", intent.getIntExtra("state_id", 0));
        actionIntent.putExtra("is_smart", intent.getBooleanExtra("is_smart", true));
        actionIntent.putExtra("action", intent.getIntExtra("action", 0));
        return PendingIntent.getService(context, translateId, actionIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    public static void sendRemindNotification(Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        boolean isSoundEnable = context.getSharedPreferences(ConstantsKt.APP, Context.MODE_PRIVATE)
                .getBoolean(ConstantsKt.SOUND, false);
        boolean isVibrateEnable = context.getSharedPreferences(ConstantsKt.APP, Context.MODE_PRIVATE)
                .getBoolean(ConstantsKt.VIBRATE, false);


        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, ID);
        builder.setSmallIcon(R.drawable.ic_notification_icon)
                .setContentTitle(context.getString(R.string.app_name))
                .setLargeIcon(bitmap)
                .setAutoCancel(false)
                .setPriority(100);

        if (isSoundEnable) {
            builder.setDefaults(Notification.DEFAULT_SOUND);
        } else {
            builder.setDefaults(DEFAULT_ALL);
        }
        if (isVibrateEnable) {
            builder.setDefaults(Notification.DEFAULT_VIBRATE);
        } else {
            builder.setVibrate(new long[]{});
        }


        Notification notification = builder.build();

        if (manager != null) {
            manager.notify(ID, REMINDER_ID, notification);
        }
    }

    private static PendingIntent getPendingIntentForExam(Context context) {
        Intent intent = new Intent(context, AssayActivity.class);
        return PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void cancelRemindNotification(Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.cancel(ID, REMINDER_ID);
        }
    }

    public static void cancelNotification(Context context, int id) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.cancel(ID, id);
        }
    }
}
