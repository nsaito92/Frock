package com.example.naotosaito.clocktest;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by nsaito on 2020/04/22.
 */

class AlarmSettingBaseAdapter extends BaseAdapter {

    private final static String TAG = AlarmSettingBaseAdapter.class.getSimpleName();
    private Context context;
    private List<AlarmSettingEntity> alarmSettingEntityList;

    public AlarmSettingBaseAdapter(Context context, List<AlarmSettingEntity> alarmSettingEntityList) {
        Log.d(TAG, "");
        this.context = context;
        this.alarmSettingEntityList = alarmSettingEntityList;
    }

    /**
     * Listの要素数を返す。
     * @return
     */
    @Override
    public int getCount() {
        return alarmSettingEntityList.size();
    }

    /**
     * indexやオブジェクトを返す。
     * @param position
     * @return
     */
    @Override
    public Object getItem(int position) {
        return alarmSettingEntityList.get(position);
    }

    /**
     * idを他のindexに渡す。
     * @param position
     * @return
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * リストの1行に表示するためのViewと、データを紐付ける。
     * リスト項目一つ一つに対して、呼ばれる。
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "getView : position = " + position);
        return null;
    }
}
