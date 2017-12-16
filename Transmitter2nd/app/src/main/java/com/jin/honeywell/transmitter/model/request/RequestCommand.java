package com.jin.honeywell.transmitter.model.request;



/**
 * Created by Jin on 9/7/16.
 */
public class RequestCommand extends Command {

    private byte[] mRequest;

    public RequestCommand(byte[] deviceId, byte type, byte[] request, byte dataCount) {
        mDeviceId = deviceId;
        mType = type;
        mRequest = request;
        mDataCount = dataCount;
    }

    public byte[] getBytes() {
        byte[] result = new byte[mDataCount + BLE_BASIC_LENGTH];
        for (int i = 0; i < result.length; i++)
            result[i] = -1;

        System.arraycopy(HEAD0, 0, result, 0, HEAD0.length);
        System.arraycopy(HEAD1, 0, result, HEAD0.length, HEAD1.length);

        System.arraycopy(mDeviceId, 0, result, HEAD0.length + HEAD1.length , mDeviceId.length);
        result[HEAD0.length + HEAD1.length + mDeviceId.length] = mType;
        result[HEAD0.length + HEAD1.length + mDeviceId.length + 1] = mDataCount;
        System.arraycopy(mRequest, 0, result, HEAD0.length + HEAD1.length + mDeviceId.length + 2, mRequest.length);

        result[mDataCount + BLE_BASIC_LENGTH - 1] = calculateCrc(result);

        return result;
    }

}
