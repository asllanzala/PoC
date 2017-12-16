package com.jin.honeywell.transmitter.util;

/**
 * Created by e573227 on 11/07/2017.
 */

public class MapScale {

    public static final float MAP_HEIGHT = 9900;
    public static final float MAP_WIDTH = 7800;
    public static final float METER_PIXEL_RATIO = MAP_WIDTH / DensityUtil.getScreenWidth();
    private static int mMapWidth;
    private static int mMapHeight;

    public static int getX(float portion) {
        return (int) (mMapWidth * portion);
    }

    public static int getY(float portion) {
        return (int) (mMapHeight * portion);
    }

    public static int getPortionX(int x) {
        return x * 100 / mMapWidth;
    }

    public static int getPortionY(int y) {
        return y * 100 / mMapHeight;
    }

    public static void setMapWidth(int mapWidth) {
        MapScale.mMapWidth = mapWidth;
    }

    public static void seMapHeight(int mapHeight) {
        MapScale.mMapHeight = mapHeight;
    }

}
