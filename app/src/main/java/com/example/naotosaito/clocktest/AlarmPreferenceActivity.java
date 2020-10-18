package com.example.naotosaito.clocktest;

import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.os.Bundle;
import android.preference.SwitchPreference;
import android.util.Log;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;

/**
 * アラーム設定画面表示用のActivity
 */

public class AlarmPreferenceActivity extends PreferenceActivity {
    private static final String TAG = "AlarmPreferenceActivity";

    /** fragment */
    private AlarmPreferenceFragment mFragment = null;
    /** アラーム ON/OFFボタン */
    private SwitchPreference alarmbutton = null;
    /** アラーム時間入力 */
    private Preference btn_alarmtime_key = null;
    /** アラーム曜日入力 */
    private Preference btn_alarm_start_week_key = null;
    /** アラームに使用する音楽再生ファイル*/
    private Preference btn_alarm_sound = null;
    /** アラーム保存ボタン */
    private Preference btn_alarm_setting_save = null;

    /** アラームの曜日設定ダイアログ **/
    private AlarmWeekDialogFragment alarmWeekSetting_dialog = null;

    /** アラーム設定 */
    private AlarmSettingEntity alarmSettingEntity;

    public AlarmPreferenceActivity () {
        Log.d(TAG, "");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        // TODO Preferenceによるアラーム設定機能は、削除する。

        // 画面呼び出し先をチェック。
        Intent intent = getIntent();
        String position = intent.getStringExtra("position");

        Log.d(TAG, "Receive position = " + position);

        // 呼び出し元が存在していた場合、DBを参照してAlarmSettingEntityを初期化する。
        if (position != null) {
            FrockSettingsHelperController controller = new FrockSettingsHelperController();
            alarmSettingEntity = controller.getAlarmSettingEntity(position);
        } else {
            // 呼び出し元が存在していない場合、新規作成扱い。
            alarmSettingEntity = new AlarmSettingEntity();
        }

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

                if (alarmbutton.isChecked()) {
                    alarmSettingEntity.setmStatus(ClockUtil.TRUE);
                    // trueになった場合は、有効なアラーム設定がある場合は、アラーム設定を行う。
                    ClockUtil.setPrefBoolean("alarmservice", ClockUtil.ALARM_SERVICE_KEY, value);

                } else if (!alarmbutton.isChecked()) {
                    alarmSettingEntity.setmStatus(ClockUtil.FALSE);

                    // falseになった場合は、アラーム鳴動予定がある場合は、無効にする。
                    ClockUtil.setPrefBoolean("alarmservice", ClockUtil.ALARM_SERVICE_KEY, value);
                }
                return alarmbutton.isChecked();
            }
        });

        /* ボタンの押したときの動作
         * PreferenceActivity#findPreferenceはAPIレベル11以降非推奨となっているため、
         * APIレベルが11以上の場合、PreferenceFragment#findPreferenceをコールする。
         */
        // TODO この辺の処理、同じようなこと書いているので、メソッド化したい。
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

        // アラームに使用するローカル音楽ファイルの設定
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            btn_alarm_sound = mFragment.findPreference("alarm_sound_key");
        } else {
            btn_alarm_sound = findPreference(getString(R.string.alarm_setting_save_key));
        }
        btn_alarm_sound.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(){
            @Override
            public boolean onPreferenceClick(Preference pref) {
                Log.d(TAG, "onCreate#onPreferencelick_btn_alarm_sound");

                // サウンドファイルの選択は、端末のファイラーアプリに任せるため、起動Intent発行。
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("audio/*");
                startActivityForResult(intent, ClockUtil.PendingIntentRequestCode.RESULT_PICK_SOUNDFILE);

                return true;
            }
        });

        // アラーム設定保存ボタンの設定
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            btn_alarm_setting_save = mFragment.findPreference("alarm_setting_save_key");
        } else {
            btn_alarm_setting_save = findPreference(getString(R.string.alarm_setting_save_key));
        }

        btn_alarm_setting_save.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(){
            @Override
            public boolean onPreferenceClick(Preference pref) {
                Log.d(TAG, "onCreate#onPreferencelick_btn_alarm_setting_save");

                // 呼び元の画面が存在しているかチェックする。
                Intent intent = getIntent();
                String position = intent.getStringExtra("position");

                FrockSettingsHelperController controller = new FrockSettingsHelperController();

                // 呼び出し元が存在していた → DBの既存データを更新を行う。
                if (position != null) {
                    if (controller.updateData
                            (FrockSettingsOpenHelper.ALARMSETTINGS_TABLE_NAME,
                                    alarmSettingEntity.getmId(),
                                    ClockUtil.convertBoolean(alarmbutton.isChecked()),
                                    alarmSettingEntity.getmHour(),
                                    alarmSettingEntity.getmMinute(),
                                    alarmSettingEntity.getmWeek(),
                                    alarmSettingEntity.getmSoundUri()
                            ))
                    {
                        Toast toast = Toast.makeText(MyApplication.getContext(), "保存成功しました。", Toast.LENGTH_SHORT);
                        toast.show();
                        finish();
                    } else {
                        Toast toast = Toast.makeText(MyApplication.getContext(), "保存失敗しました。", Toast.LENGTH_SHORT);
                        toast.show();
                        finish();
                    }
                } else {
                    // 呼び元が無い → 新規データを挿入処理を行う。

                    if (controller.insertData
                            (FrockSettingsOpenHelper.ALARMSETTINGS_TABLE_NAME,
                                    ClockUtil.convertBoolean(alarmbutton.isChecked()),
                                    alarmSettingEntity.getmHour(),
                                    alarmSettingEntity.getmMinute(),
                                    alarmSettingEntity.getmWeek(),
                                    alarmSettingEntity.getmSoundUri()
                            ))
                    {
                        Toast toast = Toast.makeText(MyApplication.getContext(), "保存成功しました。", Toast.LENGTH_SHORT);
                        toast.show();
                        finish();
                    } else {
                        Toast toast = Toast.makeText(MyApplication.getContext(), "保存失敗しました。", Toast.LENGTH_SHORT);
                        toast.show();
                        finish();
                    }
                }
                // 更新されたDBデータを元に、アラーム設定を実施。
                ClockUtil.setPrefInt("alarmservice", ClockUtil.SharedPreferencesKey.SNOOZE_COUNT, 0);
                AlarmServiceSetter setter = new AlarmServiceSetter();
                setter.updateAlarmService();

                // アラーム通知が残っている可能性があるので、キャンセル。
                NotificationManagerController notificationController = new NotificationManagerController(MyApplication.getContext());
                notificationController.notificationCansel(
                        NotificationManagerController.NotificationID.SNOOZE
                );

                return true;
            }
        });

        // アラーム鳴動時間、曜日設定の表示を、最新に更新。
        updateSettingsView();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

        // 本ActivityのonDestroyが呼ばれた時にDialogが起動していた場合は、インスタンス解放。
        if (alarmWeekSetting_dialog != null) {
            alarmWeekSetting_dialog.dismiss();
        }
    }

    /**
     * 本Activityから他アプリを起動して、ファイルが選択後呼ばれるメソッド。
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d(TAG, "onActivityResult");
        Log.d(TAG, "requestCode = " + requestCode);
        Log.d(TAG, "resultCode = " + resultCode);

        if (intent != null) {
            Log.d(TAG, "intent.getData = " + intent.getData().toString());

            // Intentから、URI取得
            Uri uri = intent.getData();

            if (uri != null) {
                // 音楽ファイルのURI情報表示。
                alarmSettingEntity.setmSoundUri(uri.toString());

                // URIからファイルパスが取得出来るか確認。存在していない場合は、entityにその旨をset。
                ContentResolverController controller = new ContentResolverController();

                if (!controller.isReallyFileAndFileDisable(alarmSettingEntity, false)) {
                    alarmSettingEntity.setmSoundUri(FrockSettingsOpenHelper.INVALID_URI);
                }
            }
        } else {
            alarmSettingEntity.setmSoundUri(FrockSettingsOpenHelper.INVALID_URI);
        }

        // URIをString形式でキャッシュに保存して、画面を更新。
        updateSettingsView();
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
//                        alarmServiceCansel();
//                        ClockUtil.alarmServiceSet();
//                        updateTimeView();
                    } else if (ClockUtil.ALARMTIME_WEEK_KEY.equals(key)) {
                        // Serviceを一度終了し、更新された曜日で再設定する。
//                        alarmServiceCansel();
//                        ClockUtil.alarmServiceSet();
//                        updateWeekView();
                    }
                }
            };

    /**
     * DBから各種アラーム設定のデータを取得して、画面に反映する。
     */
    private void updateSettingsView() {
        Log.d(TAG, "updateSettingsView");

        // Preferenceボタンの読み込み。
        btn_alarmtime_key = mFragment.findPreference("alarmtime_key");
        btn_alarm_start_week_key = mFragment.findPreference("alarm_start_week_key");
        btn_alarm_sound = mFragment.findPreference("alarm_sound_key");

        // AlarmSettingEntityから各種設定の状態を取得。
        alarmbutton.setChecked(ClockUtil.convertInt(alarmSettingEntity.getmStatus()));  // status
        int spHourInt = alarmSettingEntity.getmHour();                                  // hour
        int spMinuteInt = alarmSettingEntity.getmMinute();                              // minute
        String[] week = ClockUtil.convertStringToArray(alarmSettingEntity.getmWeek());  // week
        String soundUri = alarmSettingEntity.getmSoundUri();                            // sounduri

        // DBから時間情報を受け取れた時、文字列を整形してViewに反映。
        String valueOfH = String.valueOf(spHourInt);
        String valueOfM = String.valueOf(spMinuteInt);
        btn_alarmtime_key.setSummary(ClockUtil.shapingStringTime(valueOfH, valueOfM));

        // DBから受け取った曜日の情報を、文字列を整形してViewに反映。
        // 曜日設定が無い場合は、画面の更新は行わない。
        if (week != null) {
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

            // 音楽ファイルURI。URIが無効化されていれば、「設定無し」、それ以外であればファイル名を表示。
            if (soundUri == null || soundUri.equals(FrockSettingsOpenHelper.INVALID_URI)) {
                btn_alarm_sound.setSummary("設定無し");
            } else {

                // URIからファイル名取得。
                ContentResolverController controller = new ContentResolverController();
                Uri uri = Uri.parse(soundUri);
                String setFileName = controller.getFileNameFromUri(uri);
                btn_alarm_sound.setSummary(setFileName);
            }
        }
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
                updateSettingsView();

            }
        }, hour, minute, true);
        dialog.show();
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
        updateSettingsView();
    }
}