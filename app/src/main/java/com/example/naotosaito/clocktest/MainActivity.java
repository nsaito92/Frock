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

    //アラーム音を操作するAudioControllerのインスタンスを作成
    AudioController audioController = new AudioController();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //各ボタンの定義、リスナーをボタンに登録する
        Button setbutton = (Button) findViewById(R.id.setbutton);
        setbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarmServiceStart();
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
                //音楽停止
                audioController.audioStop();
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

    // アラームを実行するための設定を行う
    private void alarmServiceStart() {
        Log.d("nsaitotest", "alarmServiceStart");

        // AlarmService起動用のIntent、PendingIntentを作成
        Context context = getBaseContext();
        Intent intent = new Intent(context, AlarmService.class);
        int requestcode = 1;
        PendingIntent pendingintent = PendingIntent.getService(
                context, requestcode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // アラームを実行する時間の設定を準備
        Calendar calender = Calendar.getInstance();
        calender.setTimeInMillis(0);
        calender.set(Calendar.YEAR, 2018);
        calender.set(Calendar.MONTH, Calendar.APRIL);
        calender.set(Calendar.DAY_OF_MONTH, 10);
        calender.set(Calendar.HOUR_OF_DAY, 2);
        calender.set(Calendar.MINUTE, 22);
        calender.set(Calendar.SECOND, 0);

        // AlarmManagerのset()でAlarmManagerでセットした時間に、Serviceを起動
        AlarmManager alarmmanager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmmanager.set(AlarmManager.RTC, calender.getTimeInMillis(), pendingintent);
        Log.d("nsaitotest", "AlarmSettingTime is "
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
