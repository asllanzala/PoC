package com.jin.honeywell.lightsensor;

import android.app.Application;

import com.jin.honeywell.lightsensor.manager.AppManager;


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
