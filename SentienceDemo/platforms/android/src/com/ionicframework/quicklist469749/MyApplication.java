package com.ionicframework.quicklist469749;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

/**
 * Created by e573227 on 01/04/2017.
 */

public class MyApplication extends MultiDexApplication {

    private static Context mContext;

    public MyApplication() {
        mContext = MyApplication.this;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);

    }

    public static Context getInstance() {
        return mContext;
    }

}
