package com.example.naotosaito.clocktest;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.Calendar;

/**
 * AlarmManager・PendingIntentを使用して、AlarmServiceをセットするクラス。
 * Created by nsaito on 2020/06/12.
 */

class AlarmServiceSetter {
    private final static String TAG = AlarmServiceSetter.class.getSimpleName();
    private static final int TIME_TO_RUN_SOOZE = 5;
//    private static final int TIME_TO_RUN_SOOZE = 1;   // test用1分後にスヌーズ設定

    /**
     * DBデータを元にアラームを設定するCalenderを取得し、AlarmManagerにセットする。
     */
    public void updateAlarmService() {
        Log.d(TAG, "updateAlarmService");

        // AlarmManagerにセットするCalender
        AlarmManagerSetDataEntity closestEntity = getAlarmSetCalender();

        if (closestEntity != null) {
            // 一番近いCalenderをAlarmManagerにセットする。
            alarmManagerSet(closestEntity);

            // 明示的にReceiverを有効にする。
            ComponentName receiver = new ComponentName(MyApplication.getContext(), FrockReceiver.class);
            PackageManager packageManager = MyApplication.getContext().getPackageManager();

            packageManager.setComponentEnabledSetting(
                    receiver,
                    packageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    packageManager.DONT_KILL_APP);

        } else {
            // クエリの結果が何も取得できなければ、現在設定されているアラーム設定をキャンセルする。
            alarmManagerCancel();

            // 明示的にReceiverを無効にする。
            ComponentName receiver = new ComponentName(MyApplication.getContext(), FrockReceiver.class);
            PackageManager packageManager = MyApplication.getContext().getPackageManager();

            packageManager.setComponentEnabledSetting(
                    receiver,
                    packageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    packageManager.DONT_KILL_APP);
        }
    }

    /**
     * アラームの状態に応じて必要なCalenderを生成して返却する。
     * @return
     */
    private AlarmManagerSetDataEntity getAlarmSetCalender() {
        // 返却するCalender
        AlarmManagerSetDataEntity closestEntity = null;

        // スヌーズ状態をチェックして、状態によってAlarmManagerにセットするCalenderを分ける。
        int snoozeCount = ClockUtil.getPrefInt("alarmservice", ClockUtil.SharedPreferencesKey.SNOOZE_COUNT);

        Log.d(TAG, "snoozeCount = " + snoozeCount);

        // スヌーズ用カウンタが0以上、5未満の時
        if (0 < snoozeCount && snoozeCount < 5) {
            closestEntity = createSnoozeCalender();

        } else {
            // スヌーズ回数の上限に達したため、カウントをリセット
            ClockUtil.setPrefInt("alarmservice", ClockUtil.SharedPreferencesKey.SNOOZE_COUNT, 0);

            // 通知のキャンセル
            NotificationManagerController notificationManagerController = new NotificationManagerController(MyApplication.getContext());
            notificationManagerController.notificationCansel(NotificationManagerController.NotificationID.SNOOZE);

            // DBのクエリを叩いて、ONになっているアラーム設定を取得。
            FrockSettingsHelperController controller = new FrockSettingsHelperController();
            closestEntity = controller.getClosestCalender();
        }
        Log.d(TAG, "closestEntity = " + closestEntity.getmCalender().getTime());
        return closestEntity;
    }


    /**
     * スヌーズ設定用 AlarmManagerSetDataEntity を生成する。
     * @return
     */
    private AlarmManagerSetDataEntity createSnoozeCalender() {
        Log.d(TAG, "createSnoozeCalender");

        AlarmManagerSetDataEntity entity = null;
        int id;
        Calendar calender = null;

        // Calenderは現在から5分後、アラームのIndexIDは最後に鳴動したものを使用する。
        calender = ClockUtil.getTodayCalender();
        calender.add(Calendar.MINUTE, TIME_TO_RUN_SOOZE);

        id = ClockUtil.getPrefInt("alarmservice", ClockUtil.SharedPreferencesKey.LAST_ALARM_INDEX);
        Log.d(TAG, "id = " + id);

        if (calender != null && id >= 0) {
            entity = new AlarmManagerSetDataEntity(id, calender);
        }

        return entity;
    }

    /**
     * AlarmServiceに必要なデータを取得して、AlarmManagerに起動予定をセットする。
     * @param closestEntity AlarmManagerにセットする AlarmManagerSetDataEntity
     */
    private void alarmManagerSet(AlarmManagerSetDataEntity closestEntity) {
        Log.d(TAG, "AlarmManagerSet");

        // PendingIntent生成。
        Context context = MyApplication.getContext();
        Intent startIntent = new Intent(context, AlarmService.class);

        startIntent.putExtra("COLUMN_INDEX_ID", closestEntity.getmId());

        PendingIntent pendingintent = PendingIntent.getService(
                context, ClockUtil.PendingIntentRequestCode.ALARMSERVICE, startIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        // AlarmServiceの起動予定をAlarmManagerにset。
        AlarmManager alarmmanager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Oreo以降の場合はバッググラウンド制限がかかり、サービスが起動出来ない場合があるため、使用APIを変更する。
        if (Build.VERSION_CODES.O <= Build.VERSION.SDK_INT) {
            Log.d(TAG, "### AlarmService Oreo");
            alarmmanager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC,
                    closestEntity.getmCalender().getTimeInMillis(),
                    pendingintent);
        } else {
            alarmmanager.set(
                    AlarmManager.RTC,
                    closestEntity.getmCalender().getTimeInMillis(),
                    pendingintent);
        }
        Log.d(TAG, "Set " + closestEntity.getmCalender().getTime() + " to AlarmManager");
    }

    /**
     * AlarmManagerにセット済みのAlarmServiceをキャンセルする。
     */
    private void alarmManagerCancel() {
        Log.d(TAG, "alarmManagerCancel");

        Context context = MyApplication.getContext();

        AlarmManager alarmmanager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent canselIntent = new Intent(context, AlarmService.class);
        PendingIntent pendingintent = PendingIntent.getService(
                context, ClockUtil.PendingIntentRequestCode.ALARMSERVICE, canselIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        pendingintent.cancel();
        alarmmanager.cancel(pendingintent);
    }
}
