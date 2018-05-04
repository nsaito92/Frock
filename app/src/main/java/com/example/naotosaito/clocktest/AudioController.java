package com.example.naotosaito.clocktest;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by naotosaito on 2017/10/30.
 * アラーム音の設定、開始、終了を行うクラス。
 */
public class AudioController {

    // アラーム実行音の再生用MediaPlayer
    private MediaPlayer mediaPlayer;

    public boolean audioSetup() {
        mediaPlayer = new MediaPlayer(); // MP状態 → Idle

        boolean filecheck = false;
        AssetManager assetManager = MyApplication.getContext().getAssets();

        // Alarm時の音楽ファイル名を指定
        String filePath = "su650.mp3";

        try{
            // assetからMP3ファイルを読み込む
            AssetFileDescriptor assetFileDescriptor = assetManager.openFd(filePath);

            Log.d("nsaitotest", "assetFileDescriptor = " + assetFileDescriptor);

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

    public void audioPlay(){
        Log.d("nsaitotest", "mediaPlayer.audioPlay = " + mediaPlayer);
        if(mediaPlayer == null){
            // audioファイルの呼び出し
            if(audioSetup()){
                Toast.makeText(MyApplication.getContext(), "read Audio File", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MyApplication.getContext(), "Error : read Audio File", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            // 繰り返し再生する場合
            mediaPlayer.stop();
            mediaPlayer.reset();

            // リソースの解放
            mediaPlayer.release();
        }
        // 再生する
        mediaPlayer.start(); // MP状態 → Started
        Log.d("nsaitotest", "mediaPlayer.start = " + mediaPlayer);
    }

    public void audioStop(){
        Log.d("nsaitotest", "mediaPlayer.stop = " + mediaPlayer);
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
