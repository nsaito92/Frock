package com.example.naotosaito.clocktest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by naotosaito on 2018/02/25.
 * Ararm実行のIntentを受けて実行されるクラス
 */
public class AlarmBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //Toastで受け取りを確認
        Toast.makeText(context, "Received ", Toast.LENGTH_LONG).show();

        //アラーム音を操作するAudioControllerのインスタンスを作成
        // AudioController audioController = new AudioController();
        // audioController.audioPlay();
    }
}
