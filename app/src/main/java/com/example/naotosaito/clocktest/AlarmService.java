package com.example.naotosaito.clocktest;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;


public class AlarmService extends IntentService {

    //アラーム音を操作するAudioControllerのインスタンスを作成
    AudioController audioController = new AudioController();

    public AlarmService(String name){
        super(name);
    }

    //Serviceクラスは抽象メソッドのため、コンストラクタとonBind()メソッドを必ず実装する必要がある
    public AlarmService() {
        super("AlarmService");
    }

    protected void onHandleIntent(Intent intent) {
        //非同期処理を行うメソッド
        Log.d("nsaitotest_AlarmService", "onHendletIntent start");

        //音楽再生
        audioController.audioPlay();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
