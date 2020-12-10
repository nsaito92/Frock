package com.example.naotosaito.clocktest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by naotosaito on 2019/05/27.
 * アラーム鳴動中のダイアログを表示するためのActivity。
 */
public class CallAlarmDialogActivity extends AppCompatActivity {
    private static final String TAG = "CallAlarmDialogActivity";

    /** アラーム鳴動通知ダイアログ  */
    private AlertDialog dialog;

    /** アラーム鳴動終了イベント受信用receiver。 */
    private BroadcastReceiver alarmRingingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // アラーム鳴動終了イベントを受け取った場合、Activityを終了する。
            Log.d(TAG, "receiver");

            if (intent.getAction().equals(ClockUtil.BroadCast.CALL_ALARMDIALOG_FINISH)) {
                CallAlarmDialogActivity.this.finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        setContentView(R.layout.activity_call_dialog);

        dialog = dialogCreate();

        // アラーム鳴動イベントの受信用リスナーの準備
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ClockUtil.BroadCast.CALL_ALARMDIALOG_FINISH);
        registerReceiver(alarmRingingReceiver, intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

        dialog.show();

        // アラームサービスが終了している場合は、画面を表示しない。
        if (!ClockUtil.isYourServiceWorking()) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

        dialog.dismiss();

        unregisterReceiver(alarmRingingReceiver);
    }

    /**
     *  アラーム通知ダイアログオブジェクトを生成する。
     * @return
     */
    private AlertDialog dialogCreate() {
        // アラーム鳴動中ダイアログの生成
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);

        return alertBuilder.setTitle("Frock")
                .setIcon(R.mipmap.ic_launcher)
                .setMessage("アラーム鳴動中です")
                .setCancelable(false)
                .setPositiveButton("停止", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // アラームを終了して、Activityを終了する。
                        Intent stopIntent = new Intent(CallAlarmDialogActivity.this, AlarmService.class);
                        stopService(stopIntent);
                        CallAlarmDialogActivity.this.finish();

                        // スヌーズカウントをリセット
                        ClockUtil.setPrefInt("alarmservice", ClockUtil.SharedPreferencesKey.SNOOZE_COUNT, 0);

                        // スヌーズ通知のキャンセル
                        NotificationManagerController controller = new NotificationManagerController(MyApplication.getContext());
                        controller.notificationCansel(NotificationManagerController.NotificationID.SNOOZE);

                        // 次のアラーム予定の再設定する。
                        AlarmServiceSetter setter = new AlarmServiceSetter();
                        setter.updateAlarmService();
                    }
                })
                .setNeutralButton("後で", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick (DialogInterface dialog, int which) {

                        // 何も行わず、Activityを終了させる。
                        CallAlarmDialogActivity.this.finish();
                    }
                })
                .create();
    }
}
