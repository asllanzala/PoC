package com.jin.honeywell.lightsensor.model;


import com.google.gson.Gson;
import com.jin.honeywell.lightsensor.util.MapScale;
import com.jin.honeywell.lightsensor.util.SharedPreferenceUtil;
import com.jin.honeywell.lightsensor.util.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SensorSharedPreference {

    private static final String PREFERENCE_FILE_NAME = "light_sensor_model_sp";
    private static final String PREFERENCE_SENSOR_KEY = "light_sensor_model_key";
    private static final String PREFERENCE_A1_KEY = "a1_key";
    private static final String PREFERENCE_A2_KEY = "a2_key";
    private static final String PREFERENCE_A3_KEY = "a3_key";
    private static final String PREFERENCE_N1_KEY = "n1_key";
    private static final String PREFERENCE_N2_KEY = "n2_key";
    private static final String PREFERENCE_N3_KEY = "n3_key";

    public static int getA1() {
        return SharedPreferenceUtil.getPrefInt(PREFERENCE_FILE_NAME, PREFERENCE_A1_KEY, 59);
    }

    public static void setA1(int a) {
        SharedPreferenceUtil.setPrefInt(PREFERENCE_FILE_NAME, PREFERENCE_A1_KEY, a);
    }

    public static int getA2() {
        return SharedPreferenceUtil.getPrefInt(PREFERENCE_FILE_NAME, PREFERENCE_A2_KEY, 59);
    }

    public static void setA2(int a) {
        SharedPreferenceUtil.setPrefInt(PREFERENCE_FILE_NAME, PREFERENCE_A2_KEY, a);
    }

    public static int getA3() {
        return SharedPreferenceUtil.getPrefInt(PREFERENCE_FILE_NAME, PREFERENCE_A3_KEY, 59);
    }

    public static void setA3(int a) {
        SharedPreferenceUtil.setPrefInt(PREFERENCE_FILE_NAME, PREFERENCE_A3_KEY, a);
    }

    public static float getN1() {
        return SharedPreferenceUtil.getPrefFloat(PREFERENCE_FILE_NAME, PREFERENCE_N1_KEY, 2.7f);
    }

    public static void setN1(float n) {
        SharedPreferenceUtil.setPrefFloat(PREFERENCE_FILE_NAME, PREFERENCE_N1_KEY, n);
    }

    public static float getN2() {
        return SharedPreferenceUtil.getPrefFloat(PREFERENCE_FILE_NAME, PREFERENCE_N2_KEY, 2.7f);
    }

    public static void setN2(float n) {
        SharedPreferenceUtil.setPrefFloat(PREFERENCE_FILE_NAME, PREFERENCE_N2_KEY, n);
    }

    public static float getN3() {
        return SharedPreferenceUtil.getPrefFloat(PREFERENCE_FILE_NAME, PREFERENCE_N3_KEY, 2.7f);
    }

    public static void setN3(float n) {
        SharedPreferenceUtil.setPrefFloat(PREFERENCE_FILE_NAME, PREFERENCE_N3_KEY, n);
    }

    public static void addDevice(LightSensorModel newDevice) {

        List<LightSensorModel> devices = getDeviceList();

        if (devices.size() == 0) {
            devices.add(newDevice);

        } else {
            // do not add same device
            LightSensorModel oldDevice = getDevice(newDevice.getMacAddress(), devices);
            if (oldDevice == null)
                devices.add(newDevice);
        }

        saveDevices(devices);
    }

    public static void deleteDevice(String macAddress) {
        List<LightSensorModel> devices = getDeviceList();
        LightSensorModel device = getDevice(macAddress, devices);
        if (device != null) {
            devices.remove(device);
            saveDevices(devices);
        }
    }

    public static boolean hasDevice(String macAddress) {
        List<LightSensorModel> devices = SensorSharedPreference.getDeviceList();
        LightSensorModel device = getDevice(macAddress, devices);

        return device != null;
    }


    public static void saveStatus(String macAddress, LightSensorStatus status) {
        List<LightSensorModel> devices = getDeviceList();
        LightSensorModel device = getDevice(macAddress, devices);
        if (device != null) {
            device.setStatus(status);
            saveDevices(devices);
        }
    }

    public static void savePositions(int x1, int y1, int x2, int y2, int x3, int y3) {
        List<LightSensorModel> devices = getDeviceList();
        for (LightSensorModel device : devices) {
            if (LightSensorModel.isSensor1(device.getMacAddress())) {
                device.setPosition(new int[] {MapScale.getX(x1 / 100.00f), MapScale.getY(y1 / 100.00f)});
            } else if (LightSensorModel.isSensor2(device.getMacAddress())) {
                device.setPosition(new int[] {MapScale.getX(x2 / 100.00f), MapScale.getY(y2 / 100.00f)});
            } else if (LightSensorModel.isSensor3(device.getMacAddress())) {
                device.setPosition(new int[] {MapScale.getX(x3 / 100.00f), MapScale.getY(y3 / 100.00f)});
            }
        }
        saveDevices(devices);
    }

    public static Map<String, Integer[]> getPositions() {
        Map<String, Integer[]> positions = new HashMap<>();

        for (LightSensorModel device : getDeviceList()) {
            if (device.getPosition() != null)
                positions.put(device.getMacAddress(), new Integer[] {device.getPosition()[0], device.getPosition()[1]});
            else
                positions.put(device.getMacAddress(), new Integer[] {0, 0});
        }

        return positions;
    }

    public static List<LightSensorModel> getDeviceList() {
        JSONArray responseArray;
        List<LightSensorModel> results = new ArrayList<>();

        String record = SharedPreferenceUtil.getPrefString(PREFERENCE_FILE_NAME, PREFERENCE_SENSOR_KEY, null);

        if (StringUtil.isEmpty(record)) {
            return results;
        }

        try {
            responseArray = new JSONArray(record);

            for (int i = 0; i < responseArray.length(); i++) {
                JSONObject responseJSON = responseArray.getJSONObject(i);
                LightSensorModel model = new Gson().fromJson(responseJSON.toString(), LightSensorModel.class);
                results.add(model);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return results;
        }

        return results;
    }

    public static void clearData() {
        SharedPreferenceUtil.clearPreference(SharedPreferenceUtil
                .getSharedPreferenceInstanceByName(PREFERENCE_FILE_NAME));
    }

    public static LightSensorModel getDevice(String address) {
        List<LightSensorModel> devices = getDeviceList();

        for (LightSensorModel device : devices) {
            if (device.getMacAddress().equals(address))
                return device;
        }

        return null;
    }

    private static LightSensorModel getDevice(String address, List<LightSensorModel> devices) {

        if (devices != null) {
            for (LightSensorModel device : devices) {
                if (device.getMacAddress().equals(address))
                    return device;
            }
        }

        return null;
    }

    private static void saveDevices(List<LightSensorModel> devices) {
        SharedPreferenceUtil.setPrefString(PREFERENCE_FILE_NAME, PREFERENCE_SENSOR_KEY,
                new Gson().toJson(devices));
    }

}
