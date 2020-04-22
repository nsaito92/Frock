package com.example.naotosaito.clocktest;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.List;

/**
 * Created by nsaito on 2020/02/03.
 * FrockSettingsOpenHelperを使用するコントローラークラス。
 */

public class FrockSettingsHelperController {

    public static final String TAG = FrockSettingsHelperController.class.getSimpleName();
    FrockSettingsOpenHelper settingshelper;
    AlarmSettingEntity alarmSettingEntity;

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
        entity.setmStatus(cursor.getInt(FrockSettingsOpenHelper.COLUMN_INDEX_STATUS));
        entity.setmHour(cursor.getInt(FrockSettingsOpenHelper.COLUMN_INDEX_HOUR));
        entity.setmMinute(cursor.getInt(FrockSettingsOpenHelper.COLUMN_INDEX_MINUTE));
        entity.setmWeek(cursor.getString(FrockSettingsOpenHelper.COLUMN_INDEX_WEEK));

        cursor.close();

        return entity;
    }

    /**
     * DBからデータを取得して、アラーム設定リストオブジェクトを返す。
     * @return
     */
    public List<AlarmSettingEntity> getAlarmSettingEntityList(List<AlarmSettingEntity> alarmSettingEntityList) {

        Cursor cursor = getCursor();

        if (cursor.moveToFirst()) {
            for (boolean next = cursor.moveToFirst(); next;next = cursor.moveToNext()) {
                alarmSettingEntity = new AlarmSettingEntity(
                        cursor.getInt(FrockSettingsOpenHelper.COLUMN_INDEX_STATUS),
                        cursor.getInt(FrockSettingsOpenHelper.COLUMN_INDEX_HOUR),
                        cursor.getInt(FrockSettingsOpenHelper.COLUMN_INDEX_MINUTE),
                        cursor.getString(FrockSettingsOpenHelper.COLUMN_INDEX_WEEK)
                );
                Log.d(TAG, "COLUMN_INDEX_STATUS = " + cursor.getInt(FrockSettingsOpenHelper.COLUMN_INDEX_STATUS));
                Log.d(TAG, "COLUMN_INDEX_HOUR = " + cursor.getInt(FrockSettingsOpenHelper.COLUMN_INDEX_HOUR));
                Log.d(TAG, "COLUMN_INDEX_MINUTE = " + cursor.getInt(FrockSettingsOpenHelper.COLUMN_INDEX_MINUTE));
                Log.d(TAG, "COLUMN_INDEX_WEEK = " + cursor.getString(FrockSettingsOpenHelper.COLUMN_INDEX_WEEK));

                alarmSettingEntityList.add(alarmSettingEntity);
            }

        }
        cursor.close();

        return alarmSettingEntityList;
    }

    /**
     * DBのデータを取得して、StringBuilderで整形して返す。
     * @return
     */
    public StringBuilder getAlarmSettingsToString() {
        StringBuilder stringBuilder = new StringBuilder();
        Cursor cursor = getCursor();

        for (int i = 0; i < cursor.getCount(); i++) {
            stringBuilder.append(cursor.getInt(0));
            stringBuilder.append(", ");
            stringBuilder.append(cursor.getInt(1));
            stringBuilder.append(", ");
            stringBuilder.append(cursor.getInt(2));
            stringBuilder.append(" : ");
            stringBuilder.append(cursor.getInt(3));
            stringBuilder.append(", ");
            stringBuilder.append(cursor.getString(4));
            stringBuilder.append("\n");
            cursor.moveToNext();
        }
        cursor.close();
        return stringBuilder;
    }
}
