package com.example.naotosaito.clocktest;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by nsaito on 2019/12/15.
 */

public class FrockSettingsOpenHelper extends SQLiteOpenHelper {

    private static final String TAG = "FrockSettingsOpenHelper";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "AppSettingsDB.db";

    // アラーム設定用テーブル
    public static final String ALARMSETTINGS_TABLE_NAME = "alarmsettingsdb";
    public static final String ALARMSETTINGS_ID = "_id";
    public static final String ALARMSETTINGS_COLUMN_NAME_STATUS = "status";
    public static final String ALARMSETTINGS_COLUMN_NAME_HOUR = "hour";
    public static final String ALARMSETTINGS_COLUMN_NAME_MINUTE = "minute";
    public static final String ALARMSETTINGS_COLUMN_NAME_WEEK = "week";
    // public static final String ALARMSETTINGS_COLUMN_NAME_SOUND = "sound"; //音楽ファイル追加対応後に使用。

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ALARMSETTINGS_TABLE_NAME +
                    " (" + ALARMSETTINGS_ID + "INTEGER PRIMARY KEY," +
                    ALARMSETTINGS_COLUMN_NAME_STATUS + "INTEGER," +
                    ALARMSETTINGS_COLUMN_NAME_HOUR + "INTEGER," +
                    ALARMSETTINGS_COLUMN_NAME_MINUTE + "INTEGER," +
                    ALARMSETTINGS_COLUMN_NAME_WEEK + "TEXT)";

    private static final String SQL_SELECT_USER = "SELECT FROM " + ALARMSETTINGS_TABLE_NAME;


    public FrockSettingsOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "FrockSettingsOpenHelper");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate");

        // テーブル作成。
        db.execSQL(SQL_CREATE_ENTRIES);

        // TODO テストコード
        saveData(db, ALARMSETTINGS_TABLE_NAME,1,21,0,"0,1");
        saveData(db, ALARMSETTINGS_TABLE_NAME,0,11,45,"2,4");

        // テーブルの状態を表示。
        db.execSQL(SQL_SELECT_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        Log.d(TAG, "onUpgrade");

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
    public void saveData (SQLiteDatabase db, String table_name, int status, int hour, int minute, String week) {
        Log.d(TAG, "saveData");

        ContentValues values = new ContentValues();
        values.put("status", status);
        values.put("hour", hour);
        values.put("minute", minute);
        values.put("week", week);

        db.insert(table_name, null, values);
    }
}
