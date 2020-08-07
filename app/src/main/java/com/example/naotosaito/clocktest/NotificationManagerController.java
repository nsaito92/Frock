package com.example.naotosaito.clocktest;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

/**
 * NotificationManagerを使用するクラス
 * Created by nsaito on 2020/07/05.
 */

class NotificationManagerController {
    private Context mContext = null;
    private NotificationManager manager;

    public NotificationManagerController(Context context) {
        // NotificationManagerでNotificationを表示
        // contextを経由してインスタンス取得
        mContext = context;
        manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    // Notification ID
    public class NotificationID {
        final static int ALARMRINGING = 0;
        final static int SNOOZE = 1;
    }

    /**
     * アラーム鳴動中の通知を生成する
     */
    public void createNotificationAlarmRinging() {
        // 通知をタップした時のPendingIntent生成
        Intent intent_MainActivity = new Intent(mContext, MainActivity.class);
        PendingIntent pendingintent = PendingIntent.getActivity(
                MyApplication.getContext(),
                ClockUtil.PendingIntentRequestCode.ALARM_NOTIFICATION,
                intent_MainActivity,
                PendingIntent.FLAG_CANCEL_CURRENT);

        // Notification生成
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
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
        // 通知をタップした時のPendingIntent生成
        Intent intent_MainActivity = new Intent(mContext, MainActivity.class);
        PendingIntent pendingintent = PendingIntent.getActivity(
                MyApplication.getContext(),
                ClockUtil.PendingIntentRequestCode.NOTIFICATION_SNOOZE,
                intent_MainActivity,
                PendingIntent.FLAG_CANCEL_CURRENT);

        // アクションボタンをタップした時の処理
        Intent actionIntent = new Intent(mContext, FrockReceiver.class);
        actionIntent.setAction(ClockUtil.BroadCast.SNOOZE_FINISH);

        PendingIntent actionButtonIntent = PendingIntent.getBroadcast(
                MyApplication.getContext(),
                ClockUtil.PendingIntentRequestCode.SNOOZE_FINISH,
                actionIntent,
                0
        );

        // Notification生成
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
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
        NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(id);
    }
}
