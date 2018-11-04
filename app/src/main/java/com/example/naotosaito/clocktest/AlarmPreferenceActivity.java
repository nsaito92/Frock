package com.example.naotosaito.clocktest;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.os.Bundle;
import android.preference.SwitchPreference;
import android.util.Log;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * アラーム設定画面表示用のActivity
 */

public class AlarmPreferenceActivity extends PreferenceActivity {
    private static final String TAG = "AlarmPreferenceActivity";

    final static String ALARMTIME_HOUR_KEY = "alarmtime_hour";
    final static String ALARMTIME_MINUTE_KEY = "alarmtime_minute";

    AlarmPreferenceFragment mFragment;
    SwitchPreference alarmbutton;

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

        // アラーム設定の有効・無効を設定するトグルボタンを押した時の動作。
        // (SwitchPreference)でキャストし、findPreferenceを実行。
        alarmbutton = (SwitchPreference)mFragment.findPreference(getString(R.string.alarmboolean_key));
        alarmbutton.setChecked(false); //初期値を指定。

        alarmbutton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(){
            // ここでif(alarmbutton.isChecked())
            public boolean onPreferenceClick(Preference pref) {
                Log.d(TAG, "onCreate#alarmbutton onPreferencelick");
                return true;
            }
        });

        /* ボタンの押したときの動作
         * PreferenceActivity#findPreferenceはAPIレベル11以降非推奨となっているため、
         * APIレベルが11以上の場合、PreferenceFragment#findPreferenceをコールする。
         */
        // TODO この辺の処理、同じようなこと書いているので、メソッド化したい。
        Preference btn_alarmtime_key = null;

        // ここから
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            btn_alarmtime_key = mFragment.findPreference("alarmtime_key");
        } else {
            btn_alarmtime_key = findPreference(getString(R.string.alarmtime_key));
        }
        // ここまで


        btn_alarmtime_key.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(){
            @Override
            public boolean onPreferenceClick(Preference pref) {
                Log.d(TAG, "onCreate#onPreferencelick_alarmtime_key");
                alarmTimeSetting();
                return true;
            }
        });

        // 曜日選択ボタンの設定
        Preference btn_alarm_start_week_key = null;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            btn_alarm_start_week_key = mFragment.findPreference("alarm_start_week_key");
        } else {
            btn_alarm_start_week_key = findPreference(getString(R.string.alarm_start_week_key));
        }

        btn_alarm_start_week_key.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(){
            @Override
            public boolean onPreferenceClick(Preference pref) {
                Log.d(TAG, "onCreate#onPreferencelick_alarm_start_week_key");
                alarmWeekSetting();
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        // Preferenceの値が変更された時に呼び出されるコールバック関数をregist
        SharedPreferences prefer_hour = getSharedPreferences("hour", MODE_PRIVATE);
        prefer_hour.registerOnSharedPreferenceChangeListener(listener);

        SharedPreferences prefer_minute = getSharedPreferences("minute", MODE_PRIVATE);
        prefer_minute.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");

        // Preferenceの値が変更された時に呼び出されるコールバック関数unregist
        SharedPreferences prefer_hour = getSharedPreferences("hour", MODE_PRIVATE);
        prefer_hour.unregisterOnSharedPreferenceChangeListener(listener);

        SharedPreferences prefer_minute = getSharedPreferences("minute", MODE_PRIVATE);
        prefer_minute.unregisterOnSharedPreferenceChangeListener(listener);
    }

    private SharedPreferences.OnSharedPreferenceChangeListener listener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {

                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    Log.d(TAG, "onSharedPreferenceChanged");
                    Preference button = null;
                    button = mFragment.findPreference("alarmtime_key");

                    // アラームの時間か分のキーの場合、サマリーを保存する処理を行う。
                    if(ALARMTIME_HOUR_KEY.equals(key) || ALARMTIME_MINUTE_KEY.equals(key)) {

                        // Preferenceへのアクセス
                        SharedPreferences prefer_hour = getSharedPreferences("hour", MODE_PRIVATE);
                        SharedPreferences prefer_minute = getSharedPreferences("minute", MODE_PRIVATE);

                        // 保存されているPreferenceの値を取得
                        int spHourInt = prefer_hour.getInt(ALARMTIME_HOUR_KEY, MODE_PRIVATE);
                        int spMinuteInt = prefer_minute.getInt(ALARMTIME_MINUTE_KEY, MODE_PRIVATE);

                        // Preferenceを文字列に変換する。
                        String valueOfH = String.valueOf(spHourInt);
                        String valueOfM = String.valueOf(spMinuteInt);

                        // 時間と分を、一つの文字列に統合して、画面に表示する。
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(valueOfH);
                        stringBuilder.append(":");
                        stringBuilder.append(valueOfM);
                        button.setSummary(stringBuilder.toString());
                    }
                }
            };

    // アラーム時間を設定するダイアログを表示させ、入力された値をアラーム時刻として保存する
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

                // AlarmServiceを起動する時間を更新する
                alarmServiceSetting();
            }
        }, hour, minute, true);
        dialog.show();
    }

    // アラームを実行するための設定を行う
    private void alarmServiceSetting() {
        Log.d(TAG, "alarmServiceSetting");

        // AlarmService起動用のIntent、PendingIntentを作成
        Context context = getBaseContext();
        Intent intent = new Intent(AlarmPreferenceActivity.this, AlarmService.class);
        int requestcode = 1;
        PendingIntent pendingintent = PendingIntent.getService(
                context, requestcode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Preferenceへのアクセス
        SharedPreferences prefer_hour = getSharedPreferences("hour", MODE_PRIVATE);
        SharedPreferences prefer_minute = getSharedPreferences("minute", MODE_PRIVATE);

        // 保存されているPreferenceの値を取得
        int spHourInt = prefer_hour.getInt(ALARMTIME_HOUR_KEY, MODE_PRIVATE);
        int spMinuteInt = prefer_minute.getInt(ALARMTIME_MINUTE_KEY, MODE_PRIVATE);

        // アラームを実行する時間の設定を準備
        Calendar calender = Calendar.getInstance();
        calender.setTimeInMillis(0);
        calender.set(Calendar.YEAR, 2018);         // 年
        calender.set(Calendar.MONTH, Calendar.OCTOBER); // 月
        calender.set(Calendar.DAY_OF_MONTH, 16);    // 日
        calender.set(Calendar.HOUR_OF_DAY, prefer_hour.getInt(ALARMTIME_HOUR_KEY, MODE_PRIVATE));     // 時
        calender.set(Calendar.MINUTE, prefer_minute.getInt(ALARMTIME_MINUTE_KEY, MODE_PRIVATE));          // 分

        // AlarmManagerのset()でAlarmManagerでセットした時間に、Serviceを起動
        AlarmManager alarmmanager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmmanager.set(AlarmManager.RTC, calender.getTimeInMillis(), pendingintent);
        Log.d(TAG, "AlarmSettingTime is "
                + calender.YEAR
                + calender.MONTH
                + calender.DAY_OF_MONTH
                + calender.HOUR_OF_DAY
                + calender.MINUTE
                + calender.SECOND + " !!");
    }

    /**
     * アラームの曜日を設定するダイアログダイアログを表示させ、選択された曜日を保存する
     */
    private void alarmWeekSetting() {
        Log.d(TAG, "alarmWeekSetting");

        // ダイアログを表示するため、FragmentManagerを取得する。
        FragmentManager manager = getFragmentManager();
        DatePickerDialogFragment dialog = new DatePickerDialogFragment();
        dialog.show(manager, "alarm_Week_Setting_dialog");
    }

    /**
     * アラームの動作する曜日を選択できるダイアログを表示させる。
     */
    public class DatePickerDialogFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            //
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("曜日");
            builder.setPositiveButton("OK", null);
            builder.setNegativeButton("Cansel", null);

            return builder.create();
        }
    }
}
