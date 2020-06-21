package com.example.naotosaito.clocktest;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

/**
 * AlarmManager・PendingIntentを使用して、AlarmServiceをセットするクラス。
 * Created by nsaito on 2020/06/12.
 */

class AlarmServiceSetter {
    private final static String TAG = AlarmServiceSetter.class.getSimpleName();

    /**
     *
     */
    public void UpdateAlarmService() {
        Log.d(TAG, "UpdateAlarmService");
        // TODO DBのクエリを叩いて、ONになっているアラーム設定を取得。
        FrockSettingsHelperController controller = new FrockSettingsHelperController();
        Calendar closestcalender = controller.getClosestCalender();
        Log.d(TAG, "closestcalender = " + closestcalender.getTime());

        if (closestcalender != null) {
            // TODO 一番近いCalenderをAlarmManagerにセットする。

        } else {
            // TODO クエリの結果が何も取得できなければ、現在設定されているアラーム設定をキャンセルする。
        }
    }

    /**
     * AlarmServiceに必要なデータを取得して、AlarmManagerに起動予定をセットする。
     * @param requestcode PendingIntentにセットするID
     */
    public void AlarmManagerSet(int requestcode) {
        Log.d(TAG, "AlarmManagerSet : requestcode = " + requestcode);

        // PendingIntent生成。
        Context context = MyApplication.getContext();
        Intent intent = new Intent(context, AlarmService.class);

        intent.putExtra("requestcode", String.valueOf(requestcode));
        PendingIntent pendingintent = PendingIntent.getService(
                context, requestcode, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Calender生成。
        FrockSettingsHelperController controller = new FrockSettingsHelperController();
        Calendar calendar = controller.CreateCalendarFromDB(requestcode);

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
     * @param mId
     */
    public void AlarmManagerCancel(int mId) {
        Log.d(TAG, "AlarmManagerCancel : mId = " + mId);

        Context context = MyApplication.getContext();

        AlarmManager alarmmanager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmService.class);
        PendingIntent pendingintent = PendingIntent.getService(
                context, mId, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        pendingintent.cancel();
        alarmmanager.cancel(pendingintent);
    }
}
