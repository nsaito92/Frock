package com.example.naotosaito.clocktest;

import android.util.Log;

/**
 * Created by nsaito on 2020/03/16.
 * AlarmPreferenceActivityでの画面表示〜DB永続化までのデータを管理するクラス。
 *
 */

class AlarmSettingEntity {
    private static final String TAG = "AlarmSettingEntity";

    /** アラーム ON/OFF **/
    int mStatus;
    /** 時 **/
    int mHour;
    /** 分 **/
    int mMinute;
    /** 曜日 **/
    String mWeeks;

    public AlarmSettingEntity() {
        Log.d(TAG, "");
    }


    public void setmHour (int hour) {
        mHour = hour;
    }

    public int getmHour () {
        return mHour;
    }

    public void setmMinute (int minute) {
        mMinute = minute;
    }

    public int getmMinute () {
        return mMinute;
    }

    public void setmWeek (String week) {
        mWeeks = week;
    }

    public String getmWeek () {
        return mWeeks;
    }
}