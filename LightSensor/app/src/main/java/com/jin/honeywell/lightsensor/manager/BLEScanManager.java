package com.jin.honeywell.lightsensor.manager;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;


public class BLEScanManager {

    private static BLEScanManager mBLEScanManager;

    private ScanListener mScanListener;
    private BLEManager mBleManager;

    public static BLEScanManager getInstance() {
        if (mBLEScanManager == null) {
            mBLEScanManager = new BLEScanManager();
        }
        return mBLEScanManager;
    }

    public BLEScanManager() {
        mBleManager = BLEManager.getInstance();
    }

    public interface ScanListener {
        void onScan(String macId, int rssi);
    }

    public void setScanListener(ScanListener scanListener) {
        this.mScanListener = scanListener;
    }

    public void startBleScan() {
        mBleManager.startBLEScan(mLeScanCallback);
    }

    public void stopBleScan() {
        mBleManager.stopBLEScan(mLeScanCallback);
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {

            if (mScanListener != null)
                mScanListener.onScan(device.getAddress(), rssi);
        }
    };


}
