package com.example.naotosaito.clocktest;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.Timer;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    FrockSettingsOpenHelper settingshelper;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.db_view);

        // DB作成
        settingshelper = new FrockSettingsOpenHelper(getApplicationContext());

        // TODO テストコード
        settingshelper.saveData(settingshelper.getReadableDatabase(),
                FrockSettingsOpenHelper.ALARMSETTINGS_TABLE_NAME,
                0, 12, 00, "0,1");

        // TODO テストコード
        Button testAlarmSetButton = (Button) findViewById(R.id.db_alarmbutton);
        testAlarmSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DBを読み込み、アラーム設定を行う。
                ClockUtil.alarmServiceSet();
            }
        });

        //各ボタンの定義、リスナーをボタンに登録する
        Button stopButton = (Button) findViewById(R.id.stopbutton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // アラーム音を再生しているServiceを終了する
                Intent intent = new Intent(MainActivity.this, AlarmService.class);

                boolean stopServiceresult = stopService(intent);
                Log.d(TAG, "stopServiceresult = " + stopServiceresult);
                if (!stopServiceresult) {
                    Toast.makeText(MyApplication.getContext(),
                            R.string.No_alarm_to_stop, Toast.LENGTH_SHORT).show();
                }

                // 次のアラームを設定。
                ClockUtil.alarmServiceSet();
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
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

        updateAlarmSettingsView();
    }

    /**
     * デバッグ用想定。
     * アラーム設定のDB状態を表示、更新する。
     */
    private void updateAlarmSettingsView() {
        SQLiteDatabase db = settingshelper.getReadableDatabase();
        Cursor cursor = db.query(FrockSettingsOpenHelper.ALARMSETTINGS_TABLE_NAME,
                null, null, null, null, null, null);
        cursor.moveToFirst();

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < cursor.getCount(); i++) {
            stringBuilder.append(cursor.getInt(0));
            stringBuilder.append(", ");
            stringBuilder.append(cursor.getInt(1));
            stringBuilder.append(", ");
            stringBuilder.append(cursor.getInt(2));
            stringBuilder.append(" : ");
            stringBuilder.append(cursor.getInt(3));
            stringBuilder.append(", ");
            stringBuilder.append(cursor.getString(4));
            stringBuilder.append("\n");
            cursor.moveToNext();
        }
        cursor.close();

        textView.setText(stringBuilder.toString());
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
