package com.example.naotosaito.clocktest;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.Calendar;
import java.util.Timer;

import android.widget.Button;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.TimePicker;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //各ボタンの定義、リスナーをボタンに登録する
        Button setbutton = (Button) findViewById(R.id.setbutton);
        setbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarmServiceSetting();
            }
        });

        Button saveButton = (Button) findViewById(R.id.savebutton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveButtonClick();
            }
        });

        Button stopButton = (Button) findViewById(R.id.stopbutton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // アラーム音を再生しているServiceを終了する
                Intent intent = new Intent(MainActivity.this, AlarmService.class);
                stopService(intent);
            }
        });

        ViewGroup viewGroup = (ViewGroup)findViewById(R.id.layout);
        //アラームの音量調節を端末の調整ボタンに任せる
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        //タイマーの初期化処理
        MyTimerTask timerTask = new MyTimerTask(viewGroup);
        Timer mTimer = new Timer(true);            //mTimerはコンストラクタ。スレッドの種類を指定する。

        mTimer.schedule(timerTask, 1000, 1000);      //100ミリ秒後に、100ミリ秒感覚でtimerTaskを実行する。
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.optionsMenu_01:
                Intent intent = new android.content.Intent(this, AlarmPreferenceActivity.class);
                startActivity(intent);
                return true;
            case R.id.optionsMenu_02:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // アラームを実行するための設定を行う
    private void alarmServiceSetting() {
        Log.d(TAG, "alarmServiceSetting called");

        // AlarmService起動用のIntent、PendingIntentを作成
        Context context = getBaseContext();
        Intent intent = new Intent(MainActivity.this, AlarmService.class);
        int requestcode = 1;
        PendingIntent pendingintent = PendingIntent.getService(
                context, requestcode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // アラームを実行する時間の設定を準備
        Calendar calender = Calendar.getInstance();
        calender.setTimeInMillis(0);
        calender.set(Calendar.YEAR, 2018);          // 年
        calender.set(Calendar.MONTH, Calendar.MAY); // 月
        calender.set(Calendar.DAY_OF_MONTH, 28);    // 日
        calender.set(Calendar.HOUR_OF_DAY, 13);     // 時
        calender.set(Calendar.MINUTE, 22);          // 分
        calender.set(Calendar.SECOND, 0);           // 秒

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

    //アラーム時間を設定するダイアログを表示させる
    public void TimeSet() {
        final Calendar calender = Calendar.getInstance();
        final int hour = calender.get(Calendar.HOUR_OF_DAY); //現在時刻の取得
        final int minute = calender.get(Calendar.MINUTE);    //現在時刻の取得

        final TimePickerDialog timerPickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Toast.makeText(MainActivity.this, hourOfDay + "時" + minute + "分", Toast.LENGTH_LONG).show();

                //アラーム対象時刻を表示する
                TextView textView = (TextView) findViewById(R.id.setalarmview);
                textView.setText(hourOfDay + "時" + minute + "分");
            }
        }, hour, minute, true);
        timerPickerDialog.show();
    }

    //入力された値の保存を行いつつ、アラーム対象時刻を表示する
    public void saveButtonClick() {
        //入力された値の保存を行う
        EditText edittext = (EditText)findViewById(R.id.editalarm);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().putString("SaveString", edittext.getText().toString()).commit();

        //アラーム対象時刻を表示する
        TextView textView = (TextView) findViewById(R.id.setalarmview);
        SharedPreferences sp2 = PreferenceManager.getDefaultSharedPreferences(this);
        textView.setText(sp2.getString("SaveString", null), BufferType.NORMAL);
    }
}
