package com.example.naotosaito.clocktest;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by nsaito on 2019/12/15.
 */

public class AppSettingsOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "AppSettingsDB.db";

    // アラーム設定用テーブル
    private static final String ALARMSETTINGS_TABLE_NAME = "alarmsettingsdb";
    private static final String ALARMSETTINGS_ID = "_id";
    private static final String ALARMSETTINGS_COLUMN_NAME_STATUS = "status";
    private static final String ALARMSETTINGS_COLUMN_NAME_HOUR = "hour";
    private static final String ALARMSETTINGS_COLUMN_NAME_MINUTE = "minute";
    private static final String ALARMSETTINGS_COLUMN_NAME_WEEK = "week";
    // private static final String ALARMSETTINGS_COLUMN_NAME_SOUND = "sound"; //音楽ファイル追加対応後に使用。

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ALARMSETTINGS_TABLE_NAME +
                    " (" + ALARMSETTINGS_ID + "INTEGER PRIMARY KEY," +
                    ALARMSETTINGS_COLUMN_NAME_STATUS + "INTEGER," +
                    ALARMSETTINGS_COLUMN_NAME_HOUR + "INTEGER," +
                    ALARMSETTINGS_COLUMN_NAME_MINUTE + "INTEGER," +
                    ALARMSETTINGS_COLUMN_NAME_WEEK + "TEXT)";

    private static final String SQL_SELECT_USER = "SELECT FROM " + ALARMSETTINGS_TABLE_NAME;


    public AppSettingsOpenHelper(Context context) {
        super(context, ALARMSETTINGS_TABLE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // テーブル作成。
        db.execSQL(SQL_CREATE_ENTRIES);

        saveData(db, 1, 12, 0, "0");
        saveData(db, 0, 1, 30, "1,2");

        // テーブルの状態を表示。
        db.execSQL(SQL_SELECT_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        onCreate(db);
    }

    /**
     * アラーム設定用DBにデータをセットする。
     * @param db
     * @param status
     * @param hour
     * @param minute
     * @param week
     */
    public void saveData (SQLiteDatabase db, int status, int hour, int minute, String week) {

        ContentValues values = new ContentValues();
        values.put("status", status);
        values.put("hour", hour);
        values.put("minute", minute);
        values.put("week", week);

        db.insert(ALARMSETTINGS_TABLE_NAME, null, values);
    }
}
