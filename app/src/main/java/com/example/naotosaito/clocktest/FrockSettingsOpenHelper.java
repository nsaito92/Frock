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

    // カラム
    public static final String ALARMSETTINGS_ID = "_id";
    public static final String ALARMSETTINGS_COLUMN_NAME_STATUS = "status";
    public static final String ALARMSETTINGS_COLUMN_NAME_HOUR = "hour";
    public static final String ALARMSETTINGS_COLUMN_NAME_MINUTE = "minute";
    public static final String ALARMSETTINGS_COLUMN_NAME_WEEK = "week";
    // public static final String ALARMSETTINGS_COLUMN_NAME_SOUND = "sound"; //音楽ファイル追加対応後に使用。

    // カラムindex
    public static final int COLUMN_INDEX_ID = 0;
    public static final int COLUMN_INDEX_STATUS = 1;
    public static final int COLUMN_INDEX_HOUR = 2;
    public static final int COLUMN_INDEX_MINUTE = 3;
    public static final int COLUMN_INDEX_WEEK = 4;

    // SQL文
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ALARMSETTINGS_TABLE_NAME +
                    " (" + ALARMSETTINGS_ID + " INTEGER PRIMARY KEY," +
                    ALARMSETTINGS_COLUMN_NAME_STATUS + " INTEGER," +
                    ALARMSETTINGS_COLUMN_NAME_HOUR + " INTEGER," +
                    ALARMSETTINGS_COLUMN_NAME_MINUTE + " INTEGER," +
                    ALARMSETTINGS_COLUMN_NAME_WEEK + " TEXT)";

    private static final String SQL_SELECT_USER = "SELECT FROM " + ALARMSETTINGS_TABLE_NAME;

    // バリデーションチェック用定数
    public static final long ALARMSETTINGS_TABLE_MAX_RECORD = 10;

    public FrockSettingsOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "FrockSettingsOpenHelper");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate");

        // テーブル作成。
        db.execSQL(SQL_CREATE_ENTRIES);
        Log.d(TAG, "SQL_CREATE_ENTRIES = " + SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        Log.d(TAG, "onUpgrade");

        onCreate(db);
    }
}
