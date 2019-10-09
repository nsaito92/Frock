package com.example.naotosaito.clocktest;

import android.app.AlarmManager;
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
        alarmbutton.setChecked(ClockUtil.getPrefBoolean("alarmservice", ClockUtil.ALARM_SERVICE_KEY)); //初期値を指定。

        alarmbutton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(){
            public boolean onPreferenceClick(Preference pref) {
                Log.d(TAG, "onCreate#alarmbutton onPreferencelick");

                // トグルボタンの状態をチェックして結果をPreferenceに保存する。
                boolean value = alarmbutton.isChecked();

                if (value) {
                    // trueになった場合は、有効なアラーム設定がある場合は、アラーム設定を行う。
                    ClockUtil.setPrefBoolean("alarmservice", ClockUtil.ALARM_SERVICE_KEY, value);
                    ClockUtil.alarmServiceSet();
                } else if (!value) {
                    // falseになった場合は、アラーム鳴動予定がある場合は、無効にする。
                    ClockUtil.setPrefBoolean("alarmservice", ClockUtil.ALARM_SERVICE_KEY, value);
                    alarmServiceCansel();
                }
                return alarmbutton.isChecked();
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
                alarmTimeSetDialogShow();
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
                alarmWeekDialogshow();
                return true;
            }
        });

        // アラーム鳴動時間、曜日設定の表示を、最新に更新。
        updateTimeView();
        updateWeekView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        // Preferenceの値が変更された時に呼び出されるコールバック関数をregist
        // TODO アラーム設定の更新も、ここでやるのがよさそう。
        SharedPreferences prefer_hour = getSharedPreferences("hour", MODE_PRIVATE);
        prefer_hour.registerOnSharedPreferenceChangeListener(listener);

        SharedPreferences prefer_minute = getSharedPreferences("minute", MODE_PRIVATE);
        prefer_minute.registerOnSharedPreferenceChangeListener(listener);

        SharedPreferences prefer_week = getSharedPreferences("week", MODE_PRIVATE);
        prefer_week.registerOnSharedPreferenceChangeListener(listener);

        // AlarmServiceが起動中・Pending中共にない場合は、トグルボタンを更新する。
        if (!ClockUtil.isYourServiceWorking() && !ClockUtil.getAlarmPendingIntent()) {
            ClockUtil.setPrefBoolean("alarmservice", ClockUtil.ALARM_SERVICE_KEY, false);
        }
        Log.d(TAG, "getAlarmServiceBoolean() = " + ClockUtil.getPrefBoolean("alarmservice", ClockUtil.ALARM_SERVICE_KEY));
        // アラームON/OFFボタンの状態を更新。
        alarmbutton.setChecked(ClockUtil.getPrefBoolean("alarmservice", ClockUtil.ALARM_SERVICE_KEY));
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");

        // Preferenceの値が変更された時に呼び出されるコールバック関数unregist
        // TODO アラーム設定の更新も、ここでやるのがよさそう。
        SharedPreferences prefer_hour = getSharedPreferences("hour", MODE_PRIVATE);
        prefer_hour.unregisterOnSharedPreferenceChangeListener(listener);

        SharedPreferences prefer_minute = getSharedPreferences("minute", MODE_PRIVATE);
        prefer_minute.unregisterOnSharedPreferenceChangeListener(listener);

        SharedPreferences prefer_week = getSharedPreferences("week", MODE_PRIVATE);
        prefer_week.unregisterOnSharedPreferenceChangeListener(listener);
    }

    /**
     * 各Preferenceが変更されたことを検知するリスナー
     * 渡されたkeyの情報でアラーム時間を設定し直す。
     */
    private SharedPreferences.OnSharedPreferenceChangeListener listener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {

                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    Log.d(TAG, "onSharedPreferenceChanged");

                    // アラームの時間か分のキーの場合、サマリーを保存する処理を行う。
                    if(ClockUtil.ALARMTIME_HOUR_KEY.equals(key) || ClockUtil.ALARMTIME_MINUTE_KEY.equals(key)) {
                        // Serviceを一度終了し、更新された時間で再設定する。
                        alarmServiceCansel();
                        ClockUtil.alarmServiceSet();
                        updateTimeView();
                    } else if (ClockUtil.ALARMTIME_WEEK_KEY.equals(key)) {
                        // Serviceを一度終了し、更新された曜日で再設定する。
                        alarmServiceCansel();
                        ClockUtil.alarmServiceSet();
                        updateWeekView();
                    }
                }
            };

    /**
     * アラームが動作する時間の画面表示を更新する。
     */
    private void updateTimeView() {
        Log.d(TAG, "updateTimeView");
        Preference button = null;
        button = mFragment.findPreference("alarmtime_key");

        // 保存されているPreferenceの値を取得
        int spHourInt = ClockUtil.getPrefInt("hour", ClockUtil.ALARMTIME_HOUR_KEY);
        int spMinuteInt = ClockUtil.getPrefInt("minute", ClockUtil.ALARMTIME_MINUTE_KEY);

        // Preferenceを文字列に変換する。
        String valueOfH = String.valueOf(spHourInt);
        String valueOfM = String.valueOf(spMinuteInt);

        // 取得した文字列を整形して画面に反映。
        button.setSummary(ClockUtil.shapingStringTime(valueOfH, valueOfM));
    }

    /**
     * アラームが鳴動する曜日の画面表示を更新する。
     */
    private void updateWeekView() {
        Log.d(TAG, "updateWeekView");

        Preference button = null;
        button = mFragment.findPreference("alarm_start_week_key");

        String[] week = ClockUtil.getSelectedWeeks(ClockUtil.ALARMTIME_WEEK_KEY);

        // 曜日設定が無い場合は、画面の更新は行わない。
        if (week == null) {
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();

        Log.d(TAG, "week = " + week);

        // 配列の値を一通りチェック
        for (int i=0; i<week.length; i++) {
            // 配列の中身をチェック、値によって曜日の文字列に変換する。
            switch(week[i]) {
                case "0":
                    stringBuilder.append("日");
                    break;
                case "1":
                    stringBuilder.append("月");
                    break;
                case "2":
                    stringBuilder.append("火");
                    break;
                case "3":
                    stringBuilder.append("水");
                    break;
                case "4":
                    stringBuilder.append("木");
                    break;
                case "5":
                    stringBuilder.append("金");
                    break;
                case "6":
                    stringBuilder.append("土");
                    break;
            }
        }
        // 変換した文字列を統合して、画面に表示する。
        button.setSummary(stringBuilder.toString());
    }

    /**
     * アラーム時間を設定するダイアログを表示させ、入力された値をアラーム時刻として保存する
     */
    private void alarmTimeSetDialogShow() {
        Log.d(TAG, "alarmTimeSetDialogShow");
        // ダイアログの初期選択状態を現在の時間にするため、Calender.getで現在時間を取得している。
        Calendar calender = Calendar.getInstance();
        int hour = calender.get(Calendar.HOUR_OF_DAY);
        int minute = calender.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                // アラーム時間をPreferenceで保存する
                ClockUtil.setPrefInt("hour", ClockUtil.ALARMTIME_HOUR_KEY, hourOfDay);
                ClockUtil.setPrefInt("minute", ClockUtil.ALARMTIME_MINUTE_KEY, minute);

                // アラームの設定保存に完了したことを永続化する。
            }
        }, hour, minute, true);
        dialog.show();
    }

    /**
     * アラーム実行予定をキャンセルする。
     */
    private void alarmServiceCansel() {

        Context context = getBaseContext();
        int requestcode = 1;

        AlarmManager alarmmanager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        // 設定されたサービスをチェックし、必要な方をIntentに追加する。
        Intent intent;
        if (ClockUtil.getPrefBoolean("spotify_use_boolean", ClockUtil.SPOTIFY_USE_KEY)) {
            intent = new Intent(AlarmPreferenceActivity.this, AlarmSpotifyService.class);
        } else {
            intent = new Intent(AlarmPreferenceActivity.this, AlarmService.class);
        }

        PendingIntent pendingintent = PendingIntent.getService(
                context, requestcode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        pendingintent.cancel();
        alarmmanager.cancel(pendingintent);

        // PendingIntentをキャンセルしたためflagを無効化する
        ClockUtil.setAlarmPendingIntent(false);
    }

    /**
     * アラームの曜日を設定するダイアログダイアログを表示させる
     */
    private void alarmWeekDialogshow() {
        Log.d(TAG, "alarmWeekDialogshow");

        // ダイアログを表示するため、FragmentManagerを取得する。
        FragmentManager manager = getFragmentManager();
        AlarmWeekDialogFragment alarmWeekSetting_dialog = new AlarmWeekDialogFragment();
        alarmWeekSetting_dialog.show(manager, "alarm_Week_Setting_dialog");
    }
}