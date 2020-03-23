package com.example.naotosaito.clocktest;

import android.database.Cursor;
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
        // DB作成
        SQLiteDatabase db = settingshelper.getReadableDatabase();

        // TODO 仮実装で、DBの一行目のデータを取得するが、最終的にはユーザー操作により変更できる様にする。
        Cursor cursor = db.query(FrockSettingsOpenHelper.ALARMSETTINGS_TABLE_NAME,
                null, null, null, null, null, null);
        cursor.moveToFirst();

        return cursor;
    }

    /**
     *
     * @param table_name
     * @param status
     * @param hour
     * @param minute
     * @param week
     */
    public void updateData(String table_name, int status, int hour, int minute, String week) {

        SQLiteDatabase db = settingshelper.getReadableDatabase();

    }
}
