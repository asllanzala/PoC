package com.jin.honeywell.transmitter.util;

import java.nio.ByteBuffer;
import java.text.DecimalFormat;

/**
 * Created by Jin on 16/08/2017.
 */

public class ByteUtil {

    public static String getFloatStringFrom4bytes(byte[] data) {
        int l = ((data[3] & 0xFF) | ((data[2] & 0xFF) << 8)
                | ((data[1] & 0xFF) << 16) | ((data[0] & 0xFF) << 24));

        return new DecimalFormat("0.00").format(Float.intBitsToFloat(l));
    }

    public static byte[] get4BytesFromFloat(float data) {
        return ByteBuffer.allocate(4).putFloat(data).array();
    }

    public static byte[] get4BytesFromInt(int data) {
        return ByteBuffer.allocate(4).putInt(data).array();
    }

    public static String getStringFromBytes(byte[] data) {

        String result = "";

        for (byte d : data) {
            if (d < 0)
                result += (char) (256 + d);
            else
                result += (char) d;
        }

        return result;
    }

    public static byte[] getBytesFromString(String data) {
        byte[] result = new byte[data.length()];

        for (int i = 0; i < data.length(); i++) {
            result[i] = (byte) data.charAt(i);
        }

        return result;
    }

}
