package com.example.naotosaito.clocktest;

import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.os.Bundle;
import android.util.Log;

/**
 * アラーム設定画面表示用のActivity
 */

public class AlarmPreferenceActivity extends PreferenceActivity {
    private static final String TAG = "AlarmPreferenceActivity";
    AlarmPreferenceFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        mFragment = new AlarmPreferenceFragment();
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, mFragment).commit();
        // メインスレッドで保留中となるため、AlarmPreferenceFragment#onCreateがコールされない。
        // findpreferenceを実行したいため、executePendingTransactionsを呼び出す。
        getFragmentManager().executePendingTransactions();

        /* ボタンの押したときの動作
         * PreferenceActivity#findPreferenceはAPIレベル11以降非推奨となっているため、
         * APIレベルが11以上の場合、PreferenceFragment#findPreferenceをコールする。
         */
        Preference button = null;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            button = mFragment.findPreference("alarmtime_key");
        } else {
            button = findPreference(getString(R.string.alarmtime_key));
        }

        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(){
            @Override
            public boolean onPreferenceClick(Preference pref) {
                Log.d(TAG, "onCreate#onPreferencelick");
                return true;
            }
        });
    }
}
