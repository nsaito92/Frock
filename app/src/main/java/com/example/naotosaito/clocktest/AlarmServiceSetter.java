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
     * AlarmServiceに必要なデータを取得して、AlarmManagerに起動予定をセットする。
     * @param mId PendingIntentにセットするID
     */
    public void AlarmManagerSet(int mId) {
        Log.d(TAG, "AlarmManagerSet : mId = " + mId);

        // PendingIntent生成。
        Context context = MyApplication.getContext();
        Intent intent = new Intent(context, AlarmService.class);
        PendingIntent pendingintent = PendingIntent.getService(
                context, mId, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Calender生成。
        FrockSettingsHelperController controller = new FrockSettingsHelperController();
        Calendar calendar = controller.CreateCalendarFromDB(mId);

        // AlarmServiceの起動予定をAlarmManagerにset。
        AlarmManager alarmmanager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmmanager.set(
                AlarmManager.RTC,
                calendar.getTimeInMillis(),
                pendingintent);

        Log.d(TAG, "The alarm was set at " + calendar.getTime());
        ClockUtil.ToastShow("The alarm was set at " + calendar.getTime());
    }
}
