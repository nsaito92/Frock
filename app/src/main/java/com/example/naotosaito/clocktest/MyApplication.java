package com.example.naotosaito.clocktest;

import android.app.Application;
import android.content.Context;

/**
 * Created by naotosaito on 2018/05/03.
 * Applicationクラスを継承して、起動時に呼び出されるクラスでアプリケーションで共有するオブジェクトを管理する
 */
public class MyApplication extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext(){
        return mContext;
    }
}
