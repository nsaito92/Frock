package com.example.naotosaito.clocktest;

import android.util.Log;
import android.widget.Toast;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

/**
 * Created by naotosaito on 2019/08/21.
 * Spotify APIを使用して、認証処理を行うクラス。
 */

public class SpotifyAppRemoteController {

    private static final String TAG = "SpotifyAppRemoteController";

    private static final String CLIENT_ID = "779e27eb586742d7bb1eea46b275d464";
    private static final String REDIRECT_URI = "https://www.spotify.com/jp/";
    private SpotifyAppRemote mSpotifyAppRemote;

    protected void onStart() {
        // We will start writing our code here.

        // Set the connection parameters
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(MyApplication.getContext(), connectionParams,
                new Connector.ConnectionListener() {

                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d(TAG, "onConnected");
                        Toast.makeText(MyApplication.getContext(),
                                MyApplication.getContext().getString(R.string.sptf_auth_comp), Toast.LENGTH_SHORT);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e(TAG, throwable.getMessage(), throwable);
                        Log.d(TAG, "onFailure");

                        Toast.makeText(MyApplication.getContext(),
                                MyApplication.getContext().getString(R.string.sptf_auth_fail), Toast.LENGTH_SHORT);
                    }
                });
    }

    protected void onFinish() {
        Log.d(TAG, "onFinish");

        if(mSpotifyAppRemote != null && mSpotifyAppRemote.isConnected()) {
            SpotifyAppRemote.disconnect(mSpotifyAppRemote);
        }
    }

    /**
     * SpotifyAppRemote APIを接続した状態となっているか。
     * @return true : 接続済み false : 未接続
     */
    public boolean isConnected() {

        try {
            mSpotifyAppRemote.isConnected();
        } catch (RuntimeException e) {
            e.printStackTrace();

            return false;
        }
        return true;
    }
}
