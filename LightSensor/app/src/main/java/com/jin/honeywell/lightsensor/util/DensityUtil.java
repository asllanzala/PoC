package com.jin.honeywell.lightsensor.util;

import android.content.Context;
import android.view.WindowManager;

import com.jin.honeywell.lightsensor.manager.AppManager;


public class DensityUtil {

    /**
     * convert dp to px according to screen resolution
     */
    public static int dip2px(float dpValue) {
        final float scale = AppManager.getInstance().getApplication().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * convert px to dp according to screen resolution
     */
    public static int px2dip(float pxValue) {
        final float scale =  AppManager.getInstance().getApplication().getResources()
                .getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int getScreenWidth() {
        WindowManager wm = (WindowManager) AppManager.getInstance().getApplication().getSystemService(Context.WINDOW_SERVICE);
        @SuppressWarnings("deprecation")
        int width = wm.getDefaultDisplay().getWidth();// 屏幕宽度
        return width;
    }

    public static int getScreenHeight() {
        WindowManager wm = (WindowManager)  AppManager.getInstance().getApplication().getSystemService(Context.WINDOW_SERVICE);
        @SuppressWarnings("deprecation")
        int height = wm.getDefaultDisplay().getHeight();// 屏幕高度
        return height;
    }

    public static int px2sp(float pxValue) {
        final float fontScale =  AppManager.getInstance().getApplication().getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * sp to px
     *
     * @param spValue
     * @return
     */
    public static int sp2px(float spValue) {
        final float fontScale =  AppManager.getInstance().getApplication().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

}
