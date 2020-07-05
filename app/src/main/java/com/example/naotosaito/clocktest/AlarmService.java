package com.example.naotosaito.clocktest;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by naotosaito on 2017/10/30.
 * アラーム音の設定、開始、終了を行うクラス。
 */

public class AlarmService extends Service {
    private static final String TAG = "AlarmService";

    // アラーム実行音の再生用MediaPlayer
    private MediaPlayer mediaPlayer;

    //Serviceクラスは抽象メソッドのため、コンストラクタとonBind()メソッドを必ず実装する必要がある
    public AlarmService() {
    }

    // Serviceの初回起動時に、一回限りのセットアップを行う。
    @Override
    public void onCreate () {
        Log.d(TAG, "onCreate");
    }

    // 他のコンポーネントからstartservice()が実行された際に、呼び出されるメソッド。
    @Override
     public int onStartCommand (Intent intent, int flags, int startId) {
        if (ClockUtil.isYourServiceWorking()) {

            //非同期処理を行うメソッド
            Log.d(TAG, "onStartCommand");

            Toast.makeText(MyApplication.getContext(),
                    getString(R.string.started_the_alarm), Toast.LENGTH_SHORT).show();

            // PendingIntentによるServiceが起動したため、flagを無効化する
            ClockUtil.setAlarmPendingIntent(false);

            //音楽再生
            audioPlay();

            // 通知を表示する
            NotificationManagerController controller = new NotificationManagerController(MyApplication.getContext());
            controller.createNotificationAlarmRinging();

            // アラーム鳴動通知ダイアログを表示
            Intent intent_alarmdialogactivity = new Intent(this, CallAlarmDialogActivity.class);
            intent_alarmdialogactivity.putExtra("requestcode", ClockUtil.PendingIntentRequestCode.ALARMSERVICE);
            intent_alarmdialogactivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent_alarmdialogactivity);
        } else {
            onDestroy();
        }

        // Serviceが強制終了された際に、Serviceを再起動しない。
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        audioStop();

        // 通知削除
        NotificationManagerController controller = new NotificationManagerController(MyApplication.getContext());
        controller.notificationCansel(NotificationManagerController.NotificationID.ALARMRINGING);

        Toast.makeText(MyApplication.getContext(),
                getString(R.string.stopped_the_alarm), Toast.LENGTH_SHORT).show();
    }

    private boolean audioSetup() {
        Log.d(TAG, "audioSetup");
        mediaPlayer = new MediaPlayer(); // MP状態 → Idle

        boolean filecheck = false;
        AssetManager assetManager = MyApplication.getContext().getAssets();

        // Alarm時の音楽ファイル名を指定
        String filePath = "su650.mp3";

        try{
            // assetからMP3ファイルを読み込む
            AssetFileDescriptor assetFileDescriptor = assetManager.openFd(filePath);

            Log.d(TAG, "assetFileDescriptor = " + assetFileDescriptor);

            // MediaPlayerに読み込んだ音楽ファイルを指定
            mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor()); // 状態 → Initialized
            assetFileDescriptor.getStartOffset();
            assetFileDescriptor.getLength();

            // 再生準備、再生可能状態になるまでブロック
            mediaPlayer.prepare(); // MP状態 → Prepared
            filecheck = true;
        } catch(IOException e1) {
            e1.printStackTrace();
        }
        return filecheck;
    }

    private void audioPlay() {
        Log.d(TAG, "audioPlay");
        if(mediaPlayer == null){
            // audioファイルの呼び出し
            if(audioSetup()){
                Log.d(TAG, "read Audio File");
            } else {
                Log.d(TAG, "Error : read Audio File");
                return;
            }
        } else {
            // 繰り返し再生する場合
            mediaPlayer.stop();
            mediaPlayer.reset();

            // リソースの解放
            mediaPlayer.release();
        }
        // ファイルを最後まで再生したら、ループする
        mediaPlayer.setLooping(true);
        // 再生する
        mediaPlayer.start();
        Log.d(TAG, "audioPlay mediaPlayer.start");
    }

    private void audioStop() {
        Log.d(TAG, "audioStop");
        if (mediaPlayer != null) {
            // 再生終了
            mediaPlayer.stop();
            // リセット
            mediaPlayer.reset();
            // リソースの解放
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
