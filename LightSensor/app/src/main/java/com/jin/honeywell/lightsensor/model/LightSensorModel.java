package com.jin.honeywell.lightsensor.model;



public class LightSensorModel {

//    public static final String SENSOR_1_ADDRESS = "0C:61:CF:8D:95:62";
//    public static final String SENSOR_2_ADDRESS = "0C:61:CF:8D:95:7D";
//    public static final String SENSOR_3_ADDRESS = "0C:61:CF:8D:9C:9C";

    public static final String SENSOR_1_ADDRESS = "A0:E6:F8:B3:32:08";
    public static final String SENSOR_2_ADDRESS = "A0:E6:F8:B3:30:B3";
    public static final String SENSOR_3_ADDRESS = "A0:E6:F8:B3:30:F0";

    private LightSensorStatus mStatus;

    private String mMacAddress;

    private int[] mPosition;

    public static boolean isSensor1(String address) {
        return SENSOR_1_ADDRESS.equals(address);
    }

    public static boolean isSensor2(String address) {
        return SENSOR_2_ADDRESS.equals(address);
    }

    public static boolean isSensor3(String address) {
        return SENSOR_3_ADDRESS.equals(address);
    }

    public String getMacAddress() {
        return mMacAddress;
    }

    public void setMacAddress(String mMacAddress) {
        this.mMacAddress = mMacAddress;
    }

    public int[] getPosition() {
        return mPosition;
    }

    public void setPosition(int[] position) {
        this.mPosition = position;
    }

    public LightSensorStatus getStatus() {
        return mStatus;
    }

    public void setStatus(LightSensorStatus mStatus) {
        this.mStatus = mStatus;
    }

}
