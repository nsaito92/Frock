# Configuration #

## 全体クラス図

```
@startuml

title 全体クラス図

@startuml

class AlarmPreferenceActivity {
    AlarmPreferenceFragment mFragment
    SwitchPreference alarmbutton
    Preference btn_alarmtime_key
    Preference btn_alarm_start_week_key
    Preference btn_alarm_setting_save
    AlarmSettingEntity alarmSettingEntity
}

class FrockSettingsHelperController {
    FrockSettingsOpenHelper settingshelper
}

class FrockSettingsOpenHelper {

}

class AlarmSettingEntity {
    int mStatus
    int mHour
    int mMinute
    String mWeeks
}

AlarmPreferenceActivity - AlarmSettingEntity
AlarmPreferenceActivity --> FrockSettingsHelperController

FrockSettingsHelperController --> FrockSettingsOpenHelper

@enduml

```
