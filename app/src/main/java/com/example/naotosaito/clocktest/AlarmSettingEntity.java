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
    private int mId;
    /** アラーム ON/OFF **/
    private int mStatus;
    /** 時 **/
    private int mHour;
    /** 分 **/
    private int mMinute;
    /** 曜日 **/
    private String mWeeks = null;
    /** 音楽ファイルのURI **/
    private String mSoundUri = null;

    public AlarmSettingEntity() {
        Log.d(TAG, "");

        // パラメーターが無い場合は、新規作成扱い。デフォルトのデータを入れる。
        this.mStatus = ClockUtil.TRUE;
        this.mHour = ClockUtil.ALARMTIME_DEFAULT;
        this.mMinute = ClockUtil.ALARMTIME_DEFAULT;
    }

    public AlarmSettingEntity(int mId, int mStatus, int mHour, int mMinute, String mWeeks, String mSoundUri) {
        Log.d(TAG, "");

        this.mId = mId;
        this.mStatus = mStatus;
        this.mHour = mHour;
        this.mMinute = mMinute;
        this.mWeeks = mWeeks;
        this.mSoundUri = mSoundUri;
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

    public void setmSoundUri (String soundUri) {
        mSoundUri = soundUri;
    }

    public String getmSoundUri () {
        return mSoundUri;
    }
}
