package com.example.naotosaito.clocktest;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

/**
 * Created by naotosaito on 2019/03/10.
 *
 * アプリケーション全体で共通の処理を行う。
 * インスタンス化不可。
 */

public class ClockUtil {
    private static final String TAG = "ClockUtil";

    final static String ALARM_SERVICE_KEY = "alarmservice_boolean";
    final static String ALARMTIME_HOUR_KEY = "alarmtime_hour";
    final static String ALARMTIME_MINUTE_KEY = "alarmtime_minute";
    final static String ALARMTIME_WEEK_KEY = "alarmtime_week";
    final static String PENDING_ALARMSERVICE_KEY = "pendingalarmservice_boolean";

    final static int DAY_OF_WEEK = 7;

    final static int TRUE = 1;
    final static int FALSE = 0;

    // アラーム設定デフォルト設定用
    final static int ALARMTIME_DEFAULT = 0;

    // PendingIntent RequestCode
    public class PendingIntentRequestCode {
        final static int ALARMSERVICE = 0;
        final static int ALARM_NOTIFICATION = 1;
    }

    // 間違ってインスタンスを生成された場合、コンストラクタで例外を返す。
    private ClockUtil() {
        throw new AssertionError();
    }

    /**
     * Preferenceに整数を保存する
     * @param integer 保存する整数。
     */
    public static void setPrefInt(String name, String prefkey, int integer) {
        // Preferenceへのアクセス
        SharedPreferences pref_int = MyApplication.getContext().
                getSharedPreferences(name, MyApplication.getContext().MODE_PRIVATE);

        // Preferenceの保存
        SharedPreferences.Editor editor_int = pref_int.edit();
        editor_int.putInt(prefkey, integer);
        editor_int.commit();
    }

    /**
     * Preferenceに保存されている整数を取得する。
     * @return 指定されたkeyに保存されている整数。
     */
    public static int getPrefInt (String name, String prefkey) {
        SharedPreferences prefer_int = MyApplication.getContext().
                getSharedPreferences(name, MyApplication.getContext().MODE_PRIVATE);
        return prefer_int.getInt(prefkey, MyApplication.getContext().MODE_PRIVATE);
    }

    /**
     * 指定されたPrefKeyに紐づけてbooleanを保存する。
     * @return
     */
    public static void setPrefBoolean (String name, String prefkey, boolean value) {
        // Preferenceへのアクセス
        SharedPreferences prefer_value = MyApplication.getContext().
                getSharedPreferences(name, MyApplication.getContext().MODE_PRIVATE);

        // Preferenceの保存
        SharedPreferences.Editor editor_value = prefer_value.edit();
        editor_value.putBoolean(prefkey, value);
        editor_value.commit();
    }

    /**
     * 指定されたkeyに保存されているbooleanを取得する。
     * @return
     */
    public static boolean getPrefBoolean (String name, String prefkey) {
        SharedPreferences prefer_value = MyApplication.getContext().
                getSharedPreferences(name, MyApplication.getContext().MODE_PRIVATE);
        return prefer_value.getBoolean(prefkey, false);
    }

    /**
     * Preferenceに保存されている文字列を取得する。
     * @return 指定されたkeyに保存されている整数。
     */
    public static String getPrefString (String name, String prefkey) {
        SharedPreferences prefString = MyApplication.getContext().
                getSharedPreferences(name, MyApplication.getContext().MODE_PRIVATE);
        return prefString.getString(prefkey, "");
    }

    /**
     * 受け取った整数を真偽値で返却する。
     * @return
     */
    public static boolean convertInt (int value) {
        if (value == TRUE) {
            return true;
        } else if (value == FALSE) {
            return false;
        }
        return false;
    }

    /**
     * 受け取ったbooleanを0か1で返却する。
     * @param checked
     * @return
     */
    public static int convertBoolean(boolean checked) {
        int result = FALSE;
        if (checked) {
            result = TRUE;
        }
        return result;
    }

    /**
     * Serviceの起動状態をチェックする。
     * @return true : Serviceの起動中、false : 起動していない。
     */
    public static boolean isYourServiceWorking() {
        Log.d(TAG, "isYourServiceWorking");
        ActivityManager manager = (ActivityManager) MyApplication.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo serviceinfo :
                manager.getRunningServices(Integer.MAX_VALUE)) {
            // TODO AlarmService以外のServiceでも共通利用できる様にしたい。
            if(AlarmService.class.getName().equals(serviceinfo.service.getClassName())) {
                Log.d(TAG, "isYourServiceWorking = " + true);
                return true;
            }
        }
        Log.d(TAG, "isYourServiceWorking = " + false);
        return false;
    }

    /**
     * AlarmServiceの起動するPendingIntentの状態をセットする。
     * @param value PendingIntentを有無
     */
    public static void setAlarmPendingIntent(boolean value) {
        // Preferenceへのアクセス
        SharedPreferences pref_almvalue =
                MyApplication.getContext().
                        getSharedPreferences("PendingAlarm", MyApplication.getContext().MODE_PRIVATE);

        // Preferenceの保存
        SharedPreferences.Editor editor_almvalue = pref_almvalue.edit();
        editor_almvalue.putBoolean(PENDING_ALARMSERVICE_KEY, value);
        editor_almvalue.commit();
    }

    /**
     * Pending中のアラーム設定の有無を返す。
     * @return Pending中のアラーム設定の有無
     */
    public static boolean getAlarmPendingIntent() {
        SharedPreferences pref_almvalue =
                MyApplication.getContext().
                        getSharedPreferences("PendingAlarm", MyApplication.getContext().MODE_PRIVATE);
        return pref_almvalue.getBoolean(PENDING_ALARMSERVICE_KEY, false);
    }

    /**
     * 受け取った時間の文字列の結合、調整をして返却する。
     * @param hour 時
     * @param minute 分
     * @return 「xx:xx」という形式に変換して返却
     */
    public static StringBuilder shapingStringTime(String hour, String minute) {

        StringBuilder stringBuilder = new StringBuilder();

        // 文字列の調整。時間と分を、一つの文字列に統合。
        // 一桁の場合見栄えが悪いので、「0」を追加する。
        if (hour.length() < 2) {
            stringBuilder.insert(0, "0");
        }
        stringBuilder.append(hour);
        stringBuilder.append(":");
        if (minute.length() < 2) {
            stringBuilder.append("0");
        }
        stringBuilder.append(minute);

        return stringBuilder;
    }

//    /**
//     * アラームが動作するサービスの設定を行う
//     */
//    public static void alarmServiceSet() {
//        Log.d(TAG, "alarmServiceSet");
//
//        // TODO 検索結果が入るcursorを使用して、アラーム設定に使用する。
//
//        // 以下の場合、アラームサービスの起動を行わない。
//        // 1. アラーム設定のトグルボタンが無効の場合
//        // 2. アラームが鳴動中である場合
////        if (!ClockUtil.getPrefBoolean("alarmservice", ClockUtil.ALARM_SERVICE_KEY) ||
////                ClockUtil.isYourServiceWorking()) {
////            return;
////        }
//
//        // AlarmService起動用のIntent、PendingIntentを作成
//        Context context = MyApplication.getContext();
//        Intent intent = new Intent(MyApplication.getContext(), AlarmService.class);
//        int requestcode = 1;
//        PendingIntent pendingintent = PendingIntent.getService(
//                context, requestcode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        // TODO 各アラーム設定のPreferenceにアクセスする
//
//        // アラームを実行する時間の設定を準備
//        // TODO ユーティリティクラスにした方が良さそう
//
//        Calendar alarmCld = Calendar.getInstance();
//
//        // 曜日設定がすでに保存済みであるかチェック。
////        if (getSelectedWeeks(ALARMTIME_WEEK_KEY) != null) {
////            cld_alarm = ClockUtil.getAlarmCalender();
//            // TODO Prefではなく、DB情報を元にCalenderを作成する
////        alarmCld = ClockUtil.getAlarmCalenderFromDB();
////        }
//
//        // AlarmManagerのset()でAlarmManagerでセットした時間に、Serviceを起動
//        AlarmManager alarmmanager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
//        alarmmanager.set(AlarmManager.RTC, alarmCld.getTimeInMillis(), pendingintent);
//
//        // PendingIntentをセットしたためflagを有効化する
//        ClockUtil.setAlarmPendingIntent(true);
//
//        Log.d(TAG, "The alarm was set at " + alarmCld.getTime());
//        Toast.makeText(MyApplication.getContext(),
//                "The alarm was set at " + alarmCld.getTime(),
//                Toast.LENGTH_SHORT).show();
//
////        Log.d(TAG, "AlarmSettingTime is "
////                + calender.YEAR
////                + calender.MONTH
////                + calender.DAY_OF_MONTH
////                + calender.HOUR_OF_DAY
////                + calender.MINUTE
////                + calender.SECOND + " !!");
//    }

    /**
     * アラームが動作する曜日設定のPreferenceの配列を文字列に変換・保存
     * @param mSelectedWeeks 保存する曜日設定
     */
    public static String setSelectedWeeks(ArrayList mSelectedWeeks) {
        Log.d(TAG, "setSelectedWeeks");

        // 曜日が何も選択されていなかった場合は、保存処理は行わない。
        if (mSelectedWeeks == null || mSelectedWeeks.size() == 0) {
            return null;
        }

        StringBuffer buffer = new StringBuffer();
        String stringItem = null;

        // 曜日順に整列して保存したいため、配列の整列を行う。
        Collections.sort(mSelectedWeeks);

        // 選択された曜日を確認し、StringBufferに「,」区切りで追加
        for (Object item : mSelectedWeeks) {
            buffer.append(item+",");
        }
        // StringBufferを、一つの文字列に変換する。
        if (buffer != null) {
            String buf = buffer.toString();
            stringItem = buf.substring(0, buf.length() -1);
        }
        return stringItem;

//        // Preferenceの保存
//        SharedPreferences prefer_week = MyApplication.getContext().
//                getSharedPreferences("week", MyApplication.getContext().MODE_PRIVATE);
//        SharedPreferences.Editor editor = prefer_week.edit();
//        editor.putString(ClockUtil.ALARMTIME_WEEK_KEY, stringItem).commit();
    }

    /**
     * 「a,b,c」という様になっている文字列を、配列形式に変換して返却する。
     * @param string
     * @return 保存されたアラーム曜日情設定の配列
     */
    public static String[] convertStringToArray(String string) {
        Log.d(TAG, "convertStringToArray");

        // 渡された文字列が非null、文字数が0以上の場合、文字列を分割して返却する。
        if (string != null && string.length() != 0 ) {
            return string.split(",");
        } else {
            return null;
        }
        // 現在の日時、曜日を取得、何日後か確認
        // 現在の日時から、何日後の日にちを確認
    }

    /**
     * 今日のカレンダーオブジェクトを返却する。
     * @return
     */
    public static Calendar getTodayCalender() {
        // 今日の日時を元にするCalender
        Calendar todayCalender = Calendar.getInstance();
        todayCalender.get(Calendar.YEAR);
        todayCalender.get(Calendar.MONTH);
        todayCalender.get(Calendar.DAY_OF_MONTH);
        return todayCalender;
    }

    /**
     * 今日の曜日情報を返却する。
     * @return
     */
    public static int isTodayWeek() {
        int week = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        Log.d(TAG, "isTodayWeek = " + week);
        return week;
    }

    //    /**
//     * Preference情報を元にアラームが動作予定のCalender情報を作成し、返す。
//     * @return アラームが動作予定のCalender情報
//     */
//    public static Calendar getAlarmCalender() {
//        Log.d(TAG, "getAlarmCalender");
//
//        Calendar todayCalender = getTodayCalender();
//
//        // 戻り値となるアラーム鳴動予定を入れるCalender
//        Calendar cld_alarm = Calendar.getInstance();
//
//        // 選択された曜日データを取得
//        String pref_week[] = ClockUtil.getSelectedWeeks(ClockUtil.ALARMTIME_WEEK_KEY);
//
//        ArrayList<Calendar> cldlist = new ArrayList<>();
//
//        // Preferenceの曜日データを元に、鳴動予定のCalenderを一通り作成する。
//        for (String i : pref_week) {
//
//            // Preferenceに保存されているデータを元にするCalender
//            Calendar cld_pref = Calendar.getInstance();
//            cld_pref.set(Calendar.HOUR_OF_DAY, getPrefInt("hour", ClockUtil.ALARMTIME_HOUR_KEY)); // 時
//            cld_pref.set(Calendar.MINUTE, getPrefInt("minute", ClockUtil.ALARMTIME_MINUTE_KEY));    // 分
//            cld_pref.set(Calendar.SECOND, 0);                   // 秒
//
//            // Calenderクラスでは、曜日は0からではなく1から始まっているため、1+して処理する。
//            cld_pref.set(Calendar.DAY_OF_WEEK, Integer.parseInt(i) + 1); //曜日
//            Log.d(TAG,"cld_pref.compareTo(cld_today) = " + cld_pref.compareTo(todayCalender));
//
//            if (cld_pref.compareTo(todayCalender) >= 0) {
//                // 現在か、今週の場合は設定された時刻通りにアラームを設定する。
//            } else if (cld_pref.compareTo(todayCalender) < 0) {
//                // 過ぎてしまっている場合は、来週になるようにCalenderを調整。
//                cld_pref.add(Calendar.DATE, DAY_OF_WEEK);
//            }
//            cldlist.add(cld_pref);
//        }
//
//        // returmするcalendarの初期値を指定。
//        cld_alarm = cldlist.get(0);
//
//        // 設定されたCalenderを比較していき、一番今日に近いCalenderはどれか確認する。
//        for (int i=0; i<cldlist.size(); i++) {
//            if (cld_alarm.after(cldlist.get(i))) {
//                Log.d("NSAITOTEST","cld_alarm update");
//                cld_alarm = cldlist.get(i);
//            }
//        }
//        Log.d(TAG, "getAlarmCalender() return = " + cld_alarm.getTime());
//        return cld_alarm;
//    }

    /**
     * 受け取ったカレンダーの文字列情報を、ユーザーが読み取れる様に整形する。
     * @param string
     * @return
     */
    public static String convertStringToWeek(String string) {
        // 設定がnullだった場合は無しと記載
        if (string == null) {
            return "曜日設定無し";
        }

        StringBuilder stringBuilder = new StringBuilder();  // 整形文字の格納先

        for (int i=0; i<string.length(); i++) {
            if (!string.substring(i, i+1).equals(",")) {    // 「,」ではない場合、処理する。
                if (i>0) {                    // 二回目以降の場合は、末尾に「,」をつける。
                    stringBuilder.append(",");
                }
                switch(string.substring(i, i+1)) {
                    case "0":
                        stringBuilder.append("日");
                        break;
                    case "1":
                        stringBuilder.append("月");
                        break;
                    case "2":
                        stringBuilder.append("火");
                        break;
                    case "3":
                        stringBuilder.append("水");
                        break;
                    case "4":
                        stringBuilder.append("木");
                        break;
                    case "5":
                        stringBuilder.append("金");
                        break;
                    case "6":
                        stringBuilder.append("土");
                        break;
                    default:
                        break;
                }
            }
        }
        return stringBuilder.toString();
    }

    /**
     * トースト表示を行う。
     * @param message 表示するメッセージ
     */
    public static void ToastShow(String message) {
        Toast.makeText(MyApplication.getContext(),
                message,
                Toast.LENGTH_SHORT).show();
    }

    /**
     * 渡されたCalenderのリストから、最も現在時間に近いCalenderを返す。
     * @param validCalenderList
     */
    public static Calendar isClosestCalender(ArrayList<Calendar> validCalenderList) {
        Log.d(TAG, "isClosestCalender");
        Calendar closestCalender = Calendar.getInstance();

        for (int i=0; i<validCalenderList.size(); i++) {
            if (i == 0) {
                closestCalender = validCalenderList.get(i);
            } else if (i > 0) {
                if (validCalenderList.get(i).before(closestCalender)) {
                    closestCalender = validCalenderList.get(i);
                }
            }
        }
        return closestCalender;
    }
}
