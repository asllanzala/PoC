package com.ionicframework.quicklist469749.Picture;

import android.os.Environment;
import android.os.Environment;


public class Path {
    public static final String SAVE_DIR = Environment.getExternalStorageDirectory().getPath() + "/Sentience/";
    public static final String IMAGE_NAME = "photo.jpg";
    public static final String TEMP_IMAGE_NAME = "temp1.jpg";
    public static final String DOMAIN_PATH = "android.resource://";
    public static final String STORAGE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "SentienceBoard/";

    public static final String DETECT_PATH = SAVE_DIR;
    public static final String  RECEIVE_PATH = SAVE_DIR + "Receive/";
    public static final String TRUST_PATH = SAVE_DIR + "Trust/";

    public static final String NOTIFY_ACTION_LIST ="android.intent.action.MyBroadcastReceiver";
    public static final String SERVER_SETTING_ATTRIBUTE="android.intent.action.ServerSetting";
    public static final String SERVER_SETTING="serverSetting";
}

