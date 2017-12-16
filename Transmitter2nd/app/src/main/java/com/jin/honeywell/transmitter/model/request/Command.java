package com.jin.honeywell.transmitter.model.request;


import java.nio.ByteBuffer;
import java.text.DecimalFormat;

/**
 * Created by Qian Jin on 9/7/16.
 */
public class Command {

    protected int BLE_BASIC_LENGTH = 14;

    protected final static byte[] HEAD0 = new byte[] {(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff};
    protected final static byte[] HEAD1 = new byte[] {(byte) 0x82, (byte) 0x97, (byte) 0x21};

    protected byte[] mDeviceId;

    protected byte mType;

    protected byte mDataCount;


    public Command() {}

    public Command(byte[] deviceId, byte type, byte dataCount) {

        mDeviceId = deviceId;

        mType = type;

        mDataCount = dataCount;

    }

    public byte[] getBytes() {
        byte[] result = new byte[BLE_BASIC_LENGTH];

        System.arraycopy(HEAD0, 0, result, 0, HEAD0.length);
        System.arraycopy(HEAD1, 0, result, HEAD0.length, HEAD1.length);

        System.arraycopy(mDeviceId, 0, result, HEAD0.length + HEAD1.length , mDeviceId.length);
        result[HEAD0.length + HEAD1.length + mDeviceId.length] = mType;
        result[HEAD0.length + HEAD1.length + mDeviceId.length + 1] = mDataCount;
        result[HEAD0.length + HEAD1.length + mDeviceId.length + 2] = calculateCrc(result);

        return result;
    }

    protected byte calculateCrc(byte[] command) {

        int crc = command[HEAD0.length];

        for (int i = HEAD0.length + 1; i < command.length - 1; i++) {
            crc ^= command[i];
        }

        return (byte) crc;
    }

}
