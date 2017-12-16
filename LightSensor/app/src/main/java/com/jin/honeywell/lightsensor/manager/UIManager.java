package com.jin.honeywell.lightsensor.manager;


import android.graphics.Point;
import com.jin.honeywell.lightsensor.model.LightSensorModel;
import com.jin.honeywell.lightsensor.model.LightSensorStatus;
import com.jin.honeywell.lightsensor.model.SensorSharedPreference;
import com.jin.honeywell.lightsensor.util.EvaluateCoordinate;
import com.jin.honeywell.lightsensor.util.Logger.Log;
import com.jin.honeywell.lightsensor.util.MapScale;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class UIManager {

    private static final String TAG = "UIManager";

    private static UIManager mUIManager;

    private BigDecimal mLed1X;
    private BigDecimal mLed1Y;
    private BigDecimal mLed2X;
    private BigDecimal mLed2Y;
    private BigDecimal mLed3X;
    private BigDecimal mLed3Y;

    private static final int BLE_AUTO_CONNECT_POLLING_INTERVAL = 5000;

    private LocationListener mLocationListener;

    private List<LightSensorModel> mNeedAutoConnectDevices = new ArrayList<>();


    private UIManager() {}

    public static UIManager getInstance() {
        if (mUIManager == null) {
            mUIManager = new UIManager();
        }
        return mUIManager;
    }

    public interface LocationListener {
        void onUpdate(int px, int py);
    }

    public void setLocationListener(LocationListener locationListener) {
        this.mLocationListener = locationListener;
    }

    public void setLed1X(float led1X) {
        this.mLed1X = BigDecimal.valueOf(led1X);
    }

    public void setLed1Y(float led1Y) {
        this.mLed1Y = BigDecimal.valueOf(led1Y);
    }

    public void setLed2X(float led2X) {
        this.mLed2X = BigDecimal.valueOf(led2X);
    }

    public void setLed2Y(float led2Y) {
        this.mLed2Y = BigDecimal.valueOf(led2Y);
    }

    public void setLed3X(float led3X) {
        this.mLed3X = BigDecimal.valueOf(led3X);
    }

    public void setLed3Y(float led3Y) {
        this.mLed3Y = BigDecimal.valueOf(led3Y);
    }

    public void initSensor() {
        List<LightSensorModel> devices = SensorSharedPreference.getDeviceList();

        if (devices == null || devices.size() == 0) {
            LightSensorModel sensor1 = new LightSensorModel();
            LightSensorModel sensor2 = new LightSensorModel();
            LightSensorModel sensor3 = new LightSensorModel();

            sensor1.setMacAddress(LightSensorModel.SENSOR_1_ADDRESS);
            sensor2.setMacAddress(LightSensorModel.SENSOR_2_ADDRESS);
            sensor3.setMacAddress(LightSensorModel.SENSOR_3_ADDRESS);

            sensor1.setStatus(LightSensorStatus.DISCONNECT);
            sensor2.setStatus(LightSensorStatus.DISCONNECT);
            sensor3.setStatus(LightSensorStatus.DISCONNECT);

            SensorSharedPreference.addDevice(sensor1);
            SensorSharedPreference.addDevice(sensor2);
            SensorSharedPreference.addDevice(sensor3);

            SensorSharedPreference.savePositions(0, 50, 50, 0, 95, 50);
        }
    }

    public void startAutoConnectThread() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {

                    sleep(BLE_AUTO_CONNECT_POLLING_INTERVAL);

                    Log.i(TAG, "=================");

                    checkBluetooth();

                    if (hasAutoConnectDevices()) {

                        for (LightSensorModel device : mNeedAutoConnectDevices) {

                            if (LightSensorModel.isSensor1(device.getMacAddress())) {
                                Log.i(TAG, "connecting sensor1");
                            } else if (LightSensorModel.isSensor2(device.getMacAddress())) {
                                Log.i(TAG, "connecting sensor2");
                            } else if (LightSensorModel.isSensor3(device.getMacAddress())) {
                                Log.i(TAG, "connecting sensor3");
                            }

                            BLEManager.getInstance().connectBle(device.getMacAddress());
                        }

                    } else {
                        Log.i(TAG, "All sensors are connected!");

                        BLEManager.getInstance().readRSSI(LightSensorModel.SENSOR_1_ADDRESS);
                        sleep(100);
                        BLEManager.getInstance().readRSSI(LightSensorModel.SENSOR_2_ADDRESS);
                        sleep(100);
                        BLEManager.getInstance().readRSSI(LightSensorModel.SENSOR_3_ADDRESS);
                    }
                }
            }
        }).start();

    }

    private boolean hasAutoConnectDevices() {

        mNeedAutoConnectDevices.clear();

        List<LightSensorModel> devices = SensorSharedPreference.getDeviceList();

        for (LightSensorModel device : devices) {
            if (device.getStatus() == LightSensorStatus.DISCONNECT)
                mNeedAutoConnectDevices.add(device);
        }

        return mNeedAutoConnectDevices.size() > 0;
    }

    public void disconnectAllDevices() {
        for (LightSensorModel device : SensorSharedPreference.getDeviceList()) {
            SensorSharedPreference.saveStatus(device.getMacAddress(), LightSensorStatus.DISCONNECT);
        }
        BLEManager.getInstance().disconnectAllDevices();
    }

    public void calculateCoordinate(String address, int rssi) {
        int a1 = SensorSharedPreference.getA1();
        int a2 = SensorSharedPreference.getA2();
        int a3 = SensorSharedPreference.getA3();
        float n1 = SensorSharedPreference.getN1();
        float n2 = SensorSharedPreference.getN2();
        float n3 = SensorSharedPreference.getN3();
        Point point = new Point(0, 0);

        EvaluateCoordinate.setXY(mLed1X, mLed1Y, mLed2X, mLed2Y, mLed3X, mLed3Y);

        if (LightSensorModel.isSensor1(address)) {
            int distMillimeter = (int) (Math.pow(10, (Math.abs(rssi) - a1) / (10 * n1)) * 1000);
//            Log.i(TAG, "Sensor1 rssi:" + rssi + ", " + distMillimeter + "mm");
            point = EvaluateCoordinate.evaluate1((int) (distMillimeter / MapScale.METER_PIXEL_RATIO));

        } else if (LightSensorModel.isSensor2(address)) {
            int distMillimeter = (int) (Math.pow(10, (Math.abs(rssi) - a2) / (10 * n2)) * 1000);
//            Log.i(TAG, "Sensor2 rssi:" + rssi + ", " + distMillimeter + "mm");
            point = EvaluateCoordinate.evaluate2((int) (distMillimeter / MapScale.METER_PIXEL_RATIO));

        } else if (LightSensorModel.isSensor3(address)) {
            int distMillimeter = (int) (Math.pow(10, (Math.abs(rssi) - a3) / (10 * n3)) * 1000);
//            Log.i(TAG, "Sensor3 rssi:" + rssi + ", " + distMillimeter + "mm");
            point = EvaluateCoordinate.evaluate3((int) (distMillimeter / MapScale.METER_PIXEL_RATIO));
        }


        if (mLocationListener != null) {
            if (point.x != 0 && point.y != 0) {
                mLocationListener.onUpdate(point.x, point.y);
                Log.i(TAG, "Position X: " + point.x + ", Y:" + point.y);
            }
        }

    }

    private void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void checkBluetooth() {
        if (!BLEManager.getInstance().isBLEEnable()) {
            Log.i(TAG, "Phone's bluetooth is disabled!");
        }
    }

}
