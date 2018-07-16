package com.example.naotosaito.clocktest;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TimePicker;

import java.util.Calendar;

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
                alarmTimeSetting();
                return true;
            }
        });
    }

    // アラーム時間を設定するダイアログを表示する
    private void alarmTimeSetting() {
        Log.d(TAG, "alarmTimeSetting");
        // ダイアログの初期選択状態を現在の時間にするため、Calender.getで現在時間を取得している。
        Calendar calender = Calendar.getInstance();
        int hour = calender.get(Calendar.HOUR_OF_DAY);
        int minute = calender.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Log.d(TAG, String.format("Alarm Start Time = %02d:%02d", hourOfDay, minute));

                // アラーム時間をPreferenceで保存する
                // Preferenceへのアクセス
                SharedPreferences prefer_hour = getSharedPreferences("hour", MODE_PRIVATE);
                SharedPreferences prefer_minute = getSharedPreferences("minute", MODE_PRIVATE);

                // Preferenceの保存
                SharedPreferences.Editor editor_hour = prefer_hour.edit();
                editor_hour.putInt("alarmtime_hour", hourOfDay);
                editor_hour.commit();

                SharedPreferences.Editor editor_minute = prefer_minute.edit();
                editor_minute.putInt("alarmtime_minute", minute);
                editor_minute.commit();

            }
        }, hour, minute, true);
        dialog.show();
    }
}
