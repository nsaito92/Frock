package com.example.naotosaito.clocktest;

import android.content.DialogInterface;
import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_call_dialog);

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("アラーム鳴動中")
                .setCancelable(false)
                .setPositiveButton("停止", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // アラームを終了して、Activityを終了する。
                        Intent intent = new Intent(CallAlarmDialogActivity.this, AlarmService.class);
                        stopService(intent);
                        CallAlarmDialogActivity.this.finish();

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
                .create().show();
    }
}
