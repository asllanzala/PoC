package com.jin.honeywell.transmitter.model;

/**
 * Created by Jin on 18/08/2017.
 */

public class Diagnose {

    private static final int BIT0 = 1;
    private static final int BIT1 = 1 << 1;
    private static final int BIT2 = 1 << 2;
    private static final int BIT3 = 1 << 3;
    private static final int BIT4 = 1 << 4;
    private static final int BIT5 = 1 << 5;
    private static final int BIT6 = 1 << 6;
    private static final int BIT7 = 1 << 7;


    public static String parseMessage(byte[] response) {
        String message = "";

        if (response == null || response.length != 3)
            return "";

        if (response[0] == 0 && response[1] == 0 && response[2] == 0)
            message += "一切正常\n";

        if ((response[0] & BIT0) > 0) {
            message += "通信模块电流环输出故障\n";
        }

        if ((response[0] & BIT1) > 0) {
            message += "表体非易失存储器错乱\n";
        }

        if ((response[0] & BIT2) > 0) {
            message += "配置数据错乱\n";
        }

        if ((response[0] & BIT3) > 0) {
            message += "通信模块故障\n";
        }

        if ((response[0] & BIT4) > 0) {
            message += "表体故障\n";
        }

        if ((response[0] & BIT5) > 0) {
            message += "传感器模块通信超时\n";
        }

        if ((response[1] & BIT0) > 0) {
            message += "显示模块故障\n";
        }

        if ((response[1] & BIT1) > 0) {
            message += "通信模块通信故障\n";
        }

        if ((response[1] & BIT2) > 0) {
            message += "Meter Body Excess Correct\n";
        }

        if ((response[1] & BIT3) > 0) {
            message += "传感器温度超限\n";
        }

        if ((response[1] & BIT4) > 0) {
            message += "固定电流模式\n";
        }

        if ((response[1] & BIT5) > 0) {
            message += "主变量超限\n";
        }

        if ((response[1] & BIT6) > 0) {
            message += "工厂标定数据缺失\n";
        }

        if ((response[1] & BIT7) > 0) {
            message += "电流环输出模块补偿数据缺失\n";
        }

        if ((response[2] & BIT0) > 0) {
            message += "下限设置错误\n";
        }

        if ((response[2] & BIT1) > 0) {
            message += "上限设置错误\n";
        }

        if ((response[2] & BIT2) > 0) {
            message += "模拟输出超限\n";
        }

        if ((response[2] & BIT3) > 0) {
            message += "电流环噪音过大\n";
        }

        if ((response[2] & BIT4) > 0) {
            message += "Meter Body Unreliable Comm\n";
        }

        if ((response[2] & BIT5) > 0) {
            message += "温度报警\n";
        }

        if ((response[2] & BIT6) > 0) {
            message += "电流环输出模块标定数据缺失\n";
        }

        if ((response[2] & BIT7) > 0) {
            message += "供电电压偏低\n";
        }

        return message;
    }


}
