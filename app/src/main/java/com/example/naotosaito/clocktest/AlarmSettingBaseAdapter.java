package com.example.naotosaito.clocktest;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * DBのアラーム設定をListViewで表示するためのAdapter
 * Created by nsaito on 2020/04/22.
 */

class AlarmSettingBaseAdapter extends BaseAdapter {

    private final static String TAG = AlarmSettingBaseAdapter.class.getSimpleName();
    private Context context;
    private List<AlarmSettingEntity> alarmSettingEntityList;
    private AlarmSettingEntity alarmSettingEntity;

    /** 毎回findViewByIdをせずに高速化が出来る様にするためのholderクラス。 */
    private class ViewHolder {
        TextView text_on_off;
        TextView text_time;
        TextView text_week;
    }


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

        View view = convertView;
        ViewHolder holder;

        // データ取得
        alarmSettingEntity = alarmSettingEntityList.get(position);

        if (view == null) {
            // ViewとListItem用xmlをInflate
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.alarmsettingentity_list_item, parent, false);

            // Inflateしたxmlの各TextViewを取得。
            TextView text_on_off = (TextView) view.findViewById(R.id.text_on_off);
            TextView text_time = (TextView) view.findViewById(R.id.text_time);
            TextView text_week = (TextView) view.findViewById(R.id.text_week);

            // Holderにviewを持たせておく。
            holder = new ViewHolder();
            holder.text_on_off = text_on_off;
            holder.text_time = text_time;
            holder.text_week = text_week;
            view.setTag(holder);
        } else {
            //初回表示時にsetしたtagを元にviewを取得する。
            holder = (ViewHolder) view.getTag();
        }

        // 取得した各データを、View表示用に整形をして、TextViewにセット
        if (alarmSettingEntity.getmStatus() == 1) {
            holder.text_on_off.setText(R.string.on);
        } else {
            holder.text_on_off.setText(R.string.off);
        }
        holder.text_time.setText(
                ClockUtil.shapingStringTime(
                        String.valueOf(alarmSettingEntity.getmHour()),
                        String.valueOf(alarmSettingEntity.getmMinute())
                )
        );

        holder.text_week.setText(ClockUtil.convertStringToWeek(alarmSettingEntity.getmWeek()));

        return view;
    }
}
