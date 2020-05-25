package com.example.naotosaito.clocktest;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    /** アラーム設定リスト表示用ListView */
    private ListView alarmDBlistView;

    /** 　ListView表示用アラーム設定リスト */
    private List<AlarmSettingEntity> alarmSettingEntityList;

    /** アラーム設定反映用アダプタ */
    private AlarmSettingBaseAdapter alarmSettingBaseAdapter;

    // DBの内容表示用textView(デバッグ用)
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.db_view);

        // TODO テスト用アラーム設定データ挿入コード
//        FrockSettingsHelperController controller = new FrockSettingsHelperController();
//        controller.saveData();

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

        // アラーム設定DBのListView表示用のAdapterを設定。
        alarmDBlistView = (ListView)findViewById(R.id.alarmDBlistview);
        alarmSettingEntityList = new ArrayList<>();
        alarmSettingBaseAdapter = new AlarmSettingBaseAdapter(this, alarmSettingEntityList);

        // 上記ListViewをタップした時のリスナーを設定。
        alarmDBlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick");
                Log.d(TAG, "position = " + position);
                Log.d(TAG, "id = " + id);

                // タップした項目の情報を元に、アラーム設定画面に遷移する。
                Intent intent = new Intent(MyApplication.getContext(), AlarmPreferenceActivity.class);
                // クリックイベントで受け取れるIDは0からスタートだが、
                // DBは1からスタートであるため、インクリメントして渡す。
                intent.putExtra("position", String.valueOf(position + 1));
                startActivity(intent);
            }
        });

        // 上記ListViewを長押した時のリスナーを設定。
        alarmDBlistView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemLongClick");

                // アラートダイアログ表示準備
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.setting_delete);
                builder.setMessage(R.string.message_setting_delete);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "onClick : OK");

                        // 各entityで永続化されているDBのIDを取得して、DBから削除する。
                        AlarmSettingEntity entity = alarmSettingEntityList.get(position);
                        FrockSettingsHelperController controller = new FrockSettingsHelperController();
                        Log.d(TAG, "entity.getmId() = " + entity.getmId());
                        controller.selectDelete(String.valueOf(entity.getmId()));

                        // DBに変更があったので、Entityリストを更新。
                        loadAlarmSettingEntityList();
                    }
                });
                builder.setNegativeButton(R.string.cansel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "onClick : Cancel");
                    }
                });
                // ダイアログ表示
                AlertDialog dialog = builder.create();
                dialog.show();
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

        updateAlarmSettingsView();
        loadAlarmSettingEntityList();
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

    /**
     * デバッグ用想定。
     * アラーム設定のDB状態を表示、更新する。
     */
    private void updateAlarmSettingsView() {
        FrockSettingsHelperController controller = new FrockSettingsHelperController();
        textView.setText(controller.getAlarmSettingsToString().toString());
    }

    /**
     * DBからデータを取得し、結果を元にListViewに反映する。
     */
    private void loadAlarmSettingEntityList() {
        alarmSettingEntityList.clear();

        // DBのデータを元に、alarmSettingEntityListに追加する。
        FrockSettingsHelperController controller = new FrockSettingsHelperController();
        alarmSettingEntityList = controller.getAlarmSettingEntityList(alarmSettingEntityList);

        alarmDBlistView.setAdapter(alarmSettingBaseAdapter);
        alarmSettingBaseAdapter.notifyDataSetChanged();
    }
}
