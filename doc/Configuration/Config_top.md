# Configuration #

## 画面一覧
* ホーム画面
* アラーム設定画面

## 機能一覧
* アラーム設定機能
    * 【アラーム設定生成】ユーザー入力情報からアラーム設定を生成する機能
    *  【アラーム設定永続化】アラーム設定作成機能
        * アラーム設定新規追加
            * DBへのアクセス
            * DBへの書き込み
            * UI反映
        * 編集
        * 削除
* アラーム実行機能
    * 音楽再生
    * スヌーズ機能
    * 通知機能

## 全体クラス図

```
@startuml
title クラス全体図

class MainActivity {
    private AlarmSettingBaseAdapter alarmSettingBaseAdapter
    private List<AlarmSettingEntity> alarmSettingEntityList
}
class AlarmPreferenceActivity {
    AlarmPreferenceFragment mFragment
    AlarmWeekDialogFragment alarmWeekSetting_dialog
}
class AlarmPreferenceFragment {
}

class AlarmWeekDialogFragment {
}
class CallAlarmDialogActivity {
}
class ClockUtil {
    final {static} String ALARM_SERVICE_KEY
    final {static} String ALARMTIME_HOUR_KEY
    final {static} String ALARMTIME_MINUTE_KEY
    final {static} String ALARMTIME_WEEK_KEY
    final {static} String PENDING_ALARMSERVICE_KEY
}

class MyApplication {
}

class AlarmService {
    MediaPlayer mediaPlayer
}

class MyTimerTask
class FrockSettingsOpenHelper
class FrockSettingsHelperController {
    private FrockSettingsOpenHelper settingshelper
}
class AlarmBroadcastReceiver
class AlarmManagerSetDataEntity {
    private int mId
    private Calendar mCalender
}
class AlarmServiceSetter
class AlarmSettingBaseAdapter {
    private List<AlarmSettingEntity> alarmSettingEntityList
}
class AlarmSettingEntity
class ContentResolverController
class FrockReceiver
class NotificationManagerController
class PermissionDescriptionActivity
class ValidationCheckController
class AlarmServiceObserver


' コンポーネント関係図
MainActivity - AlarmPreferenceActivity
MainActivity --> CallAlarmDialogActivity
MainActivity -- MyTimerTask
MainActivity -- AlarmSettingBaseAdapter
AlarmPreferenceActivity --> AlarmWeekDialogFragment
AlarmPreferenceActivity --> AlarmPreferenceFragment
AlarmPreferenceActivity --> PermissionDescriptionActivity
AlarmPreferenceActivity ---> ValidationCheckController
AlarmPreferenceActivity ---> NotificationManagerController
AlarmPreferenceActivity ---> ContentResolverController

' AlarmService利用処理関連
MainActivity ---> AlarmServiceSetter
FrockReceiver <---> AlarmServiceSetter
AlarmServiceObserver ---> AlarmServiceSetter
AlarmPreferenceActivity ---> AlarmServiceSetter
CallAlarmDialogActivity ---> AlarmServiceSetter
AlarmService --> CallAlarmDialogActivity
AlarmService --> NotificationManagerController

AlarmServiceSetter --> AlarmService
AlarmServiceSetter --> AlarmManagerSetDataEntity
FrockSettingsHelperController --> AlarmManagerSetDataEntity
AlarmService <-> AlarmServiceObserver

' コントローラーを使用しているクラス
MainActivity --> FrockSettingsHelperController
AlarmPreferenceActivity --> FrockSettingsHelperController
AlarmService --> FrockSettingsHelperController
AlarmServiceSetter --> FrockSettingsHelperController
AlarmServiceSetter --> NotificationManagerController
AlarmServiceObserver --> NotificationManagerController
ContentResolverController --> FrockSettingsHelperController
FrockSettingsHelperController -- FrockSettingsOpenHelper

' データクラスを使用しているクラス。
MainActivity -- AlarmSettingEntity
AlarmPreferenceActivity -- AlarmSettingEntity

@enduml

```
