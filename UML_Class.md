# クラス図

本アプリの各クラスについて記述します。

```
@startuml
title クラス図

class MainActivity {
}
class AlarmPreferenceActivity {
    AlarmPreferenceFragment mFragment
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
class FrockSettingsHelperController
class AlarmBroadcastReceiver

MainActivity --> AlarmPreferenceActivity
AlarmPreferenceActivity --> AlarmWeekDialogFragment
AlarmPreferenceActivity o- AlarmPreferenceFragment

MainActivity --> AlarmService
AlarmPreferenceActivity --> AlarmService
ClockUtil --> AlarmService
AlarmService <--> CallAlarmDialogActivity

@enduml
```