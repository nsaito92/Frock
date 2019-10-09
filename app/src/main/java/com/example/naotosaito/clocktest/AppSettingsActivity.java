package com.example.naotosaito.clocktest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.spotify.android.appremote.api.SpotifyAppRemote;

/**
 * Created by naotosaito on 2019/08/21.
 * アプリの環境設定を行うActivity
 */

public class AppSettingsActivity extends AppCompatActivity {
    private static final String TAG = "AppSettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_settings);

//        Log.d(TAG, "isConnected = " + SpotifyAppRemoteController.isConnected());

        Button btn_Sptf_auth = (Button) findViewById(R.id.btn_sptf_auth);
        btn_Sptf_auth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Spotify接続済みの場合はボタンの表記を変更する。

                SpotifyAppRemoteController.onStart(AppSettingsActivity.class.getName());
            }
        });

        // 接続解除ボタン
        Button btn_Sptf_Disconnect = (Button) findViewById(R.id.btn_sptf_Disconnect);
        btn_Sptf_Disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpotifyAppRemoteController.onFinish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume");
    }
}
