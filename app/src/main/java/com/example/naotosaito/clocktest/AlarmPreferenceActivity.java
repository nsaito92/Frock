package com.example.naotosaito.clocktest;

import android.preference.PreferenceActivity;
import android.os.Bundle;

/**
 * アラーム設定画面表示用のActivity
 */

public class AlarmPreferenceActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new AlarmPreferenceFragment()).commit();
    }
}
