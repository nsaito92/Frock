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

    public NotificationManagerController(Context context) {
        mContext = context;
    }

    // Notification ID
    public class NotificationID {
        final static int ALARMRINGING = 0;
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

        // NotificationManagerでNotificationを表示
        // contextを経由してインスタンス取得
        NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NotificationID.ALARMRINGING, notification);
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
