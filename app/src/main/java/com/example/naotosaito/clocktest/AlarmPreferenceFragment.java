package com.example.naotosaito.clocktest;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by naotosaito on 2018/06/04.
 * 個々で定義したPreferenceのリソースを設定します。
 */
public class AlarmPreferenceFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
    }
}
