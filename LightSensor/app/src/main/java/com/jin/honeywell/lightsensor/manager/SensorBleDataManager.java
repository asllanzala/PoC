package com.jin.honeywell.lightsensor.manager;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.jin.honeywell.lightsensor.model.LightSensorStatus;
import com.jin.honeywell.lightsensor.model.SensorSharedPreference;
import com.jin.honeywell.lightsensor.service.BluetoothLeService;


public class SensorBleDataManager {

    private static SensorBleDataManager mSensorBleDataManager;

    private DeviceStatusListener mDeviceStatusListener;

    private static final String TAG = "SensorBleDataManager";


    private SensorBleDataManager() {
    }

    public static SensorBleDataManager getInstance() {
        if (mSensorBleDataManager == null) {
            mSensorBleDataManager = new SensorBleDataManager();
        }
        return mSensorBleDataManager;
    }

    public interface DeviceStatusListener {
        void onChange(String macAddress, LightSensorStatus status);
    }

    public void setDeviceStatusListener(DeviceStatusListener deviceStatusListener) {
        mDeviceStatusListener = deviceStatusListener;
    }

    public void registerBleReceiver() {
        AppManager.getInstance().getApplication().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    public void unregisterBleReceiver() {
        AppManager.getInstance().getApplication().unregisterReceiver(mGattUpdateReceiver);
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {

            final String action = intent.getAction();
            final String address = intent.getStringExtra(BluetoothLeService.DEVICE_ADDRESS);

            if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {

                SensorSharedPreference.saveStatus(address, LightSensorStatus.CONNECT);

                if (mDeviceStatusListener != null)
                    mDeviceStatusListener.onChange(address, LightSensorStatus.CONNECT);

            } else if (BluetoothLeService.ACTION_DEVICE_PAIRED.equals(action)) {

            } else if (BluetoothLeService.ACTION_DESCRIPTOR_WRITE.equals(action)) {

            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {

            } else if (BluetoothLeService.ACTION_DATA_READ.equals(action)) {

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {

                SensorSharedPreference.saveStatus(address, LightSensorStatus.DISCONNECT);

                if (mDeviceStatusListener != null)
                    mDeviceStatusListener.onChange(address, LightSensorStatus.DISCONNECT);

            } else if (BluetoothLeService.ACTION_DATA_WRITE.equals(action)) {

            } else if (BluetoothLeService.ACTION_DEVICE_UNPAIR.equals(action)) {

            } else if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {

            } else if (BluetoothLeService.ACTION_READ_RSSI.equals(action)) {
                int rssi = intent.getIntExtra(BluetoothLeService.RSSI_VALUE, 0);
                UIManager.getInstance().calculateCoordinate(address, rssi);
            }

        }
    };

    private IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_DEVICE_PAIRED);
        intentFilter.addAction(BluetoothLeService.ACTION_DEVICE_UNPAIR);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_WRITE);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_READ);
        intentFilter.addAction(BluetoothLeService.ACTION_DESCRIPTOR_WRITE);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.ACTION_READ_RSSI);
        return intentFilter;
    }


}