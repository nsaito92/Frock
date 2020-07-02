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

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            // 端末再起動時、アラーム設定はクリアされるため再度設定する。
            AlarmServiceSetter setter = new AlarmServiceSetter();
            setter.updateAlarmService();
        }
    }
}
