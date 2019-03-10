package com.example.naotosaito.clocktest;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

/**
 * Created by naotosaito on 2019/03/10.
 *
 * アプリケーション全体で共通の処理を行う。
 */

public class ClockUtil {
    private static final String TAG = "ClockUtil";

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
}
