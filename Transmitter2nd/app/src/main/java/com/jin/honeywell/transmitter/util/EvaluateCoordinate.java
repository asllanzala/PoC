package com.jin.honeywell.transmitter.util;

import android.graphics.Point;

import com.jin.honeywell.transmitter.util.Logger.Log;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

public class EvaluateCoordinate {

    private static final String TAG = "EvaluateCoordinate";

    private static BigDecimal mX1, mY1, mX2, mY2, mX3, mY3;

    private static int mL1Avg = 0;
    private static int mL2Avg = 0;
    private static int mL3Avg = 0;
    private static int[] mL1Arr = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private static int[] mL2Arr = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private static int[] mL3Arr = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private static int mL1ArrIndex = 0;
    private static int mL2ArrIndex = 0;
    private static int mL3ArrIndex = 0;
    private static int MAX_INDEX = mL1Arr.length - 1;




    public static void setXY(BigDecimal x1, BigDecimal y1, BigDecimal x2, BigDecimal y2, BigDecimal x3, BigDecimal y3) {
        mX1 = x1;
        mY1 = y1;
        mX2 = x2;
        mY2 = y2;
        mX3 = x3;
        mY3 = y3;
    }

    /**
     * evaluate(去毛刺核心算法)
     * 对6组数据作处理，去掉最大最小值再求平均值
     * 对得到的平均值进行三点定位
     */
    public static Point evaluate1(int r1) {
        Point point = new Point(0, 0);

        mL1Arr[mL1ArrIndex] = r1;

        // 当填满6个array,计算平均值，然后计算三点定位
        if (mL1ArrIndex == MAX_INDEX) {
            mL1Avg = getAvg(mL1Arr);
            point = calculate(mX1, mY1, mX2, mY2, mX3, mY3,
                    BigDecimal.valueOf(mL1Avg), BigDecimal.valueOf(mL2Avg), BigDecimal.valueOf(mL3Avg));
        }

        // 累加index，当大于5则清零
        mL1ArrIndex++;
        if (mL1ArrIndex > MAX_INDEX)
            mL1ArrIndex = 0;

        return point;
    }

    public static Point evaluate2(int r2) {
        Point point = new Point(0, 0);

        mL2Arr[mL2ArrIndex] = r2;

        // 当填满6个array,计算平均值，然后计算三点定位
        if (mL2ArrIndex == MAX_INDEX) {
            mL2Avg = getAvg(mL2Arr);
            point = calculate(mX1, mY1, mX2, mY2, mX3, mY3,
                    BigDecimal.valueOf(mL1Avg), BigDecimal.valueOf(mL2Avg), BigDecimal.valueOf(mL3Avg));
        }

        // 累加index，当大于5则清零
        mL2ArrIndex++;
        if (mL2ArrIndex > MAX_INDEX)
            mL2ArrIndex = 0;

        return point;
    }

    public static Point evaluate3(int r3) {
        Point point = new Point(0, 0);

        mL3Arr[mL3ArrIndex] = r3;

        // 当填满6个array,计算平均值，然后计算三点定位
        if (mL3ArrIndex == MAX_INDEX) {
            mL3Avg = getAvg(mL3Arr);
            point = calculate(mX1, mY1, mX2, mY2, mX3, mY3,
                    BigDecimal.valueOf(mL1Avg), BigDecimal.valueOf(mL2Avg), BigDecimal.valueOf(mL3Avg));
        }

        // 累加index，当大于5则清零
        mL3ArrIndex++;
        if (mL3ArrIndex > MAX_INDEX)
            mL3ArrIndex = 0;

        return point;
    }


    private static int getAvg(int[] arr) {
        Arrays.sort(arr);
        return ((arr[1] + arr[2] + arr[3] + arr[4] + arr[5]  + arr[6]  + arr[7] + arr[8]) / MAX_INDEX - 1);
    }

    /**
     * calculateCoordinates(三点定位核心算法)
     * TODO 三点定位核心算法
     *
     */
    private static Point calculate(BigDecimal x1, BigDecimal y1, BigDecimal x2, BigDecimal y2,
                                 BigDecimal x3, BigDecimal y3, BigDecimal r1, BigDecimal r2, BigDecimal r3) {

        if (x1.equals(BigDecimal.valueOf(0)) || y1.equals(BigDecimal.valueOf(0))
                || x2.equals(BigDecimal.valueOf(0)) || y2.equals(BigDecimal.valueOf(0))
                || x3.equals(BigDecimal.valueOf(0)) || y3.equals(BigDecimal.valueOf(0))
                || r1.equals(BigDecimal.valueOf(0)) || r2.equals(BigDecimal.valueOf(0)) || r3.equals(BigDecimal.valueOf(0))) {
            return new Point(0, 0);
        }

        BigDecimal a2 = r1.multiply(r1);
        BigDecimal b2 = r2.multiply(r2);
        BigDecimal c2 = r3.multiply(r3);
        BigDecimal xa2 = x1.multiply(x1);
        BigDecimal xb2 = x2.multiply(x2);
        BigDecimal xc2 = x3.multiply(x3);
        BigDecimal ya2 = y1.multiply(y1);
        BigDecimal yb2 = y2.multiply(y2);
        BigDecimal yc2 = y3.multiply(y3);

        BigDecimal rbc = b2.subtract(c2);
        BigDecimal xbc = xb2.subtract(xc2);
        BigDecimal ybc = yb2.subtract(yc2);

        BigDecimal va = rbc.subtract(xbc).subtract(ybc).divide(new BigDecimal("2.0"));

        BigDecimal rba = b2.subtract(a2);
        BigDecimal xba = xb2.subtract(xa2);
        BigDecimal yba = yb2.subtract(ya2);

        BigDecimal vb = rba.subtract(xba).subtract(yba).divide(new BigDecimal("2.0"));

        BigDecimal xcb = x3.subtract(x2);
        BigDecimal xab = x1.subtract(x2);
        BigDecimal yab = y1.subtract(y2);
        BigDecimal ycb = y3.subtract(y2);

        BigDecimal va1 = va.multiply(xab);
        BigDecimal vb1 = vb.multiply(xcb);
        BigDecimal aa = yab.multiply(xcb);
        BigDecimal ab = ycb.multiply(xab);

        BigDecimal pY1 = vb1.subtract(va1);
        BigDecimal pY2 = aa.subtract(ab);
        BigDecimal y = pY1.divide(pY2, 10, RoundingMode.DOWN);

        BigDecimal x;
        if (xcb.signum() != 0) {
            x = va.subtract((y.multiply(ycb))).divide(xcb, 10, RoundingMode.DOWN);
        } else {
            x = vb.subtract(y.multiply(yab)).divide(xab, 10, RoundingMode.DOWN);
        }

        return new Point(x.intValue(), y.intValue());
    }

}
