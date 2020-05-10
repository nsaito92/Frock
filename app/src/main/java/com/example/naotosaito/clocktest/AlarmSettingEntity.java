package com.example.naotosaito.clocktest;

import android.util.Log;

/**
 * Created by nsaito on 2020/03/16.
 * AlarmPreferenceActivityでの画面表示〜DB永続化までのデータを管理やAdapterでのデータListに使用されるクラス。
 *
 */

class AlarmSettingEntity {
    private static final String TAG = "AlarmSettingEntity";

    /** ID **/
    protected int mId;
    /** アラーム ON/OFF **/
    protected int mStatus;
    /** 時 **/
    protected int mHour;
    /** 分 **/
    protected int mMinute;
    /** 曜日 **/
    protected String mWeeks;

    public AlarmSettingEntity() {
        Log.d(TAG, "");
    }

    public AlarmSettingEntity(int mId, int mStatus, int mHour, int mMinute, String mWeeks) {
        Log.d(TAG, "");

        this.mId = mId;
        this.mStatus = mStatus;
        this.mHour = mHour;
        this.mMinute = mMinute;
        this.mWeeks = mWeeks;
    }

    public void setmId (int id) {
        mId = id;
    }

    public int getmId () {
        return mId;
    }

    public void setmStatus (int status) {
        mStatus = status;
    }

    public int getmStatus () {
        return mStatus;
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
