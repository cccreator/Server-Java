package com.*.*.jms.entity;

/**
 * @author *
 * @Date 2019-03-30
 * @Description
 * 线路实时速度实体
 */
public class RouteRealSpeedMessage {
    private String subRouteId; //子线id
    private String busCount; //车辆数
    private String realSpeed; //实时速度
    private String recDate; //实时时间
    public String getSubRouteId() {
        return subRouteId;
    }

    public void setSubRouteId(String subRouteId) {
        this.subRouteId = subRouteId;
    }

    public String getBusCount() {
        return busCount;
    }

    public void setBusCount(String busCount) {
        this.busCount = busCount;
    }

    public String getRealSpeed() {
        return realSpeed;
    }

    public void setRealSpeed(String realSpeed) {
        this.realSpeed = realSpeed;
    }

    public String getRecDate() {
        return recDate;
    }

    public void setRecDate(String recDate) {
        this.recDate = recDate;
    }
}