package com.example.naotosaito.clocktest;

import android.app.AlarmManager;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Created by nsaito on 2020/02/03.
 * FrockSettingsOpenHelperを使用するコントローラークラス。
 */

public class FrockSettingsHelperController {

    private static final String TAG = FrockSettingsHelperController.class.getSimpleName();
    private FrockSettingsOpenHelper settingshelper;
    private AlarmSettingEntity alarmSettingEntity;
    private SQLiteDatabase db = null;
    private ContentValues values;

    // コンストラクタでFrockSettingsHelperをnewする。
    public FrockSettingsHelperController() {
        settingshelper = new FrockSettingsOpenHelper(MyApplication.getContext().getApplicationContext());
    }

    /**
     * FrockSettingsOpenHelperの読み書き用データベースのオープンを行う。
     */
    private void getWritableDatabase() {
        db = settingshelper.getWritableDatabase();
    }

    /**
     * DBを閉じる。
     */
    private void dbClose() {
        db.close();
        db = null;
    }

    /**
     * クエリを実行して、Cursorを取得する。
     * @param position 取得するDB情の位置。nullの場合は全件取得する。
     * @return DBのcursor
     */
    private Cursor getCursor(String position) {
        Log.d(TAG, "getCursor");
        getWritableDatabase();
        Cursor cursor;

        if (position == null) {
            cursor = db.query(FrockSettingsOpenHelper.ALARMSETTINGS_TABLE_NAME,
                    null, null, null, null, null, null);
        } else {
            cursor = db.query(FrockSettingsOpenHelper.ALARMSETTINGS_TABLE_NAME,
                    null,
                    FrockSettingsOpenHelper._ID + " = " + position,
                    null,
                    null,
                    null,
                    null);
        }
        cursor.moveToFirst();

        return cursor;
    }

    /**
     * アラームONの設定のクエリを実行して、Cursorを取得する。
     * @param
     * @return DBのcursor
     */
    private Cursor getCursorForValidSettings() {
        Log.d(TAG, "getCursorForValidSettings");

        Cursor cursor = null;

        try {
            getWritableDatabase();
            cursor = db.query(FrockSettingsOpenHelper.ALARMSETTINGS_TABLE_NAME,
                    null,
                    FrockSettingsOpenHelper.COLUMN_NAME_STATUS + " = " + FrockSettingsOpenHelper.VALUE_TRUE,
                    null,
                    null,
                    null,
                    null);
        } catch (SQLException e) {
            e.printStackTrace();
        }

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
    public boolean insertData(String table_name, int status, int hour, int minute, String week, String uri) {
        Log.d(TAG, "insertData");
        boolean result = true;

        getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FrockSettingsOpenHelper.COLUMN_NAME_STATUS, status);
        values.put(FrockSettingsOpenHelper.COLUMN_NAME_HOUR, hour);
        values.put(FrockSettingsOpenHelper.COLUMN_NAME_MINUTE, minute);
        values.put(FrockSettingsOpenHelper.COLUMN_NAME_WEEK, week);
        values.put(FrockSettingsOpenHelper.COLUMN_NAME_SOUND, uri);

        db.beginTransaction();

        try {
            Log.d(TAG, "insert");
            db.insert(table_name, null, values);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            result = false;
        } finally {
            db.endTransaction();
            dbClose();
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
     * @param uri
     */
    public boolean updateData(String table_name, int id, int status, int hour, int minute, String week, String uri) {

        boolean result = true;

        getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(FrockSettingsOpenHelper.COLUMN_NAME_STATUS, status);
        contentValues.put(FrockSettingsOpenHelper.COLUMN_NAME_HOUR, hour);
        contentValues.put(FrockSettingsOpenHelper.COLUMN_NAME_MINUTE, minute);
        contentValues.put(FrockSettingsOpenHelper.COLUMN_NAME_WEEK, week);
        contentValues.put(FrockSettingsOpenHelper.COLUMN_NAME_SOUND, uri);

        try {
            db.update(
                    FrockSettingsOpenHelper.ALARMSETTINGS_TABLE_NAME,
                    contentValues,
                    FrockSettingsOpenHelper._ID + " = " + String.valueOf(id),
                    null);
        } catch (SQLException e) {
            result = false;
        } finally {
            dbClose();
        }

        return result;
    }
    /**
     * 指定された位置のDB情報を削除する。
     * @param position
     */
    public boolean selectDelete(String position) {
        boolean resuit = false;
        getWritableDatabase();

        db.beginTransaction();  // トランザクション開始
        try {
            db.delete(FrockSettingsOpenHelper.ALARMSETTINGS_TABLE_NAME,
                    FrockSettingsOpenHelper._ID + "=?",
                    new String[]{position});
            db.setTransactionSuccessful();
            resuit = true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();    //トランザクション完了
            dbClose();
        }
        return resuit;
    }

    /**
     * DBからデータを取得して、AlarmSettingEntityオブジェクトを返す。
     * @param position 取得するDBポジション
     * @return
     */
    public AlarmSettingEntity getAlarmSettingEntity(String position) {
        getWritableDatabase();
        Cursor cursor = getCursor(position);

        AlarmSettingEntity entity = new AlarmSettingEntity();
        try {
            entity.setmId(cursor.getInt(FrockSettingsOpenHelper.COLUMN_INDEX_ID));
            entity.setmStatus(cursor.getInt(FrockSettingsOpenHelper.COLUMN_INDEX_STATUS));
            entity.setmHour(cursor.getInt(FrockSettingsOpenHelper.COLUMN_INDEX_HOUR));
            entity.setmMinute(cursor.getInt(FrockSettingsOpenHelper.COLUMN_INDEX_MINUTE));
            entity.setmWeek(cursor.getString(FrockSettingsOpenHelper.COLUMN_INDEX_WEEK));
            entity.setmSoundUri(cursor.getString(FrockSettingsOpenHelper.COLUMN_INDEX_SOUND));
        } finally {
            cursor.close();
            dbClose();
        }

        return entity;
    }

    /**
     * DBからデータを取得して、アラーム設定リストオブジェクトを返す。
     * @return
     */
    public List<AlarmSettingEntity> getAlarmSettingEntityList(List<AlarmSettingEntity> alarmSettingEntityList) {
        Log.d(TAG, "getAlarmSettingEntityList()");

        getWritableDatabase();
        Cursor cursor = getCursor(null);
        try {

            if (cursor.moveToFirst()) {
                for (boolean next = cursor.moveToFirst(); next; next = cursor.moveToNext()) {
                    alarmSettingEntity = new AlarmSettingEntity(
                            cursor.getInt(FrockSettingsOpenHelper.COLUMN_INDEX_ID),
                            cursor.getInt(FrockSettingsOpenHelper.COLUMN_INDEX_STATUS),
                            cursor.getInt(FrockSettingsOpenHelper.COLUMN_INDEX_HOUR),
                            cursor.getInt(FrockSettingsOpenHelper.COLUMN_INDEX_MINUTE),
                            cursor.getString(FrockSettingsOpenHelper.COLUMN_INDEX_WEEK),
                            cursor.getString(FrockSettingsOpenHelper.COLUMN_INDEX_SOUND)
                    );

                    alarmSettingEntityList.add(alarmSettingEntity);
                }
            }
        } finally {
            cursor.close();
            dbClose();
        }
        return alarmSettingEntityList;
    }

    /**
     * DBのデータを取得して、StringBuilderで整形して返す。
     * @return
     */
    public StringBuilder getAlarmSettingsToString() {
        StringBuilder stringBuilder = new StringBuilder();
        getWritableDatabase();
        Cursor cursor = getCursor(null);

        try {
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
        } finally {
            cursor.close();
            dbClose();
        }

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
        Long recode = null;
        getWritableDatabase();
        recode = DatabaseUtils.queryNumEntries(db, tablename);
        dbClose();
        return recode;
    }

    /**
     * DBのデータを取元に、Calenderオブジェクトを返す。
     * @return
     * @param mId
     */
    public Calendar CreateCalendarFromDB(int mId) {
        Log.d(TAG, "CreateCalendarFromDB");

        // 戻り値となるCalender。現在の時間で初期化する。
        Calendar alarmCld = Calendar.getInstance();
        alarmCld = ClockUtil.getTodayCalender();

        // アラーム設定候補のカレンダーリスト
        ArrayList<Calendar> cldlist = CreateCalendarOneWeek(mId);

        // アラーム設定候補のカレンダーリストで、一番近いCalenderを戻り値にする。
        for (int i=0; i<cldlist.size(); i++) {
            if (i == 0) {
                alarmCld = cldlist.get(i);
            } else if (i > 0) {
                if (cldlist.get(i).before(alarmCld)) {
                    alarmCld = cldlist.get(i);
                }
            }
        }
        return alarmCld;
    }

    /**
     * DBの曜日データを元に、カレンダーのリストを返す。
     * @return アラーム設定候補のカレンダーのリスト
     * @param mId 取得するDBのID
     */
    private ArrayList<Calendar> CreateCalendarOneWeek(int mId) {
        Log.d(TAG, "CreateCalendarOneWeek : " + mId);

        // アラーム設定候補のカレンダーを格納するリスト
        ArrayList<Calendar> cldlist = new ArrayList();

        // 選択された曜日データを取得
        Cursor cursor = getCursor(String.valueOf(mId));
        String alm_week[] = ClockUtil.convertStringToArray(cursor.getString(FrockSettingsOpenHelper.COLUMN_INDEX_WEEK));

        try {
            for (String i : alm_week) {
                // DBデータを元にCalender生成
                Calendar dbCld = Calendar.getInstance();
                dbCld.set(Calendar.HOUR_OF_DAY, cursor.getInt(FrockSettingsOpenHelper.COLUMN_INDEX_HOUR));
                dbCld.set(Calendar.MINUTE, cursor.getInt(FrockSettingsOpenHelper.COLUMN_INDEX_MINUTE));
                dbCld.set(Calendar.SECOND, 0); // 0秒を指定する。
                // Calenderクラスでは、曜日は0からではなく1から始まっているため、1+して処理する。
                dbCld.set(Calendar.DAY_OF_WEEK, Integer.parseInt(i) + 1); //曜日

                // 今日か、今週中の場合は設定された時刻通りにアラームを設定する。
                // カレンダーの時間をすでに過ぎている場合、来週になるようにカレンダーを調整。
                if (dbCld.compareTo(ClockUtil.getTodayCalender()) < 0) {
                    dbCld.add(Calendar.DATE, ClockUtil.DAY_OF_WEEK);
                }
                cldlist.add(dbCld);
            }
        } finally {
            cursor.close();
            dbClose();
        }
        return cldlist;
    }

    /**
     * DBにアラームONで保存されている設定の中で、最も鳴動予定が近いCalenderを返却する。
     * @return
     */
    public AlarmManagerSetDataEntity getClosestCalender() {
        Log.d(TAG, "getValidCalender");

        AlarmManagerSetDataEntity closestEntity = null;

        // ONで保存されているアラーム設定のCalenderオブジェクト。
        ArrayList<AlarmManagerSetDataEntity> alarmManagerSetDataList = new ArrayList<>();

        // ONで保存されているアラーム設定の取得依頼。
        Cursor cursor = null;

        try {
            cursor = getCursorForValidSettings();

            // クエリ結果、データが存在していれば、Calenderのリストを取得。
            if (cursor.moveToFirst()) {
                // DBデータを元にCalender生成
                for (int cnt = 0; cnt < cursor.getCount(); cnt++) {

                    Calendar alarmCld = Calendar.getInstance();

                    // DBから取得データをのset用Entity。
                    AlarmManagerSetDataEntity entity = new AlarmManagerSetDataEntity();

                    // 有効になっている曜日データを取得
                    String alm_week[] = ClockUtil.convertStringToArray(cursor.getString(FrockSettingsOpenHelper.COLUMN_INDEX_WEEK));

                    // DBのIDを設定
                    entity.setmId(cursor.getInt(FrockSettingsOpenHelper.COLUMN_INDEX_ID);

                    // 曜日ごとにCalenderを生成
                    for (int cnt2=0; cnt2<alm_week.length; cnt2++) {
                        String dayofweek = alm_week[cnt2];

                        // 一番近い予定のCalenderをaddする。
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, cursor.getInt(FrockSettingsOpenHelper.COLUMN_INDEX_HOUR));
                        calendar.set(Calendar.MINUTE, cursor.getInt(FrockSettingsOpenHelper.COLUMN_INDEX_MINUTE));
                        calendar.set(Calendar.SECOND, 0); // 0秒を指定する。
                        // Calenderクラスでは、曜日は0からではなく1から始まっているため、1+して処理する。
                        calendar.set(Calendar.DAY_OF_WEEK, Integer.parseInt(dayofweek) + 1); //曜日

                        // 今日か、今週中の場合は設定された時刻通りにアラームを設定する。
                        // カレンダーの時間をすでに過ぎている場合、来週になるようにカレンダーを調整。
                        if (calendar.compareTo(ClockUtil.getTodayCalender()) < 0) {
                            calendar.add(Calendar.DATE, ClockUtil.DAY_OF_WEEK);
                        }

                        if (cnt2 == 0) {
                            alarmCld = calendar;
                        } else if (cnt2 > 0) {
                            if (calendar.before(alarmCld)) {
                                alarmCld = calendar;
                            }
                        }
                    }
                    // レコードごとのAlarmManagerセット候補をセットする。
                    entity.setmCalender(alarmCld);
                    alarmManagerSetDataList.add(entity);

                    cursor.moveToNext();
                }

                // 各レコードごとの最も近いアラーム設定の中で、一番近い鳴動予定を判断する。
                closestEntity = ClockUtil.isClosestCalender(alarmManagerSetDataList);

            } else {
                // クエリの結果が何も取得できなければ、データなしでreturn
                Log.d(TAG, "ValidCalender is None");
                return null;
            }
        } finally {
            cursor.close();
            dbClose();
        }

        return closestEntity;
    }

    /**
     * TODO デバッグ用にアラーム設定用DBにデータをセットする。
     */
    public void saveData () {
        Log.d(TAG, "saveData");

        getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("status", 0);
        values.put("hour", 18);
        values.put("minute", 30);
        values.put("week", "1,2");

        db.beginTransaction();
        try {
            db.insert(FrockSettingsOpenHelper.ALARMSETTINGS_TABLE_NAME, null, values);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            dbClose();
        }
    }
}
