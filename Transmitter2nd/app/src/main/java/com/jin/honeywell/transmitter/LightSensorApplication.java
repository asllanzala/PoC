package com.jin.honeywell.transmitter;

import android.app.Application;

import com.jin.honeywell.transmitter.manager.AppManager;


public class LightSensorApplication extends Application {
    /** Instance of the current application. */
    private static LightSensorApplication mLightSensorApplication = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mLightSensorApplication = this;
        AppManager.getInstance().setApplication(mLightSensorApplication);
    }


}
