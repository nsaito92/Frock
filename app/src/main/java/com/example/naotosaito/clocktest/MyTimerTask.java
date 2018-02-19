package com.example.naotosaito.clocktest;

import android.os.Handler;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

/**
 * Created by naotosaito on 2017/11/15.
 * 定期的に画面に表示されている時間を更新するクラス
 */

public class MyTimerTask extends TimerTask {
    Handler mHandler = new Handler();   //UI Threadへのpost用ハンドラ

    static TextView textview;

    //コンストラクタで画面を更新するためのviewGrouoの値を受け取る
    public MyTimerTask(ViewGroup viewGroup) {
        MyTimerTask.textview = (TextView) viewGroup.findViewById(R.id.nowview);
    }

    @Override
    public void run() {
        //mHandlerを通じてUIThreadへ処理をキューイング
        mHandler.post(new Runnable() {
            public void run() {
                NowDataUpdate();    //現在時間の更新を行う
            }
        });
    }

    //画面に表示されている現在時間を更新する
    public void NowDataUpdate() {
        //currentTimeMillisメソッドで現在日時の取得を行う
        Date now = new Date(System.currentTimeMillis());

        //SimpleDateFormatクラスで日時のフォーマットオブジェクト作成
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH時mm分ss秒");

        //formatメソッドでnowを日付、文字列にフォーマット
        String nowText = formatter.format(now);

        //比較用データを持ってくる
        //TextView textView = (TextView) mainActivity.findViewById(R.id.setalarmview);
        //String AlarmText = textView.getText().toString();

        //現在時間とアラーム設定時間の比較を行うメソッド
        //diffchack(nowText, AlarmText);

        //UIに反映
        textview.setText(nowText);
    }

    //アラーム設定日時と現在の日時の比較をする
    public void diffchack(String nowText, String AlarmText) {

        //アラーム音を操作するAudioControllerのインスタンスを作成
        AudioController audioController = new AudioController();

        int diff = nowText.compareTo(AlarmText);
        if (diff == 0) {
            System.out.println("アラーム時刻になりました。");
            //Toast.makeText(this, "アラーム時刻になりました。", Toast.LENGTH_SHORT).show();
            audioController.audioPlay();
        } else if (diff > 0) {
            System.out.println("アラーム実行日より過去の日付を指定しています。");
        } else {
            System.out.println("アラーム実行日はまだ先です。");
        }
    }
}
