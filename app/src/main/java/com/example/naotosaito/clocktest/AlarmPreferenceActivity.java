package com.example.naotosaito.clocktest;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.os.Bundle;
import android.preference.SwitchPreference;
import android.util.Log;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

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
        AlarmWeekDialogFragment alarmWeekSetting_dialog = new AlarmWeekDialogFragment();
        alarmWeekSetting_dialog.show(manager, "alarm_Week_Setting_dialog");
    }

    /**
     * アラームの動作する曜日を選択できるダイアログの内容の設定、項目を選択した際の処理を行う
     */
    public class AlarmWeekDialogFragment extends DialogFragment {
        // 選択したアイテムを格納する配列
        ArrayList mSelectedWeeks = new ArrayList();

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            //
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            // ダイアログのタイトルの設定
            builder.setTitle(R.string.alarm_Week_setting_title)
                    // ダイアログに表示される項目の設定し、項目を選択した際の、リスナーを設定
                    .setMultiChoiceItems(R.array.alarm_Week_setting_menulist, null,
                            new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                    // ユーザーがアイテムを選択した場合、アイテムを追加する
                                    if (isChecked){
                                        mSelectedWeeks.add(which);
                                        Log.d(TAG, "mSelectedWeeks = " + mSelectedWeeks);
                                    } else if(mSelectedWeeks.contains(which)) {
                                        // アイテムがすでに配列内にある場合は、削除する
                                        mSelectedWeeks.remove(Integer.valueOf(which));
                                    }
                                }
                            })
                    .setPositiveButton(R.string.alarm_Week_setting_PositiveButton,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // mSelectedWeeksの結果を保存する
                                    setSelectedWeeks(mSelectedWeeks);
                                }
                            })
                    .setNegativeButton(R.string.alarm_Week_setting_NegativeButton, null);

            return builder.create();
        }
    }

    /**
     * アラームが動作する曜日設定のPreferenceの配列を文字列に変換・保存
     * @param mSelectedWeeks 保存する曜日設定
     */
    public void setSelectedWeeks(ArrayList mSelectedWeeks) {
        Log.d(TAG, "setSelectedWeeks");

        // 曜日が何も選択されていなかった場合は、保存処理は行わない。
        if (mSelectedWeeks == null || mSelectedWeeks.size() == 0) {
            return;
        }

        StringBuffer buffer = new StringBuffer();
        String stringItem = null;

        // 曜日順に整列して保存したいため、配列の整列を行う。
        Collections.sort(mSelectedWeeks);

        // 選択された曜日を確認し、StringBufferに「,」区切りで追加
        for (Object item : mSelectedWeeks) {
            buffer.append(item+",");
        }
        // StringBufferを、一つの文字列に変換する。
        if (buffer != null) {
            String buf = buffer.toString();
            stringItem = buf.substring(0, buf.length() -1);
        }

        // Preferenceの保存
        SharedPreferences prefer_week = getSharedPreferences("week", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefer_week.edit();
        editor.putString(ClockUtil.ALARMTIME_WEEK_KEY, stringItem).commit();
    }
}
