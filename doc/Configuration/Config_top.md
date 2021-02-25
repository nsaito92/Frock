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

```puml
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

## 内部設計

### アラーム設定機能

#### シーケンス

```puml

@startuml

title アラーム設定 シーケンス

' asを使って、「actor」から、分類子の名前を変更することも出来る。
actor       actor

activate actor
actor -> MainActivity: アプリ起動
activate MainActivity
MainActivity -> MainActivity:onCreate()

actor -> MainActivity:「アラーム設定」画面ボタンをタップ
MainActivity -> AlarmPreferenceActivity:startActivity()
activate AlarmPreferenceActivity
AlarmPreferenceActivity -> AlarmPreferenceActivity:onCreate()

'<-, ->	同期メッセージ	戻りを待つメッセージ。通常のメッセージです。
'<--, -->	戻りメッセージ	メッセージの送り先からの戻り値
'<<-, ->>	非同期メッセージ	同期されないメッセージ

alt アラーム設定新規作成
    AlarmPreferenceActivity -> AlarmPreferenceActivity:AlarmSettingEntityオブジェクト新規作成
else 既存アラーム設定編集
    AlarmPreferenceActivity -> FrockSettingsHelperController:getAlarmSettingEntity()
    activate FrockSettingsHelperController
    FrockSettingsHelperController -> DB:アラーム設定取得
    FrockSettingsHelperController <-- DB:1レコード分のデータを返却。
    deactivate FrockSettingsHelperController
    AlarmPreferenceActivity <-- FrockSettingsHelperController:取得したデータでAlarmSettingEntityインスタンスを返却
    note left:インスタンスのプロパティを取得して、ビューに反映。
end

actor -> AlarmPreferenceActivity:各種設定を入力後「保存」をタップ。

AlarmPreferenceActivity -> FrockSettingsHelperController:updateData()
activate FrockSettingsHelperController
FrockSettingsHelperController -> DB:DBに書き込み。
deactivate FrockSettingsHelperController
actor <-- AlarmPreferenceActivity:DBに保存を行ったことをトーストで通知
MainActivity <- AlarmPreferenceActivity:finish()

AlarmPreferenceActivity --> AlarmServiceSetter:DBに保存したデータを元に、アラーム予定を設定
activate AlarmServiceSetter
AlarmServiceSetter -> FrockSettingsHelperController:getClosestCalender()
activate FrockSettingsHelperController
AlarmServiceSetter <-- FrockSettingsHelperController:AlarmManagerSetDataEntityインスタンスを返却
deactivate FrockSettingsHelperController
AlarmServiceSetter -> AlarmManager:アラーム起動予定をset
deactivate AlarmServiceSetter
@enduml

```

### アラーム実行機能

#### シーケンス

```puml

```
