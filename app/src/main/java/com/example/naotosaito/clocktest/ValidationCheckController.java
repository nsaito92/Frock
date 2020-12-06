package com.example.naotosaito.clocktest;

import android.util.Log;

/**
 * バリデーションチェックを行う処理のコントローラークラス。
 * Created by nsaito on 2020/12/06.
 */

class ValidationCheckController {
    private final static String TAG = ValidationCheckController.class.getSimpleName();

    public ValidationCheckController() {

    }

    /**
     * AlarmSettingEntityのバリデーションチェックを行う。
     * @param entity
     */

    public static boolean checkEntityWeeks(AlarmSettingEntity entity) {
        Log.d(TAG, "checkEntityWeeks");

        boolean result = false;

        if (entity != null) {
            // 曜日は何も入力されない場合があるため、チェック。
            if (entity.getmWeek() != null) {
                result = true;
            }
        }

        return result;
    }
}
