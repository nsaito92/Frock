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
        Log.d(TAG, "onStartCommand called");
        Toast.makeText(MyApplication.getContext(),
                getString(R.string.started_the_alarm), Toast.LENGTH_SHORT).show();

        // PendingIntentによるServiceが起動したため、flagを無効化する
        ClockUtil.setAlarmPendingIntent(false);

        // Spotify連携の状態をチェック。接続できる状態かつ使用する設定であれば、再生する。
        if (SpotifyAppRemoteController.isConnected() &&
                ClockUtil.getPrefBoolean("spotify_use_boolean", ClockUtil.SPOTIFY_USE_KEY)) {
            //音楽再生
            audioPlay();
        }

        // アラーム鳴動通知ダイアログを表示
        Intent intent_alarmdialogactivity = new Intent(this, CallAlarmDialogActivity.class);
        //
        intent_alarmdialogactivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent_alarmdialogactivity);

        // Serviceが強制終了された際に、Serviceを再起動しない。
        return START_NOT_STICKY;
    }

    /**
     * Spotifyの音楽を再生してアラームを実行する。
     */
    @Override
    public void audioPlay() {
        SpotifyAppRemoteController.Play();
    }
}