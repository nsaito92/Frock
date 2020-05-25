package com.example.naotosaito.clocktest;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
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
     * @param position 取得するDB情の位置。nullの場合は全件取得する。
     * @return DBのcursor
     */
    public Cursor getCursor(String position) {
        Log.d(TAG, "getCursor");
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor;

        if (position == null) {
            cursor = db.query(FrockSettingsOpenHelper.ALARMSETTINGS_TABLE_NAME,
                    null, null, null, null, null, null);
        } else {
            cursor = db.query(FrockSettingsOpenHelper.ALARMSETTINGS_TABLE_NAME,
                    null,
                    FrockSettingsOpenHelper.ALARMSETTINGS_ID + " = " + position,
                    null,
                    null,
                    null,
                    null);
        }
        cursor.moveToFirst();

        return cursor;
    }

    /**
     * 渡されたデータをDBに新規挿入する。
     * @param table_name
     * @param status
     * @param hour
     * @param minute
     * @param week
     * @return
     */
    public boolean insertData(String table_name, int status, int hour, int minute, String week) {
        Log.d(TAG, "insertData");
        boolean result = true;

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FrockSettingsOpenHelper.ALARMSETTINGS_COLUMN_NAME_STATUS, status);
        values.put(FrockSettingsOpenHelper.ALARMSETTINGS_COLUMN_NAME_HOUR, hour);
        values.put(FrockSettingsOpenHelper.ALARMSETTINGS_COLUMN_NAME_MINUTE, minute);
        values.put(FrockSettingsOpenHelper.ALARMSETTINGS_COLUMN_NAME_WEEK, week);

//        db.beginTransaction();

        try {
            Log.d(TAG, "insert");
            db.insert(table_name, null, values);
        } catch (SQLException e) {
            result = false;
        } finally {
//            db.endTransaction();
        }
        return result;
    }

    /**
     * ユーザーが入力したデータをDBに永続化する。
     * @param table_name
     * @param status
     * @param hour
     * @param minute
     * @param week
     */
    public boolean updateData(String table_name, int id, int status, int hour, int minute, String week) {

        boolean result = true;

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = getCursor(String.valueOf(id));

        ContentValues contentValues = new ContentValues();
        contentValues.put(FrockSettingsOpenHelper.ALARMSETTINGS_COLUMN_NAME_STATUS, status);
        contentValues.put(FrockSettingsOpenHelper.ALARMSETTINGS_COLUMN_NAME_HOUR, hour);
        contentValues.put(FrockSettingsOpenHelper.ALARMSETTINGS_COLUMN_NAME_MINUTE, minute);
        contentValues.put(FrockSettingsOpenHelper.ALARMSETTINGS_COLUMN_NAME_WEEK, week);

        try {
            db.update(
                    FrockSettingsOpenHelper.ALARMSETTINGS_TABLE_NAME,
                    contentValues,
                    FrockSettingsOpenHelper.ALARMSETTINGS_ID + " = " + String.valueOf(id),
                    null);
        } catch (SQLException e) {
            result = false;
        }
        cursor.close();

        return result;
    }
    /**
     * 指定された位置のDB情報を削除する。
     * @param position
     */
    public boolean selectDelete(String position) {
        boolean resuit = false;
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();  // トランザクション開始
        try {
            db.delete(FrockSettingsOpenHelper.ALARMSETTINGS_TABLE_NAME,
                    FrockSettingsOpenHelper.ALARMSETTINGS_ID + "=?",
                    new String[]{position});
            db.setTransactionSuccessful();
            resuit = true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();    //トランザクション完了
            db.close();
        }
        return resuit;
    }

    /**
     * DBからデータを取得して、AlarmSettingEntityオブジェクトを返す。
     * @param position 取得するDBポジション
     * @return
     */
    public AlarmSettingEntity getAlarmSettingEntity(String position) {

        Cursor cursor = getCursor(position);
        AlarmSettingEntity entity = new AlarmSettingEntity();
        entity.setmId(cursor.getInt(FrockSettingsOpenHelper.COLUMN_INDEX_ID));
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
        Log.d(TAG, "getAlarmSettingEntityList()");

        Cursor cursor = getCursor(null);

        if (cursor.moveToFirst()) {
            for (boolean next = cursor.moveToFirst(); next; next = cursor.moveToNext()) {
                alarmSettingEntity = new AlarmSettingEntity(
                        cursor.getInt(FrockSettingsOpenHelper.COLUMN_INDEX_ID),
                        cursor.getInt(FrockSettingsOpenHelper.COLUMN_INDEX_STATUS),
                        cursor.getInt(FrockSettingsOpenHelper.COLUMN_INDEX_HOUR),
                        cursor.getInt(FrockSettingsOpenHelper.COLUMN_INDEX_MINUTE),
                        cursor.getString(FrockSettingsOpenHelper.COLUMN_INDEX_WEEK)
                );

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
        Cursor cursor = getCursor(null);

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

    /**
     * 指定されたテーブルへのレコード追加可否のバリデーションチェックを行う。
     * @param tablename データを取得するテーブル名
     * @return true : 追加出来る。 false : 出来ない。
     */
    public boolean isCanRecodeAdd(String tablename) {
        boolean result = false;

        Long recodeCount = queryNumEntries(tablename);
        Log.d(TAG, "recodeCount = " + recodeCount);

        // TODO 新規追加されたテーブルで本メソッドを使用したい場合は処理の追加が必要。
        switch (tablename) {
            case FrockSettingsOpenHelper.ALARMSETTINGS_TABLE_NAME:
                result = recodeCount < FrockSettingsOpenHelper.ALARMSETTINGS_TABLE_MAX_RECORD;
                break;
            default:
                break;
        }
        return result;
    }

    /**
     * アラーム設定DBのレコード数を取得する。
     * @param tablename データを取得するテーブル名
     */
    private Long queryNumEntries(String tablename) {
        SQLiteDatabase db = getWritableDatabase();
        return DatabaseUtils.queryNumEntries(db, tablename);
    }

    /**
     * TODO デバッグ用にアラーム設定用DBにデータをセットする。
     */
    public void saveData () {
        Log.d(TAG, "saveData");

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("status", 0);
        values.put("hour", 18);
        values.put("minute", 30);
        values.put("week", "1,2");

        db.insert(FrockSettingsOpenHelper.ALARMSETTINGS_TABLE_NAME, null, values);
    }
}
