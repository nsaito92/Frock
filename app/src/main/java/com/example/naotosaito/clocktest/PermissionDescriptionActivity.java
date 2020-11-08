package com.example.naotosaito.clocktest;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * 取得したい権限についての説明を行う画面。
 */
public class PermissionDescriptionActivity extends AppCompatActivity {

    private static final String TAG = PermissionDescriptionActivity.class.getSimpleName();

    /** ダイアログ  */
    private AlertDialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        setContentView(R.layout.activity_permission_description);

        // ダイアログに設定するパラメーターを取得して、ダイアログを生成。
        Intent getIntent = getIntent();
        if (getIntent != null) {
            String permissionName = getIntent.getStringExtra(ClockUtil.IntentKey.PERMISSION_NAME);
            String reason = getIntent.getStringExtra(ClockUtil.IntentKey.REASON);
            String permission = getIntent.getStringExtra(ClockUtil.IntentKey.PERMISSION);
            int requestCode = getIntent.getIntExtra(ClockUtil.IntentKey.REQUEST_CODE, -1);

            // ダイアログ生成。
            this.dialog = dialogCreate(permissionName,
                    reason,
                    permission,
                    requestCode);
        }

        // ダイアログ表示失敗した場合は、Activity終了。
        if (this.dialog == null) {
            ClockUtil.ToastShow("権限説明画面の起動に失敗しました。");
            PermissionDescriptionActivity.this.finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

        this.dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

        if (this.dialog != null) {
            this.dialog.dismiss();
        }
    }

    /**
     *  権限説明ダイアログオブジェクトを生成する。
     * @return
     */
    private AlertDialog dialogCreate(String permissionName, String reason, String permission, int requestCode) {
        Log.d(TAG, "dialogCreate");

        // バリデーションチェック
        if (permissionName == null || reason == null || permission == null) {
            return null;
        }

        // アラーム鳴動中ダイアログの生成
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);

        return alertBuilder.setMessage("権限「" + permissionName + "」が付与されていません。" + "\n" + reason + "を行うために必要です。")
                .setCancelable(false)
                .setPositiveButton("付与する", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 権限要求処理を行う。
                        ActivityCompat.requestPermissions(PermissionDescriptionActivity.this,
                                new String[]{permission},
                                requestCode);
                        PermissionDescriptionActivity.this.finish();
                    }
                })
                .setNeutralButton("付与しない", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick (DialogInterface dialog, int which) {

                        // 何も処理を行わない。
                        PermissionDescriptionActivity.this.finish();
                    }
                })
                .create();
    }
}
