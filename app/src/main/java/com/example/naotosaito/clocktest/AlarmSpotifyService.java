package com.example.naotosaito.clocktest;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by naotosaito on 2019/09/04.
 * アラームを実行時、Spotifyの音楽を使用したサービスを管理するクラス。
 */

public class AlarmSpotifyService extends AlarmService {
    private static final String TAG = "AlarmSpotifyService";

    // 他のコンポーネントからstartservice()が実行された際に、呼び出されるメソッド。
    @Override
    public int onStartCommand (Intent intent, int flags, int startId) {
        //非同期処理を行うメソッド
        Log.d(TAG, "onStartCommand");

        // Spotify連携の状態をチェックし、接続できる状態かつ使用する設定となっていたため、再生する。
        // TODO 　Spotify連携できなかった場合の、エラーハンドリング
        SpotifyAppRemoteController.onStart(AlarmSpotifyService.class.getName());

        Toast.makeText(MyApplication.getContext(),
                getString(R.string.started_the_alarm), Toast.LENGTH_SHORT).show();

        // PendingIntentによるServiceが起動したため、flagを無効化する
        ClockUtil.setAlarmPendingIntent(false);

        // アラーム鳴動通知ダイアログを表示
        Intent intent_alarmdialogactivity = new Intent(this, CallAlarmDialogActivity.class);
        //
        intent_alarmdialogactivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent_alarmdialogactivity);

        // Serviceが強制終了された際に、Serviceを再起動しない。
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        Toast.makeText(MyApplication.getContext(),
                getString(R.string.stopped_the_alarm), Toast.LENGTH_SHORT).show();

        SpotifyAppRemoteController.pause();

        // TODO Destroyされた直後に、setAlarmServiceBoolean(false)するのが一番正しい気がするので後ほど修正する。
    }
}
