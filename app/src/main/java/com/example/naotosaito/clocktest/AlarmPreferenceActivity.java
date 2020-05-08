package com.example.naotosaito.clocktest;

import android.app.AlarmManager;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.os.Bundle;
import android.preference.SwitchPreference;
import android.util.Log;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

/**
 * アラーム設定画面表示用のActivity
 */

public class AlarmPreferenceActivity extends PreferenceActivity {
    private static final String TAG = "AlarmPreferenceActivity";

    /** fragment */
    private AlarmPreferenceFragment mFragment;
    /** アラーム ON/OFFボタン */
    private SwitchPreference alarmbutton;
    /** アラーム時間入力 */
    private Preference btn_alarmtime_key;
    /** アラーム曜日入力 */
    private Preference btn_alarm_start_week_key;
    /** アラーム保存ボタン */
    private Preference btn_alarm_setting_save;

    /** アラームの曜日設定ダイアログ **/
    private AlarmWeekDialogFragment alarmWeekSetting_dialog;

    /** アラーム設定 */
    private AlarmSettingEntity alarmSettingEntity;

    public AlarmPreferenceActivity () {
        Log.d(TAG, "");

        // 画面表示時に、AlarmSettingEntityの値をDBの現在値で初期化する。
        FrockSettingsHelperController controller = new FrockSettingsHelperController();
        alarmSettingEntity = controller.getAlarmSettingEntity();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        // TODO Preferenceによるアラーム設定機能は、削除する。

        boolean value = false;

        mFragment = new AlarmPreferenceFragment();
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, mFragment).commit();
        // メインスレッドで保留中となるため、AlarmPreferenceFragment#onCreateがコールされない。
        // findpreferenceを実行したいため、executePendingTransactionsを呼び出す。
        getFragmentManager().executePendingTransactions();

        // アラーム設定の有効・無効を設定するトグルボタンを押した時の動作。
        // (SwitchPreference)でキャストし、findPreferenceを実行。
        alarmbutton = (SwitchPreference)mFragment.findPreference(getString(R.string.alarmboolean_key));
        alarmbutton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(){
            public boolean onPreferenceClick(Preference pref) {
                Log.d(TAG, "onCreate#alarmbutton onPreferencelick");

                // トグルボタンの状態をチェックして結果を永続化する。
//                value = alarmbutton.isChecked();


                //
                ContentValues cv = new ContentValues();

                FrockSettingsHelperController frockSettingsHelperController = new FrockSettingsHelperController();
                SQLiteDatabase db = frockSettingsHelperController.getWritableDatabase();
//                db.update(FrockSettingsOpenHelper.ALARMSETTINGS_TABLE_NAME);

                if (value) {
                    // trueになった場合は、有効なアラーム設定がある場合は、アラーム設定を行う。
                    ClockUtil.setPrefBoolean("alarmservice", ClockUtil.ALARM_SERVICE_KEY, value);

                    // TODO アラームON/OFFをDBに更新する。

                    ClockUtil.alarmServiceSet();
                } else if (!value) {
                    // falseになった場合は、アラーム鳴動予定がある場合は、無効にする。
                    ClockUtil.setPrefBoolean("alarmservice", ClockUtil.ALARM_SERVICE_KEY, value);

                    // TODO アラームON/OFFをDBに更新する。

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
        btn_alarmtime_key = null;

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
        btn_alarm_start_week_key = null;
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

        // アラーム設定保存ボタンの設定
        btn_alarm_setting_save = null;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            btn_alarm_setting_save = mFragment.findPreference("alarm_setting_save_key");
        } else {
            btn_alarm_setting_save = findPreference(getString(R.string.alarm_setting_save_key));
        }

        btn_alarm_setting_save.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(){
            @Override
            public boolean onPreferenceClick(Preference pref) {
                Log.d(TAG, "onCreate#onPreferencelick_btn_alarm_setting_save");
                Log.d(TAG, "alarmSettingEntity.getmHour() = " + alarmSettingEntity.getmHour());
                Log.d(TAG, "alarmSettingEntity.getmMinute() = " + alarmSettingEntity.getmMinute());
                Log.d(TAG, "alarmSettingEntity.getmWeek() = " + alarmSettingEntity.getmWeek());

                FrockSettingsHelperController frockSettingsHelperController = new FrockSettingsHelperController();

                if (frockSettingsHelperController.updateData
                        (FrockSettingsOpenHelper.ALARMSETTINGS_TABLE_NAME,
                                ClockUtil.convertBoolean(alarmbutton.isChecked()),
                                alarmSettingEntity.getmHour(),
                                alarmSettingEntity.getmMinute(),
                                alarmSettingEntity.getmWeek()
                        ))
                {
                    Toast toast = Toast.makeText(MyApplication.getContext(), "保存成功しました。", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(MyApplication.getContext(), "保存失敗しました。", Toast.LENGTH_SHORT);
                    toast.show();
                }
                return true;
            }
        });

        // アラーム鳴動時間、曜日設定の表示を、最新に更新。
        updateSettingsView();
//        updateTimeView();
//        updateWeekView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        // Preferenceの値が変更された時に呼び出されるコールバック関数をregist
        // TODO DB移行が完了したら、修正。
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

        updateSettingsView();
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
//                        updateTimeView();
                    } else if (ClockUtil.ALARMTIME_WEEK_KEY.equals(key)) {
                        // Serviceを一度終了し、更新された曜日で再設定する。
                        alarmServiceCansel();
                        ClockUtil.alarmServiceSet();
//                        updateWeekView();
                    }
                }
            };

    /**
     * DBから各種アラーム設定のデータを取得して、画面に反映する。
     */
    private void updateSettingsView() {
        Log.d(TAG, "updateSettingsView");

        btn_alarmtime_key = mFragment.findPreference("alarmtime_key");
        btn_alarm_start_week_key = mFragment.findPreference("alarm_start_week_key");

        alarmbutton.setChecked(ClockUtil.convertInt(alarmSettingEntity.getmStatus()));  // status
        int spHourInt = alarmSettingEntity.getmHour();                                  // hour
        int spMinuteInt = alarmSettingEntity.getmMinute();                              // minute
        String[] week = ClockUtil.convertStringToArray(alarmSettingEntity.getmWeek());  // week

        // DBから受け取った時間の情報を、文字列を整形してViewに反映。
        String valueOfH = String.valueOf(spHourInt);
        String valueOfM = String.valueOf(spMinuteInt);
        btn_alarmtime_key.setSummary(ClockUtil.shapingStringTime(valueOfH, valueOfM));

        // DBから受け取った曜日の情報を、文字列を整形してViewに反映。
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
        btn_alarm_start_week_key.setSummary(stringBuilder.toString());
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

                // アラーム時間をセット
                alarmSettingEntity.setmHour(hourOfDay);
                alarmSettingEntity.setmMinute(minute);

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
        Intent intent = new Intent(AlarmPreferenceActivity.this, AlarmService.class);
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
        alarmWeekSetting_dialog = new AlarmWeekDialogFragment();
        alarmWeekSetting_dialog.show(manager, "alarm_Week_Setting_dialog");
    }

    /**
     * 他画面から渡された文字列を受け取る。
     * @param string
     */
    public void onReceiveString(String string) {
        alarmSettingEntity.setmWeek(string);
    }
}