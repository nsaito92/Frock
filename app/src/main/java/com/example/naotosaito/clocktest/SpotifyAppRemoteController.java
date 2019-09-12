package com.example.naotosaito.clocktest;

import android.util.Log;
import android.widget.Toast;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track;

/**
 * Created by naotosaito on 2019/08/21.
 * SpotifyAppRemote APIを使用して、認証処理を行うクラス。
 * インスタンス化不可。
 */

public class SpotifyAppRemoteController {

    private static final String TAG = "SpotifyAppRemoteController";
    private static SpotifyAppRemote mSpotifyAppRemote;

    // 間違ってインスタンスを生成された場合、コンストラクタで例外を返す。
    private SpotifyAppRemoteController() {
        throw new AssertionError();
    }

    /**
     * アカウント連携APIを実行する。
     */
    protected static void onStart() {
        Log.d(TAG, "onStart");

        // We will start writing our code here.

        // Set the connection parameters
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(ClockUtil.CLIENT_ID)
                        .setRedirectUri(ClockUtil.REDIRECT_URI)
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

                        // TODO 暫定でこちらでPref設定をする。
                        // TODO Spotifyとローカル音楽ファイルを切り替えられるように対応した後、別場所で処理する。
                        ClockUtil.setPrefBoolean("spotify_use_boolean", ClockUtil.SPOTIFY_USE_KEY, true);
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

    /**
     * アカウント連携解除APIを実行する。
     */
    protected static void onFinish() {
        Log.d(TAG, "onFinish");

        if(mSpotifyAppRemote != null && mSpotifyAppRemote.isConnected()) {
            Log.d(TAG, "disconnect");
            SpotifyAppRemote.disconnect(mSpotifyAppRemote);

            // TODO 暫定でこちらでPref設定をする。
            // TODO Spotifyとローカル音楽ファイルを切り替えられるように対応した後、別場所で処理する。
            ClockUtil.setPrefBoolean("spotify_use_boolean", ClockUtil.SPOTIFY_USE_KEY, false);
        }
    }

    /**
     * アカウント連携状態を返す。
     * @return true : 接続済み false : 未接続
     */
    public static boolean isConnected() {

        boolean result = false;

        try {
            result = mSpotifyAppRemote.isConnected();
            Log.d(TAG, "isConnected = " + result);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Spotify の音楽再生を行う。
     */
    public static void Play() {

        // Play a playlist
        mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:37i9dQZF1DX2sUQwD7tbmL");

        // Subscribe to PlayerState
        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerState -> {
                    final Track track = playerState.track;
                    if (track != null) {
                        Log.d(TAG, track.name + " by " + track.artist.name);
                    }
                });
    }
}
