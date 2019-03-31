package com.*.base.common.util;

import org.springframework.stereotype.Service;

/**
 * gps纠偏算法，适用于google,高德体系的地图
 */
@Service
public class GpsUtil {

    private final static double a = 6378245.0;
    private final static double pi = 3.14159265358979324;
    private final static double ee = 0.00669342162296594323;

    /**
     * 计算地球上任意两点(经纬度)距离
     *
     * @param long1 第一点经度
     * @param lat1  第一点纬度
     * @param long2 第二点经度
     * @param lat2  第二点纬度
     * @return 返回距离 单位：米
     */
    public static double distance(double long1, double lat1, double long2, double lat2) {
        double a, b, R;
        R = 6378137; // 地球半径
        lat1 = lat1 * Math.PI / 180.0;
        lat2 = lat2 * Math.PI / 180.0;
        a = lat1 - lat2;
        b = (long1 - long2) * Math.PI / 180.0;
        double d;
        double sa2, sb2;
        sa2 = Math.sin(a / 2.0);
        sb2 = Math.sin(b / 2.0);
        d = 2 * R * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1) * Math.cos(lat2) * sb2 * sb2));
        return d;
    }

    /**
     * WGS-84 to GCJ-02
     */
    public static double[] toGCJ02Point(double latitude,double longitude) {
        double[] dev = calDev(latitude, longitude);
        double retLat = latitude + dev[0];
        double retLon = longitude + dev[1];
        return new double[]{retLat, retLon};
    }

    /**
     * GCJ-02 to WGS-84
     */
    public static double[] toWGS84Point(double latitude, double longitude) {
        double[] dev = calDev(latitude, longitude);
        double retLat = latitude - dev[0];
        double retLon = longitude - dev[1];
        dev = calDev(retLat, retLon);
        retLat = latitude - dev[0];
        retLon = longitude - dev[1];
        return new double[]{retLat, retLon};
    }

    private static double[] calDev(double wgLat, double wgLon) {
        if (isOutOfChina(wgLat, wgLon)) {
            return new double[]{0, 0};
        }
        double dLat = calLat(wgLon - 105.0, wgLat - 35.0);
        double dLon = calLon(wgLon - 105.0, wgLat - 35.0);
        double radLat = wgLat / 180.0 * pi;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
        return new double[]{dLat, dLon};
    }

    private static boolean isOutOfChina(double lat, double lon) {
        if (lon < 72.004 || lon > 137.8347)
            return true;
        if (lat < 0.8293 || lat > 55.8271)
            return true;
        return false;
    }

    private static double calLat(double x, double y) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y / 3.0 * pi)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    private static double calLon(double x, double y) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(x / 30.0 * pi)) * 2.0 / 3.0;
        return ret;
    }

//-----------------------------------------------------------------------------------------------------------------------
    // 计算角度，已弃用
    public static double getAngle(double firstLon,double firstLat,double nextLon,double nextLat){
        double dRotateAngle = Math.atan2(Math.abs(firstLon - nextLon), Math.abs(firstLat - nextLat));
        if (nextLon >= firstLon) {
            if (nextLat >= firstLat) {
            } else {
                dRotateAngle = Math.PI - dRotateAngle;
            }
        } else {
            if (nextLat >= firstLat) {
                dRotateAngle = 2 * Math.PI - dRotateAngle;
            } else {
                dRotateAngle = Math.PI + dRotateAngle;
            }
        }
        dRotateAngle = dRotateAngle * 180 / Math.PI;
        return dRotateAngle;
    }

//--------------------------------------------------------------------------------------------------------------
    //计算两个点之间的距离
    private static double EARTH_RADIUS = 6378.137;

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * 通过经纬度获取距离(单位：米)
     *
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return 距离
     */
    public static double getDistance(double lat1, double lng1, double lat2,
                                     double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000d) / 10000d;
        s = s * 1000;
        return s;
    }

    /**
     * 以真北为0度起点，由东向南向西顺时针旋转360度，主要是用于控制象限。
     根据2点经纬度，计算方位角
     * @param A
     * @param a
     * @param B
     * @param b
     * @return
     */
    public static double GetLineAngle(double A,double a,double B,double b)
    {
        double mathPI = 3.1415926535897931;
        double tmpValue = 0;
        double latStart = a * mathPI / 180;
        double lngStart = A * mathPI / 180;
        double latEnd = b * mathPI / 180;
        double lngEnd = B * mathPI / 180;
        if (A == B || a == b)
        {
            if (A == B)
            {
                /// 经度相同
                if (b >= a)
                {
                    return 0;
                }
                else
                {
                    return 180;
                }
            }
            else
            {
                /// 纬度相同
                if (B >= A)
                {
                    return 90;
                }
                else
                {
                    return 270;
                }
            }
        }

        tmpValue = Math.sin(latStart) * Math.sin(latEnd) + Math.cos(latStart) * Math.cos(latEnd) * Math.cos(lngEnd - lngStart);
        tmpValue = Math.sqrt(1 - tmpValue * tmpValue);
        tmpValue = Math.cos(latEnd) * Math.sin(lngEnd - lngStart) / tmpValue;
        double resultAngle = Math.abs(Math.asin(tmpValue) * 180 / mathPI);

        if (B > A)
        {
            if (b >= a)
            {
                /// 第一象限
                return resultAngle;
            }
            else
            {
                /// 第二象限
                return 180 - resultAngle;
            }
        }
        else
        {
            /// 第四象限
            if (b >= a)
            {
                return 360 - resultAngle;
            }
            else
            {
                /// 第三象限
                return 180 + resultAngle;
            }
        }
    }

}

