package com.example.naotosaito.clocktest;

/**
 * Created by naotosaito on 2019/08/20.
 * アラームの動作する曜日を選択できるダイアログの内容の設定、項目を選択した際の処理を行う
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;


public class AlarmWeekDialogFragment extends DialogFragment {

    private static final String TAG = "AlarmWeekDialogFragment";
    /** 選択したアイテムを格納する配列 **/
    ArrayList mSelectedWeeks = new ArrayList();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // ダイアログのタイトルの設定
        builder.setTitle(R.string.alarm_Week_setting_title)
                // ダイアログに表示される項目の設定し、項目を選択した際の、リスナーを設定
                .setMultiChoiceItems(R.array.alarm_Week_setting_menulist, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                // ユーザーがアイテムを選択した場合、アイテムを追加する
                                if (isChecked){
                                    mSelectedWeeks.add(which);
                                    Log.d(TAG, "mSelectedWeeks = " + mSelectedWeeks);
                                } else if(mSelectedWeeks.contains(which)) {
                                    // アイテムがすでに配列内にある場合は、削除する
                                    mSelectedWeeks.remove(Integer.valueOf(which));
                                }
                            }
                        })
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // ダイアログで入力された値を、文字列で呼び出し元Activityに返却する。
                                String mWeeks = ClockUtil.setSelectedWeeks(mSelectedWeeks);

                                AlarmPreferenceActivity activity = (AlarmPreferenceActivity) getActivity();
                                activity.onReceiveString(mWeeks);

                            }
                        })
                .setNegativeButton(R.string.cansel, null);

        return builder.create();
    }
}