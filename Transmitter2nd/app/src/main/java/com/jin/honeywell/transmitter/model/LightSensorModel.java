package com.jin.honeywell.transmitter.model;



public class LightSensorModel {

    public static final byte READ_INFO = 0x00;
    public static final byte READ_PV = 0x01;
    public static final byte READ_LIMIT = 0x0f;
    public static final byte SET_ZERO = 0x2b;
    public static final byte READ_TAG = 0x0d;
    public static final byte READ_SERIAL = 0x0e;
    public static final byte START_CIRCUIT = 0x28;
    public static final byte CHANGE_UNIT = 0x2c;
    public static final byte CHANGE_LIMIT = 0x23;
    public static final byte CHANGE_TAG = 0x12;
    public static final byte READ_DIY = (byte) 0xff;
    public static final byte READ_DIAGNOSE0 = (byte) 0x30;
    public static final byte READ_DIAGNOSE1 = (byte) 0x7a;
    public static final byte READ_DIAGNOSE2 = (byte) 0xfe;


    private LightSensorStatus mStatus;

    private String mMacAddress;

    public String getMacAddress() {
        return mMacAddress;
    }

    public void setMacAddress(String mMacAddress) {
        this.mMacAddress = mMacAddress;
    }

    public LightSensorStatus getStatus() {
        return mStatus;
    }

    public void setStatus(LightSensorStatus mStatus) {
        this.mStatus = mStatus;
    }

}
