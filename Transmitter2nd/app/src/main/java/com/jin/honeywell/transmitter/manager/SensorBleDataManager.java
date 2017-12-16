package com.jin.honeywell.transmitter.manager;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.jin.honeywell.transmitter.model.BleUuidKey;
import com.jin.honeywell.transmitter.model.LightSensorModel;
import com.jin.honeywell.transmitter.model.LightSensorStatus;
import com.jin.honeywell.transmitter.model.SensorSharedPreference;
import com.jin.honeywell.transmitter.model.SensorUnit;
import com.jin.honeywell.transmitter.model.request.RequestCommand;
import com.jin.honeywell.transmitter.model.request.Command;
import com.jin.honeywell.transmitter.service.BluetoothLeService;
import com.jin.honeywell.transmitter.util.ByteUtil;


public class SensorBleDataManager {

    private static SensorBleDataManager mSensorBleDataManager;

    private DataListener mDataListener;
    private StatusListener mStatusListener;
    private static BLEManager mBleManager;

    private byte[] mDeviceId = new byte[3];

    private static final String TAG = "SensorBleDataManager";


    private SensorBleDataManager() {
        mBleManager = BLEManager.getInstance();
    }

    public static SensorBleDataManager getInstance() {
        if (mSensorBleDataManager == null) {
            mSensorBleDataManager = new SensorBleDataManager();
        }
        return mSensorBleDataManager;
    }

    public interface StatusListener {
        void onStatusChange(String macAddress, LightSensorStatus status);
    }


    public interface DataListener {

        void onPv(String macAddress, String pv, int unit);

        void onLimit(String macAddress, String max, String min, int unit);

        void onDiy(String macAddress, String max, String min);

        void onTag(String macAddress, String tag);

        void onSuccess(String macAddress);

        void onSerial(String macAddress, String serial);

        void onUnit(String macAddress);

        void onDiagnose0(String macAddress, byte[] response);
        void onDiagnose1(String macAddress, byte[] response);
        void onDiagnose2(String macAddress, byte[] response);

    }

    public void setDeviceDataListener(DataListener dataListener) {
        mDataListener = dataListener;
    }

    public void setDeviceStatusListener(StatusListener statusListener) {
        mStatusListener = statusListener;
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
            final byte[] data = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);

            if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {

                SensorSharedPreference.saveStatus(address, LightSensorStatus.CONNECT);

                if (mStatusListener != null)
                    mStatusListener.onStatusChange(address, LightSensorStatus.CONNECT);

                // Step 1
                setNotification(address);

            } else if (BluetoothLeService.ACTION_DEVICE_PAIRED.equals(action)) {

            } else if (BluetoothLeService.ACTION_DESCRIPTOR_WRITE.equals(action)) {

                // Step 2
                mBleManager.requestMtu(address, 128);

            } else if (BluetoothLeService.ACTION_REQEUST_MTU.equals(action)) {

                // Step 3
                sendCommand(address, LightSensorModel.READ_INFO, null, (byte) 0);

            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {

                // Step 4
                try {
                    parseResponse(address, data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (BluetoothLeService.ACTION_DATA_READ.equals(action)) {

            } else if (BluetoothLeService.ACTION_DATA_WRITE.equals(action)) {

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {

                SensorSharedPreference.saveStatus(address, LightSensorStatus.DISCONNECT);

                if (mStatusListener != null)
                    mStatusListener.onStatusChange(address, LightSensorStatus.DISCONNECT);

            } else if (BluetoothLeService.ACTION_DEVICE_UNPAIR.equals(action)) {

            } else if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {

            } else if (BluetoothLeService.ACTION_READ_RSSI.equals(action)) {

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
        intentFilter.addAction(BluetoothLeService.ACTION_REQEUST_MTU);
        return intentFilter;
    }


    /**
     * Characteristic Notification
     */
    private void setNotification(String address) {
        mBleManager.setNotification(address, BleUuidKey.TRANSMITTER_SERVICE,
                BleUuidKey.TRANSMITTER_DATA_CHARACTER, BleUuidKey.CLIENT_CHARACTERISTIC_CONFIG, true);
    }

    public void sendCommand(String address, byte type, byte[] request, byte count) {
        switch (type) {
            case LightSensorModel.READ_INFO:
                byte[] value = new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
                        (byte) 0x02, (byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x82};
                mBleManager.writeCharacteristic(address, value,
                        BleUuidKey.TRANSMITTER_SERVICE, BleUuidKey.TRANSMITTER_DATA_CHARACTER);
                break;

            case LightSensorModel.START_CIRCUIT:
            case LightSensorModel.CHANGE_LIMIT:
            case LightSensorModel.CHANGE_UNIT:
            case LightSensorModel.CHANGE_TAG:
                RequestCommand requestCommand = new RequestCommand(mDeviceId, type, request, count);
                mBleManager.writeCharacteristic(address, requestCommand.getBytes(),
                        BleUuidKey.TRANSMITTER_SERVICE, BleUuidKey.TRANSMITTER_DATA_CHARACTER);
                break;

            case LightSensorModel.READ_DIY:
                break;

            default:
                Command command = new Command(mDeviceId, type, count);
                mBleManager.writeCharacteristic(address, command.getBytes(),
                        BleUuidKey.TRANSMITTER_SERVICE, BleUuidKey.TRANSMITTER_DATA_CHARACTER);
                break;
        }

    }

    public void parseResponse(String address, byte[] data) {

        for (int i = 0; i < data.length; i++) {
            if (data[i] == -1)
                continue;

            byte[] response = new byte[data.length - i];
            System.arraycopy(data, i, response, 0, data.length - i);

            if (response.length == 29 && response[14] == 0) {

                // Step 5
                mDeviceId[0] = response[15];
                mDeviceId[1] = response[16];
                mDeviceId[2] = response[17];

                sendCommand(address, LightSensorModel.READ_SERIAL, null, (byte) 0);

            } else {
                switch (response[6]) {
                    case LightSensorModel.READ_PV:
                        String pv = ByteUtil.getFloatStringFrom4bytes(new byte[]{response[11], response[12], response[13], response[14]});
                        mDataListener.onPv(address, pv, SensorUnit.getUnitFromByte(response[10]));
                        break;

                    case LightSensorModel.READ_LIMIT:
                        int unit = SensorUnit.getUnitFromByte(response[12]);
                        String max = ByteUtil.getFloatStringFrom4bytes(new byte[]{response[13], response[14], response[15], response[16]});
                        String min = ByteUtil.getFloatStringFrom4bytes(new byte[]{response[17], response[18], response[19], response[20]});
                        mDataListener.onLimit(address, max, min, unit);
                        break;

                    case LightSensorModel.READ_DIY:
                        String diyMax = ByteUtil.getFloatStringFrom4bytes(new byte[]{response[20], response[21], response[22], response[23]});
                        String diyMin = ByteUtil.getFloatStringFrom4bytes(new byte[]{response[16], response[17], response[18], response[19]});
                        mDataListener.onDiy(address, diyMax, diyMin);
                        break;

                    case LightSensorModel.READ_TAG:
                        String tag = ByteUtil.getStringFromBytes(new byte[]{response[10], response[11], response[12],
                                response[13], response[14], response[15]});
                        mDataListener.onTag(address, tag);
                        break;

                    case LightSensorModel.READ_SERIAL:
                        String serial = String.valueOf(response[10]) + String.valueOf(response[11]) + String.valueOf(response[12]);
                        mDataListener.onSerial(address, serial);
                        break;

                    case LightSensorModel.CHANGE_UNIT:
                        mDataListener.onUnit(address);
                        break;

                    case LightSensorModel.READ_DIAGNOSE0:
                        mDataListener.onDiagnose0(address, new byte[]{response[10], response[11], response[12]});
                        break;

                    case LightSensorModel.READ_DIAGNOSE1:
                        mDataListener.onDiagnose1(address, response);
                        break;

                    case LightSensorModel.READ_DIAGNOSE2:
                        mDataListener.onDiagnose2(address, response);
                        break;

                    case LightSensorModel.CHANGE_TAG:
                    case LightSensorModel.CHANGE_LIMIT:
                    case LightSensorModel.SET_ZERO:
                    case LightSensorModel.START_CIRCUIT:
                        mDataListener.onSuccess(address);
                        break;

                }
            }

            break;
        }


    }

}