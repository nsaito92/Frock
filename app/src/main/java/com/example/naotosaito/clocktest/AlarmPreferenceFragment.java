package com.example.naotosaito.clocktest;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;

/**
 * Created by naotosaito on 2018/06/04.
 * 個々で定義したPreferenceのリソースを設定します。
 */
public class AlarmPreferenceFragment extends PreferenceFragment {
    private static final String TAG = "AlarmPreferenceFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
    }
}
