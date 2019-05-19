package com.example.naotosaito.clocktest;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by naotosaito on 2019/03/10.
 *
 * アプリケーション全体で共通の処理を行う。
 */

public class ClockUtil {
    private static final String TAG = "ClockUtil";

    final static String PENDING_ALARMSERVICE_KEY = "pendingalarmservice_boolean";

    // 間違ってインスタンスを生成された場合、コンストラクタで例外を返す。
    private ClockUtil() {
        throw new AssertionError();
    }

    /**
     * Serviceの起動状態をチェックする。
     * @return Serviceの起動中の場合はtrue、起動していない場合はfalseを返す。
     */
    public static boolean isYourServiceWorking() {
        Log.d(TAG, "isYourServiceWorking");
        ActivityManager manager = (ActivityManager) MyApplication.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo serviceinfo :
                manager.getRunningServices(Integer.MAX_VALUE)) {
            // TODO AlarmService以外のServiceでも共通利用できる様にしたい。
            if(AlarmService.class.getName().equals(serviceinfo.service.getClassName())) {
                Log.d(TAG, "isYourServiceWorking = " + true);
                return true;
            }
        }
        Log.d(TAG, "isYourServiceWorking = " + false);
        return false;
    }

    /**
     * AlarmServiceの起動するPendingIntentの状態をセットする。
     * @param value PendingIntentを有無
     */
    public static void setAlarmPendingIntent(boolean value) {
        // Preferenceへのアクセス
        SharedPreferences pref_almvalue =
                MyApplication.getContext().getSharedPreferences("PendingAlarm", Context.MODE_PRIVATE);

        // Preferenceの保存
        SharedPreferences.Editor editor_almvalue = pref_almvalue.edit();
        editor_almvalue.putBoolean(PENDING_ALARMSERVICE_KEY, value);
        editor_almvalue.commit();
    }

    /**
     * Pending中のアラーム設定の有無を返す。
     * @return Pending中のアラーム設定の有無
     */
    public static boolean getAlarmPendingIntent() {
        SharedPreferences pref_almvalue =
                MyApplication.getContext().getSharedPreferences("PendingAlarm", Context.MODE_PRIVATE);
        return pref_almvalue.getBoolean(PENDING_ALARMSERVICE_KEY, false);
    }

    /**
     * 受け取った時間の文字列の結合、調整をして返却する。
     * @param hour 時
     * @param minute 分
     * @return 「xx:xx」という形式に変換して返却
     */
    public static StringBuilder shapingStringTime(String hour, String minute) {

        Log.d("NSAITOTEST", "shapingStringTime");

        StringBuilder stringBuilder = new StringBuilder();

        // 文字列の調整。時間と分を、一つの文字列に統合。
        // 一桁の場合見栄えが悪いので、「0」を追加する。
        if (hour.length() < 2) {
            stringBuilder.insert(0, "0");
        }
        stringBuilder.append(hour);
        stringBuilder.append(":");
        if (minute.length() < 2) {
            stringBuilder.append("0");
        }
        stringBuilder.append(minute);

        return stringBuilder;
    }
}
