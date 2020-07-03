package com.example.naotosaito.clocktest;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.Calendar;

/**
 * AlarmManager・PendingIntentを使用して、AlarmServiceをセットするクラス。
 * Created by nsaito on 2020/06/12.
 */

class AlarmServiceSetter {
    private final static String TAG = AlarmServiceSetter.class.getSimpleName();

    /**
     * DBデータを元にアラームを設定するCalenderを取得し、AlarmManagerにセットする。
     */
    public void updateAlarmService() {
        Log.d(TAG, "updateAlarmService");
        // DBのクエリを叩いて、ONになっているアラーム設定を取得。
        FrockSettingsHelperController controller = new FrockSettingsHelperController();
        Calendar closestcalender = controller.getClosestCalender();
        Log.d(TAG, "closestcalender = " + closestcalender.getTime());

        if (closestcalender != null) {
            // 一番近いCalenderをAlarmManagerにセットする。
            alarmManagerSet(closestcalender);

            // 明示的にReceiverを有効にする。
            ComponentName receiver = new ComponentName(MyApplication.getContext(), FrockReceiver.class);
            PackageManager packageManager = MyApplication.getContext().getPackageManager();

            packageManager.setComponentEnabledSetting(
                    receiver,
                    packageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    packageManager.DONT_KILL_APP);

        } else {
            // クエリの結果が何も取得できなければ、現在設定されているアラーム設定をキャンセルする。
            alarmManagerCancel();

            // 明示的にReceiverを無効にする。
            ComponentName receiver = new ComponentName(MyApplication.getContext(), FrockReceiver.class);
            PackageManager packageManager = MyApplication.getContext().getPackageManager();

            packageManager.setComponentEnabledSetting(
                    receiver,
                    packageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    packageManager.DONT_KILL_APP);
        }
    }

    /**
     * AlarmServiceに必要なデータを取得して、AlarmManagerに起動予定をセットする。
     * @param calendar AlarmManagerにセットするCalender
     */
    private void alarmManagerSet(Calendar calendar) {
        Log.d(TAG, "AlarmManagerSet");

        // PendingIntent生成。
        Context context = MyApplication.getContext();
        Intent intent = new Intent(context, AlarmService.class);

        PendingIntent pendingintent = PendingIntent.getService(
                context, ClockUtil.AlarmManagerRequestCode.ALARMSERVICE, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        // AlarmServiceの起動予定をAlarmManagerにset。
        AlarmManager alarmmanager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmmanager.set(
                AlarmManager.RTC,
                calendar.getTimeInMillis(),
                pendingintent);

        Log.d(TAG, "Set " + calendar.getTime() + " to AlarmManager");
    }

    /**
     * AlarmManagerにセット済みのAlarmServiceをキャンセルする。
     */
    private void alarmManagerCancel() {
        Log.d(TAG, "alarmManagerCancel");

        Context context = MyApplication.getContext();

        AlarmManager alarmmanager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmService.class);
        PendingIntent pendingintent = PendingIntent.getService(
                context, ClockUtil.AlarmManagerRequestCode.ALARMSERVICE, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        pendingintent.cancel();
        alarmmanager.cancel(pendingintent);
    }
}
