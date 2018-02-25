package com.example.naotosaito.clocktest;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
                //時間を設定するポップアップを表示させる。一時的にコメントアウト。
                // TimeSet();

                //時間をセットする
                Calendar calender = Calendar.getInstance();

                //Calenderを使って現在の時間を入り秒で取得
                calender.setTimeInMillis(System.currentTimeMillis());

                //アラームを5秒後に設定する
                calender.add(Calendar.SECOND, 5);

                //明示的BroadCast
                Intent intent = new Intent(getApplicationContext(),
                        AlarmBroadcastReceiver.class);

                //Broadcastにメッセージを送るための設定
                PendingIntent pending = PendingIntent.getBroadcast(
                        getApplicationContext(), 0, intent, 0);

                //アラームをセットする
                AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

                if(am !=null) {
                    am.setExact(AlarmManager.RTC_WAKEUP, calender.getTimeInMillis(), pending);

                    Toast.makeText(getApplicationContext(),
                            "Set Alarm ", Toast.LENGTH_SHORT).show();
                }
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
