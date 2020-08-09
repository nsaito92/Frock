package com.example.naotosaito.clocktest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/** BroadcastReceiver継承クラス。
 * Created by nsaito on 2020/07/01.
 */

public class FrockReceiver extends BroadcastReceiver {
    final static String TAG = FrockReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive, intent : " + intent.getAction());

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            // 端末再起動時、アラーム設定はクリアされるため再度設定する。
            ClockUtil.setPrefInt("alarmservice", ClockUtil.SharedPreferencesKey.SNOOZE_COUNT, 0);
            AlarmServiceSetter setter = new AlarmServiceSetter();
            setter.updateAlarmService();

        } else if (intent.getAction().equals(ClockUtil.BroadCast.SNOOZE_FINISH)) {
            if (ClockUtil.isYourServiceWorking()) {
                // 鳴動中アラームサービスを終了する。
                // アラームを終了して、Activityを終了する。
                Intent stopServiceIntent = new Intent(MyApplication.getContext(), AlarmService.class);
                MyApplication.getContext().stopService(stopServiceIntent);
            }

            // スヌーズカウントをリセット
            ClockUtil.setPrefInt("alarmservice", ClockUtil.SharedPreferencesKey.SNOOZE_COUNT, 0);

            // スヌーズ通知のキャンセル
            NotificationManagerController controller = new NotificationManagerController(MyApplication.getContext());
            controller.notificationCansel(NotificationManagerController.NotificationID.SNOOZE);

            // 次のアラーム予定の再設定する。
            AlarmServiceSetter setter = new AlarmServiceSetter();
            setter.updateAlarmService();
        }
    }
}
