package com.example.naotosaito.clocktest;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * AlarmServiceを起動状態を監視する。所定の時間を経過したらAlarmServiceを終了する。
 */
public class AlarmServiceObserver extends IntentService {
    private final String TAG = AlarmServiceObserver.class.getSimpleName();
//    private final int ALARMSERVICE_SURVIVAL_TIME = 300000;
    private final int ALARMSERVICE_SURVIVAL_TIME = 10000; // Test用10秒設定

    public AlarmServiceObserver() {
        super("AlarmServiceObserver");
        Log.d(TAG, "");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "onHandleIntent");

        try {
            Thread.sleep(ALARMSERVICE_SURVIVAL_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // アラームを終了。
            Intent stopServiceIntent = new Intent(MyApplication.getContext(), AlarmService.class);
            stopService(stopServiceIntent);

            // アラーム鳴動Activity終了イベントを投げる。
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(ClockUtil.BroadCast.CALL_ALARMDIALOG_FINISH);
            sendBroadcast(broadcastIntent);

            int count = ClockUtil.getPrefInt("alarmservice", ClockUtil.SharedPreferencesKey.SNOOZE_COUNT);
            if (count < 5) {
                // スヌーズ中
                ClockUtil.setPrefInt("alarmservice", ClockUtil.SharedPreferencesKey.SNOOZE_COUNT, count + 1);

                // 通知を表示する
                NotificationManagerController controller = new NotificationManagerController(MyApplication.getContext());
                controller.createNotificationSnooze();

            } else {
                // スヌーズ終了。カウントをリセット
                ClockUtil.setPrefInt("alarmservice", ClockUtil.SharedPreferencesKey.SNOOZE_COUNT, 0);

                // 通知のキャンセル
                NotificationManagerController controller = new NotificationManagerController(MyApplication.getContext());
                controller.notificationCansel(NotificationManagerController.NotificationID.SNOOZE);
            }

            // 次のアラーム予定の再設定する。
            AlarmServiceSetter setter = new AlarmServiceSetter();
            setter.updateAlarmService();
        }
    }
}
