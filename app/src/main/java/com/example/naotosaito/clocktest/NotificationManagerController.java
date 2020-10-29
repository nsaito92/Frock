package com.example.naotosaito.clocktest;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * NotificationManagerを使用するクラス
 * Created by nsaito on 2020/07/05.
 */

class NotificationManagerController {
    private final String TAG = NotificationManagerController.class.getSimpleName();
    private Context mContext = null;
    private NotificationManager manager;

    public NotificationManagerController(Context context) {
        Log.d(TAG, "");
        // NotificationManagerでNotificationを表示
        // contextを経由してインスタンス取得
        mContext = context;
        manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    // Notification ID
    public class NotificationID {
        final static int ALARMRINGING = 0;
        final static int SNOOZE = 1;

        final static int DEBUG = 100;
    }

    // Notification Channel ID
    public class NotificationChannelID {
        final static String ALARMRINGING = "0";
    }

    /**
     * アラーム鳴動中の通知を生成する
     */
    public void createNotificationAlarmRinging() {
        Log.d(TAG, "createNotificationAlarmRinging");

        // Android 8.0以降で必要な通知チャンネルの生成。未精製の場合、生成する。
        createNotificationChannel(
                NotificationChannelID.ALARMRINGING,
                mContext.getString(R.string.channel_name_alarmringing),
                manager.IMPORTANCE_DEFAULT);

        // 通知をタップした時のPendingIntent生成
        Intent intent_MainActivity = new Intent(mContext, MainActivity.class);
        PendingIntent pendingintent = PendingIntent.getActivity(
                mContext,
                ClockUtil.PendingIntentRequestCode.ALARM_NOTIFICATION,
                intent_MainActivity,
                PendingIntent.FLAG_CANCEL_CURRENT);

        // Notification生成
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, NotificationChannelID.ALARMRINGING);
        Notification notification = builder
                .setWhen(System.currentTimeMillis())
                .setContentTitle("アラーム鳴動中")
                .setContentText("アプリを開く場合はタップしてください")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("アラーム鳴動中")
                .setContentIntent(pendingintent)
                .build();

        manager.notify(NotificationID.ALARMRINGING, notification);
    }

    /**
     * スヌーズが動作中の通知を生成する
     */
    public void createNotificationSnooze() {
        Log.d(TAG, "createNotificationSnooze");

        // Android 8.0以降で必要な通知チャンネルの生成。未精製の場合、生成する。
        createNotificationChannel(
                NotificationChannelID.ALARMRINGING,
                mContext.getString(R.string.channel_name_alarmringing),
                manager.IMPORTANCE_DEFAULT);

        // 通知をタップした時のPendingIntent生成
        Intent intent_MainActivity = new Intent(mContext, MainActivity.class);
        PendingIntent pendingintent = PendingIntent.getActivity(
                mContext,
                ClockUtil.PendingIntentRequestCode.NOTIFICATION_SNOOZE,
                intent_MainActivity,
                PendingIntent.FLAG_CANCEL_CURRENT);

        // アクションボタンをタップした時の処理
        Intent actionIntent = new Intent(mContext, FrockReceiver.class);
        actionIntent.setAction(ClockUtil.BroadCast.SNOOZE_FINISH);

        PendingIntent actionButtonIntent = PendingIntent.getBroadcast(
                mContext,
                ClockUtil.PendingIntentRequestCode.SNOOZE_FINISH,
                actionIntent,
                0
        );

        // Notification生成
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, NotificationChannelID.ALARMRINGING);
        Notification notification = builder
                .setWhen(System.currentTimeMillis())
                .setContentTitle("スヌーズ動作中")
                .setContentText("5分後に再度アラームが実行されます。アプリを開く場合はタップしてください")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("スヌーズ動作中")
                .setContentIntent(pendingintent)
                .addAction(R.mipmap.ic_launcher, "解除", actionButtonIntent)
                .build();

        manager.notify(NotificationID.SNOOZE, notification);
    }

    /**
     * 指定されたIDの通知を削除する。
     * @param id
     */
    public void notificationCansel(int id) {
        Log.d(TAG, "notificationCansel");

        NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(id);
    }

    /**
     * Android 8.0以降で必要な通知チャンネルの生成処理
     * @param id
     */
    private void createNotificationChannel(String id, CharSequence name, int importance) {
        Log.d(TAG, "createNotificationChannel");

        if (id == null || name == null) {
            return;
        }

        // 渡されたIDのチャンネルが生成済みだった場合は、return。
        if (manager.getNotificationChannel(id) != null) {
            return;
        }
        // 端末OSverが、8.0未満の場合はreturn。
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }

        // NotificationChannelの初期化
        NotificationChannel channel = new NotificationChannel(
                id, name, importance);

        // チャンネル生成実行
        manager.createNotificationChannel(channel);
    }
}
