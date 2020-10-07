package com.example.naotosaito.clocktest;

import android.util.Log;

import java.util.Calendar;

/**
 * Created by nsaito on 2020/10/07.
 * AlarmManagerにセットするアラームの設定のEntityクラス。
 */

class AlarmManagerSetDataEntity {
    public final static String TAG = AlarmManagerSetDataEntity.class.getSimpleName();

    /** Calender生成の際に参照したDBのID **/
    private int mId;

    /** AlarmManagerにセットするCalender */
    private Calendar mCalender = null;

    public AlarmManagerSetDataEntity() {
        Log.d(TAG, "");



    }

    public AlarmManagerSetDataEntity(Calendar calendar) {
        Log.d(TAG, "");

        this.mCalender = calendar;

    }



    public void setmId (int id) {
        this.mId = id;
    }

    public int getmId () {
        return this.mId;
    }

    public void setmCalender (Calendar calendar) {
        this.mCalender = calendar;
    }

    public Calendar getmCalender () {
        return this.mCalender;
    }
}
