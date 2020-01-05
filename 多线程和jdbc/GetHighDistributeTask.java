package com.hisense.hiose.service.impl;

import com.hisense.base.common.util.*;
import com.hisense.base.common.util.graphics.RedisKeyParamter;
import com.hisense.hiose.Gps.bean.dbCommon.ClsBusInfo;
import com.hisense.hiose.Gps.bean.dbCommon.ClsRouteInfo;
import com.hisense.hiose.bean.BusRunRecord;
import com.hisense.hiose.entity.DriveHighDisData;
import com.hisense.hiose.jms.util.StringUtils;
import com.hisense.hiose.service.NEGisMonitorService;
import com.hisense.smartroad.base.utils.TypeEntryUtils;
import com.vividsolutions.jts.geom.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * Created by panxiaochen on 2020-01-01.
 */

public class GetHighDistributeTask implements Callable<Map> {
    private List<DriveHighDisData> listWg;
    private String quence;
    @Autowired
    private NEGisMonitorService neGisMonitorService;

    public GetHighDistributeTask(List listWg,String quence) {
        this.listWg = listWg;
        this.quence = quence;
    }

    /**
     * 任务的具体过程，一旦任务传给ExecutorService的submit方法，则该方法自动在一个线程上执行。
     *
     * @return
     * @throws Exception
     */
    public Map call() throws Exception {
        System.out.println("call()方法被自动调用！！！             " + Thread.currentThread().getName());
        JdbcTemplate template = SpringContextHolder.getBean(JdbcTemplate.class);
        CoordinateConversion coordinateConversion = new CoordinateConversion();
        GeometryFactory geometryFactory = new GeometryFactory();
        String dateStr = quence;
        List<DriveHighDisData> resultListWg = new ArrayList<>();
        String dateStr1 = String.valueOf(Integer.parseInt(dateStr)  + 1);
        if (dateStr.split("").length == 1){
            dateStr = "0" + dateStr;
        }
        if (dateStr1.split("").length == 1){
            dateStr1 = "0" + dateStr1;
        }
        dateStr = "20";
        dateStr1 = "21";
        //当月第一天和最后一天
        //String yearAndMonth = DateUtil.getLastDate(new Date(),"yyyy-MM");
        String lastMonthFirstDay = "2019-11-" + dateStr;
        String lastMonthLastDay = "2019-11-" + dateStr1;
        Map mapClsBusInfo = RedisUtil.getMap(RedisKeyParamter.cacheMapBusInfoByProductId, ClsBusInfo.class);
        Map mapClsRouteInfo =RedisUtil.getMap(RedisKeyParamter.cacheMapRouteByRouteCode, ClsRouteInfo.class);
        StringBuffer wgSql = new StringBuffer();
        wgSql.append(" select LD5.BUSRDID,  " +
                "       LD5.PRODUCTID,  " +
                "       LD5.GPSLONGITUDE,  " +
                "       LD5.GPSLATITUDE,  " +
                "       LD5.PECCANCYTIME,  " +
                "       LD5.Bcrouteid ," +
                "  LD5.PECCANCYTYPE "+
                "  from BSVCBUSVIOLLD5 LD5  " +
                "  LEFT JOIN MCBUSINFOGS BIF  " +
                "    ON LD5.PRODUCTID = BIF.PRODUCTID  " +
                " WHERE LD5.PECCANCYTIME BETWEEN TO_DATE('" +lastMonthFirstDay + "', 'yyyy-MM-dd') AND  " +
                "       TO_DATE('" + lastMonthLastDay + "', 'yyyy-MM-dd')  " +
                "AND LD5.PECCANCYTYPE IN ('8','141') "  +
                "   and LD5.GPSLONGITUDE is not null and LD5.GPSLATITUDE is not null and LD5.GPSLONGITUDE <> 0 and LD5.GPSLATITUDE <> 0");

        //rs 的映射规则
        RowMapper<DriveHighDisData> rowmapper =
                new RowMapper<DriveHighDisData>() {
                    public DriveHighDisData mapRow(
                            ResultSet rs,
                            int index)
                            throws SQLException {
                        String BUSRDID = rs.getString("BUSRDID");
                        String PRODUCTID = rs.getString("PRODUCTID");
                        String GPSLONGITUDE = rs.getString("GPSLONGITUDE");
                        String GPSLATITUDE = rs.getString("GPSLATITUDE");
                        String PECCANCYTIME = rs.getString("PECCANCYTIME");
                        String Bcrouteid = rs.getString("Bcrouteid");
                        String PECCANCYTYPE = rs.getString("PECCANCYTYPE");
                        return new DriveHighDisData(BUSRDID,PRODUCTID,GPSLONGITUDE,GPSLATITUDE,PECCANCYTIME,Bcrouteid,PECCANCYTYPE);
                    }
                };
        List<DriveHighDisData> wgList = template.query(wgSql.toString(), rowmapper, new Object[]{});
        //遍历wgList，将所有数据封装进resultListWg
        for (DriveHighDisData driveHighDisData:wgList){
            //事故id
            String busRdId = driveHighDisData.getBusRdId();
            String productId = driveHighDisData.getProductId();
            String longitude = driveHighDisData.getLongitude();
            String latitude = driveHighDisData.getLatitude();
            //事故时间
            String accTime = driveHighDisData.getAccTime();
            String routeId = driveHighDisData.getRouteId();
            //事故类型
            String accType = driveHighDisData.getAccType();
            if (!StringUtils.isBlank(busRdId) && !StringUtils.isBlank(productId)
                    && !StringUtils.isBlank(longitude) && !StringUtils.isBlank(latitude)
                    && !StringUtils.isBlank(routeId) && !StringUtils.isBlank(accType)){
                DriveHighDisData driveHighDisData1 = new DriveHighDisData();
                driveHighDisData1.setBusRdId(busRdId);
                driveHighDisData1.setProductId(productId);
                ClsBusInfo clsBusInfo = (ClsBusInfo)mapClsBusInfo.get(productId);
                if (clsBusInfo != null){
                    driveHighDisData1.setCarNo(String.valueOf(clsBusInfo.getBusCardNo()));
                }
                driveHighDisData1.setLongitude(longitude);
                driveHighDisData1.setLatitude(latitude);
                driveHighDisData1.setWgTime(accTime);
                driveHighDisData1.setRouteId(routeId);
                ClsRouteInfo clsRouteInfo = (ClsRouteInfo)mapClsRouteInfo.get(routeId);
                if (clsRouteInfo != null){
                    driveHighDisData1.setRouteName(String.valueOf(clsRouteInfo.getRouteName()));
                }
                String wgcdKey1Value = TypeEntryUtils.getItemKey("PECCANCYTYPE_ADAS", accType , "");
                driveHighDisData1.setWgContent(String.valueOf(wgcdKey1Value));
                resultListWg.add(driveHighDisData1);
            }
        }

        DriveHighDisData driveHighDisData_Other = null;
        DriveHighDisData driveHighDisData = null;
        String busRdId_Other = null;
        Point point = null;
        Point point_Other = null;
        Polygon pointPolygon = null;
        Polygon pointPolygon_Other = null;

        Map map = new HashMap();
        //第一个List，遍历每一天的数据
        for(int i = 0 ;i<resultListWg.size();i++){
            Map map1 = new HashMap();
            driveHighDisData = resultListWg.get(i);
            if (driveHighDisData != null){
                String busRdId = driveHighDisData.getBusRdId();
                Double longitude = Double.valueOf(driveHighDisData.getLongitude());
                Double latitude = Double.valueOf(driveHighDisData.getLatitude());
                Coordinate[] coordinates = new Coordinate[1];
                //将站点经纬度投影，必须投影
                double [] testUtm = coordinateConversion.GPS2Gauss(longitude,latitude);
                coordinates[0] = new Coordinate(testUtm[0],testUtm[1]);
                //获取点对象
                point = geometryFactory.createPoint(coordinates[0]);
                pointPolygon = (Polygon) point.buffer(250);

                List list = new ArrayList();
                Integer clusterSum = 0;
                //第二个List,遍历所有违规点
                for (int j = 0;j < listWg.size();j++) {
                    driveHighDisData_Other = listWg.get(j);
                    if (driveHighDisData_Other != null){
                        busRdId_Other = driveHighDisData_Other.getBusRdId();
                        if (!busRdId.equals(busRdId_Other)) {
                            Double longitude_Other = Double.valueOf(driveHighDisData_Other.getLongitude());
                            Double latitude_Other = Double.valueOf(driveHighDisData_Other.getLatitude());
                            Coordinate[] coordinates_Other = new Coordinate[1];
                            //将站点经纬度投影，必须投影
                            double[] testUtm_Other = coordinateConversion.GPS2Gauss(longitude_Other, latitude_Other);
                            coordinates_Other[0] = new Coordinate(testUtm_Other[0], testUtm_Other[1]);
                            //获取点对象
                            point_Other = geometryFactory.createPoint(coordinates_Other[0]);
                            pointPolygon_Other = (Polygon) point_Other.buffer(1);
                            Geometry interPoint = pointPolygon_Other.intersection(pointPolygon);
                            if (interPoint.getLength() > 0){
                                pointPolygon.union(pointPolygon_Other);
                                driveHighDisData_Other.setFlag(busRdId);
                                Map mapOther = new HashMap();
                                mapOther.put(busRdId_Other,driveHighDisData_Other);
                                list.add(mapOther);
                                clusterSum++;
                            }
                        }
                    }
                }
                driveHighDisData.setFlag(busRdId);
                driveHighDisData.setSgClusterSum(clusterSum);
                map1.put(busRdId,driveHighDisData);
                list.add(map1);
                if (list.size() >= 5){
                    map.put(busRdId,list);
                }
            }
        }
        //map为所有违规点计算后的数据
        return map;
    }
}
