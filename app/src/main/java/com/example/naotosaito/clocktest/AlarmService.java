package com.example.naotosaito.clocktest;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.IOException;

/**
 * Created by naotosaito on 2017/10/30.
 * アラーム音の設定、開始、終了を行うクラス。
 */

public class AlarmService extends Service {
    private static final String TAG = "AlarmService";

    // アラーム実行音の再生用MediaPlayer
    private MediaPlayer mediaPlayer = null;

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
        //非同期処理を行うメソッド
        Log.d(TAG, "onStartCommand");

        // 自身を監視するサービスを起動
        Intent startServiceIntent = new Intent(this, AlarmServiceObserver.class);
        this.startService(startServiceIntent);

        if (ClockUtil.isYourServiceWorking()) {
            Toast.makeText(MyApplication.getContext(),
                    getString(R.string.started_the_alarm), Toast.LENGTH_SHORT).show();

            // PendingIntentによるServiceが起動したため、flagを無効化する
            ClockUtil.setAlarmPendingIntent(false);

            //音楽再生
            audioPlay(intent.getIntExtra("COLUMN_INDEX_ID", 0));

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

    private boolean audioSetup(int indexID) {
        Log.d(TAG, "audioSetup");

        mediaPlayer = new MediaPlayer(); // MP状態 → Idle
        boolean filecheck = false;

        try{
            // TODO MediaPlayerにDBの保存した音楽ファイルか、assetのファイルのどちらかをsetする。
            mediaPlayer.setDataSource(createFileDescriptor(indexID)); // 状態 → Initialized

//            assetFileDescriptor.getStartOffset();
//            assetFileDescriptor.getLength();

            // 再生準備、再生可能状態になるまでブロック
            mediaPlayer.prepare(); // MP状態 → Prepared
            filecheck = true;
        } catch(IOException e1) {
            e1.printStackTrace();
        }
        return filecheck;
    }

    private void audioPlay(int indexID) {
        Log.d(TAG, "audioPlay");
        if(mediaPlayer == null){
            // audioファイルの呼び出し
            if(audioSetup(indexID)){
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

    /**
     * アラーム用のMediaPlayerにセットするFileDescriptorを生成する。
     * DBに永続化されている音楽ファイルもしくは、assetのファイルを使用する。
     * @return ローカルファイル、もしくはassetファイルを元に生成したFileDescriptorオブジェクト
     * @param indexID
     * @throws IOException
     */
    public FileDescriptor createFileDescriptor(int indexID) throws IOException {
        Log.d(TAG, "createFileDescriptor");

        Log.d(TAG, "indexID = " + indexID);

        FileDescriptor fileDescriptor = null;


        // TODO  DBに永続化されている音楽ファイルの情報を取得して、FileDescriptor オブジェクトを生成する。
        FrockSettingsHelperController controller = new FrockSettingsHelperController();
        AlarmSettingEntity entity = controller.getAlarmSettingEntity(String.valueOf(indexID));

        Uri uri = null;
        uri = Uri.parse(entity.getmSoundUri());

        ContentResolverController resolverController = new ContentResolverController();

        // TODO FileDescriptor取得に完了した場合は、そちらの結果を元に音楽を再生する。
        if (resolverController.isReallyFile(uri)) {
            fileDescriptor = resolverController.getFileDescriptor();
            Log.d(TAG, "Local File");

        } else {
            // DB から取得取得出来なければ、assetのファイルを使用する。
            AssetManager assetManager = MyApplication.getContext().getAssets();

            // Alarm時の音楽ファイル名を指定
            String filePath = "su650.mp3";

            // assetからMP3ファイルを読み込む
            AssetFileDescriptor assetFileDescriptor = assetManager.openFd(filePath);
            fileDescriptor = assetFileDescriptor.getFileDescriptor();
            Log.d(TAG, "asset File");
        }
        Log.d(TAG, "return");
        return fileDescriptor;
    }
}
