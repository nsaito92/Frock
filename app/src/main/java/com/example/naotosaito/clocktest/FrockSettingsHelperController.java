package com.example.naotosaito.clocktest;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by nsaito on 2020/02/03.
 * FrockSettingsOpenHelperを使用するコントローラークラス。
 */

public class FrockSettingsHelperController {

    FrockSettingsOpenHelper settingshelper;

    // コンストラクタでFrockSettingsHelperをnewする。
    public FrockSettingsHelperController() {
        settingshelper = new FrockSettingsOpenHelper(MyApplication.getContext().getApplicationContext());
    }

    /**
     * FrockSettingsOpenHelperの読み書き用データベースの作成および/オープンを行い、返却する。
     * @return
     */
    public SQLiteDatabase getWritableDatabase() {
        return settingshelper.getWritableDatabase();
    }

    /**
     * クエリを実行して、Cursorを取得する。
     * @return DBのcursor
     */
    public Cursor getCursor() {
        SQLiteDatabase db = getWritableDatabase();

        // TODO 仮実装で、DBの一行目のデータを取得するが、最終的にはユーザー操作により変更できる様にする。
        Cursor cursor = db.query(FrockSettingsOpenHelper.ALARMSETTINGS_TABLE_NAME,
                null, null, null, null, null, null);
        cursor.moveToFirst();

        return cursor;
    }

    /**
     * ユーザーが入力したデータをDBに永続化する。
     * @param table_name
     * @param status
     * @param hour
     * @param minute
     * @param week
     */
    public boolean updateData(String table_name, int status, int hour, int minute, String week) {

        boolean result = true;

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = getCursor();

        ContentValues contentValues = new ContentValues();
        contentValues.put(FrockSettingsOpenHelper.ALARMSETTINGS_COLUMN_NAME_STATUS, status);
        contentValues.put(FrockSettingsOpenHelper.ALARMSETTINGS_COLUMN_NAME_HOUR, hour);
        contentValues.put(FrockSettingsOpenHelper.ALARMSETTINGS_COLUMN_NAME_MINUTE, minute);
        contentValues.put(FrockSettingsOpenHelper.ALARMSETTINGS_COLUMN_NAME_WEEK, week);

        try {
            db.update(FrockSettingsOpenHelper.ALARMSETTINGS_TABLE_NAME, contentValues, "_id = " + 1, null);
        } catch (SQLException e) {
            result = false;
        }
        cursor.close();

        return result;
    }

    /**
     * DBからデータを取得して、AlarmSettingEntityオブジェクトを返す。
     * @return
     */
    public AlarmSettingEntity getAlarmSettingEntity() {

        Cursor cursor = getCursor();
        AlarmSettingEntity entity = new AlarmSettingEntity();
        entity.setmStatus(cursor.getInt(1));
        entity.setmHour(cursor.getInt(2));
        entity.setmMinute(cursor.getInt(3));
        entity.setmWeek(cursor.getString(4));

        cursor.close();

        return entity;
    }
}
