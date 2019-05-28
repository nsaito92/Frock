package com.example.naotosaito.clocktest;

import android.content.DialogInterface;
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

        Log.d(TAG, "onCreate");

        setContentView(R.layout.activity_call_dialog);
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("title");
        alertBuilder.setMessage("message");
        alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CallAlarmDialogActivity.this.finish();//選択をしたら自信のActivityを終了させる
            }
        });
        alertBuilder.create().show();
    }
}
