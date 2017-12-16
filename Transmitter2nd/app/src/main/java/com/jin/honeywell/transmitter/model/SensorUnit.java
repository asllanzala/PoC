package com.jin.honeywell.transmitter.model;

/**
 * Created by Jin on 10/08/2017.
 */

public class SensorUnit {

    public static int getUnitFromByte(byte unit) {
        switch (unit) {
            case 1:
                return 0;
            case 2:
                return 1;
            case 3:
                return 2;
            case 4:
                return 3;
            case 5:
                return 4;
            case 6:
                return 5;
            case 7:
                return 6;
            case 8:
                return 7;
            case 9:
                return 8;
            case 10:
                return 9;
            case 11:
                return 10;
            case 12:
                return 11;
            case 13:
                return 12;
            case 14:
                return 13;
            case (byte) 237:
                return 14;
            case (byte) 238:
                return 15;
            case (byte) 239:
                return 16;
            case (byte) 145:
                return 17;
            case 32:
                return 18;
            case 33:
                return 19;
            case 34:
                return 20;
            case 35:
                return 21;
            case 16:
                return 22;
            case (byte) 136:
                return 23;
            case 17:
                return 24;
            case (byte) 138:
                return 25;
            case 57:
                return 26;
            case (byte) 170:
                return 27;
            case (byte) 171:
                return 28;

            default:
                return 0;
        }

    }

    public static byte getUnitFromInt(int unit) {
        switch (unit) {
            case 0:
                return 1;
            case 1:
                return 2;
            case 2:
                return 3;
            case 3:
                return 4;
            case 4:
                return 5;
            case 5:
                return 6;
            case 6:
                return 7;
            case 7:
                return 8;
            case 8:
                return 9;
            case 9:
                return 10;
            case 10:
                return 11;
            case 11:
                return 12;
            case 12:
                return 13;
            case 13:
                return 14;
            case 14:
                return (byte) 237;
            case 15:
                return (byte) 238;
            case 16:
                return (byte) 239;
            case 17:
                return (byte) 145;
            case 18:
                return 32;
            case 19:
                return 33;
            case 20:
                return 34;
            case 21:
                return 35;
            case 22:
                return 16;
            case 23:
                return (byte) 136;
            case 24:
                return 17;
            case 25:
                return (byte) 138;
            case 26:
                return 57;
            case 27:
                return (byte) 170;
            case 28:
                return (byte) 171;

            default:
                return 0;
        }

    }

}
