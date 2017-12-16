package com.jin.honeywell.transmitter.model;

/**
 * Created by Jin on 07/08/2017.
 */

public class BleUuidKey {

    // service uuid for send and read data
    public static final String TRANSMITTER_SERVICE = "f000c0e0-0451-4000-b000-000000000000";
    public static final String TRANSMITTER_DATA_CHARACTER = "f000c0e1-0451-4000-b000-000000000000";
    public static final String TRANSMITTER_DEVICE_CHARACTER = "f000c0e2-0451-4000-b000-000000000000";
    public static final String TRANSMITTER_CONFIG_CHARACTER = "f000c0e3-0451-4000-b000-000000000000";
    public static final String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    public static final String DEVICE_INFO = "0000180a-0000-1000-8000-00805f9b34fb";
    public static final String MANUFACTURER = "00002a29-0000-1000-8000-00805f9b34fb";
    public static final String MODULE_NUMBER = "00002a24-0000-1000-8000-00805f9b34fb";
    public static final String SERIAL_NUMBER = "00002a25-0000-1000-8000-00805f9b34fb";
    public static final String FIRMWARE_NUMBER = "00002a26-0000-1000-8000-00805f9b34fb";
    public static final String HARDWARE_NUMBER = "00002a27-0000-1000-8000-00805f9b34fb";
    public static final String SOFTWARE_NUMBER = "00002a28-0000-1000-8000-00805f9b34fb";

}