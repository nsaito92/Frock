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
        Log.d(TAG, "onCreate called");
    }

    // 他のコンポーネントからstartservice()が実行された際に、呼び出されるメソッド。
    @Override
     public int onStartCommand (Intent intent, int flags, int startId) {
        //非同期処理を行うメソッド
        Log.d(TAG, "onStartCommand called");

        //音楽再生
        audioPlay();

        // Serviceが強制終了された際に、Serviceを再起動しない。
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind called");
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy called");
        audioStop();
    }

    private boolean audioSetup() {
        Log.d(TAG, "onCreate called");
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
        Log.d(TAG, "audioPlay called");
        Log.d(TAG, "audioPlay mediaPlayer = " + mediaPlayer);
        if(mediaPlayer == null){
            // audioファイルの呼び出し
            if(audioSetup()){
                Toast.makeText(MyApplication.getContext(), "read Audio File", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MyApplication.getContext(), "Error : read Audio File", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            Log.d(TAG, "audioPlay mediaPlayer != null");
            // 繰り返し再生する場合
            mediaPlayer.stop();
            mediaPlayer.reset();

            // リソースの解放
            mediaPlayer.release();
        }
        // ファイルを最後まで再生したら、ループする
        mediaPlayer.setLooping(true);
        // 再生する
        mediaPlayer.start(); // MP状態 → Started
        Log.d(TAG, "audioPlay mediaPlayer.start = " + mediaPlayer);
    }

    public void audioStop() {
        Log.d(TAG, "audioStop called");
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
