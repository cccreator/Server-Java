 public List playReviewList(String carno, String beginTime, String endTime, String routeId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        List<Map> mapList = new ArrayList();

        try {
            System.out.println("zong时间开始1---------------------" + new Date());
            StringBuffer sql = new StringBuffer();
            String productId = MapCacheManager.cacheMapBusInfoByBusCardNo.get(carno).getProductID();
            if (productId == null) {
                String proSql = "SELECT PRODUCTID FROM MCBUSINFOGS WHERE CARDID='" + carno + "'";
                productId = String.valueOf(getBySql(proSql).get(0));
            }
            if (StringUtils.isNotEmpty(carno)) {
                sql.append(" SELECT O.ORGNAME as ORGNAME,R.ROUTENAME as ROUTENAME, " +
                        " T.* FROM ( SELECT RESERVECHAR4 AS CARDID,ROUTEID,LONGITUDE,LATITUDE," +
                        " PRODUCTID,ACTDATETIME,RECDATETIME,ROUND(GPSSPEED * 3.6, 2) AS GPSSPEED,ROUND(GPSMILE / 1000, 2) AS GPSMILE " +
                        " FROM BSVCBUSRUNDATALD5 " +
                        " WHERE DATATYPE = '3' " +
                        " AND LONGITUDE > 0 " +
                        " AND LATITUDE >0 " +
                        " AND PRODUCTID = " + productId);
                if (routeId != null && !routeId.equals("") && !PropertiesUtils.getValue("cityName").equals("QD")) {
                    sql.append(" and routeid = " + routeId);
                }
                if (StringUtils.isNotEmpty(beginTime) && StringUtils.isNotEmpty(endTime)) {
                    sql.append(" AND ACTDATETIME  BETWEEN TO_DATE( '" + beginTime + "','YYYY-MM-DD HH24:MI:SS') " +
                            " AND TO_DATE( '" + endTime + "','YYYY-MM-DD HH24:MI:SS') ");
                } else if (StringUtils.isEmpty(beginTime) && StringUtils.isNotEmpty(endTime)) {
                    sql.append(" AND ACTDATETIME < TO_DATE('" + endTime + "','YYYY-MM-DD HH24:MI:SS') ");
                } else if (StringUtils.isEmpty(endTime) && StringUtils.isNotEmpty(beginTime)) {
                    sql.append(" AND ACTDATETIME  > TO_DATE('" + beginTime + "','YYYY-MM-DD HH24:MI:SS') ");
                }
                sql.append("  ) T " +
                        "LEFT JOIN MCROUTEINFOGS R ON to_char(T.ROUTEID) = R.ROUTEID AND R.ISACTIVE = '1' " +
                        "LEFT JOIN MCORGINFOGS O ON R.ORGID = O.ORGID AND O.ISACTIVE = '1' ");
                sql.append(" ORDER BY T.ACTDATETIME ");

                System.out.println("sql执行时间开始1---------------------" + new Date());
                //List<Object[]> list = getBySql(sql.toString());
                String cityName = PropertiesUtils.getValue("cityName");
                conn = getConnection();
                pstmt = conn.prepareStatement(sql.toString());
                pstmt.setFetchSize(1000);
                ResultSet rs = pstmt.executeQuery();
                System.out.println("sql执行时间结束2---------------------" + new Date());
                System.out.println("java执行时间开始1---------------------" + new Date());
                while (rs.next()) {
                    String orgName = rs.getString("ORGNAME");
                    String routeName = rs.getString("ROUTENAME");
                    String carNo = rs.getString("CARDID");
                    String longitude = rs.getString("LONGITUDE");
                    String latitude = rs.getString("LATITUDE");
                    String actDateTime = rs.getString("ACTDATETIME");
                    String recDateTime = rs.getString("RECDATETIME");
                    String gpsSpeed = rs.getString("GPSSPEED");
                    String gpsMile = rs.getString("GPSMILE");
                    Map map = new HashMap();
                    map.put("orgname", orgName == null ? "" : orgName);//obj[0].toString()
                    map.put("routename", routeName == null ? "" : routeName);
                    map.put("carno", carNo == null ? "" : carNo);   //String.valueOf((obj[2] == null) ? "" : obj[2])
                    map.put("routeid", routeId == null ? "" : routeId);

                    if (cityName.equals("QD") || cityName.equals("SZ")) {//青岛和深圳的轨迹回放GPS偏移
                        double[] offSetDate = GpsUtil.toGCJ02Point(Double.parseDouble(latitude), Double.parseDouble(longitude));
                        String lon = String.valueOf(offSetDate[1]);
                        String lat = String.valueOf(offSetDate[0]);
                        map.put("longitude", (lon == null) ? "" : lon);
                        map.put("latitude", (lat == null) ? "" : lat);
                    } else {
                        map.put("longitude", longitude == null ? "" : longitude);
                        map.put("latitude", latitude == null ? "" : latitude);
                    }

                    map.put("productid", productId == null ? "" : productId);
                    map.put("actdatetime", actDateTime == null ? "" : actDateTime);
                    map.put("recdatetime", recDateTime == null ? "" : recDateTime);
                    if (!gpsSpeed.equals("0") && gpsSpeed != null) {
                        map.put("gpsspeed", String.format("%.2f", Double.parseDouble(gpsSpeed)));
                    }
                    if (!gpsMile.equals("0") && gpsMile != null) {
                        map.put("gpsmile", String.format("%.2f", Double.parseDouble(gpsMile)));
                    }
                    mapList.add(map);
                }
                if (cityName.equals("SZ")) {
                    for (int i = 0; i < mapList.size() - 1; i++) {
                        Double longitude = Double.parseDouble(String.valueOf(((Map) mapList.get(i)).get("productid")));
                        Double latitude = Double.parseDouble(String.valueOf(((Map) mapList.get(i)).get("latitude")));
                        Double longitude1 = Double.parseDouble(String.valueOf(((Map) mapList.get(i + 1)).get("productid")));
                        Double latitude1 = Double.parseDouble(String.valueOf(((Map) mapList.get(i + 1)).get("latitude")));
                        if (longitude != null && latitude != null) {
                            double[] offSetDateFist = GpsUtil.toGCJ02Point(latitude, longitude);
                            double[] offSetDateNext = GpsUtil.toGCJ02Point(latitude1, longitude1);

                            Double firstOffSetLon = offSetDateFist[1];
                            Double firstOffSetLat = offSetDateFist[0];
                            Double nextOffSetLon = offSetDateNext[1];
                            Double nextOffSetLat = offSetDateNext[0];

                            double xgap = firstOffSetLon - nextOffSetLon;
                            double ygap = firstOffSetLat - nextOffSetLat;

                            double dou;
                            double dou1;
                            dou = Math.abs(xgap);
                            dou1 = Math.abs(ygap);

                            DecimalFormat df = new DecimalFormat("#.00000");

                            if (Double.parseDouble(df.format(dou)) > 0.00001 && Double.parseDouble(df.format(dou1)) > 0.00001) {
                                double angle = GpsUtil.getAngle(firstOffSetLon, firstOffSetLat, nextOffSetLon, nextOffSetLat);
//                        double angle = GpsUtil.getAngle(firstOffSetLat,firstOffSetLon,nextOffSetLat ,nextOffSetLon);
                                ((Map) mapList.get(i)).put("angle", String.valueOf(angle));
                            }
                        }
                    }
                }
                System.out.println("java执行时间结束2---------------------" + new Date());


                return mapList;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return mapList;
        } finally {
            if (pstmt != null) {
                pstmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }




 /**
     * 结存违规500米范围高发点聚集详情
     * @param dateStr
     * @return
     */
    @Override
    public List calWgHighDisPointDetail(String dateStr){
        System.out.println("-------------------违规数据计算---开始-----------" + new Date());
        List resultList = new ArrayList();
        List resultList1 = new ArrayList();
        List resultList2 = new ArrayList();
        Map map = new HashMap();
        List<Future<Map>> futureList = new ArrayList<Future<Map>>();
        //获取所有违规数据
        List<DriveHighDisData> listWg = neGisMonitorService.getDirHighDisDataWg(dateStr);

        //获取上个月的时间，格式为yyyy-MM
        String yearAndMonth = DateUtil.getLastDate(new Date(),"yyyy-MM");
        String [] yearAndMonthArr = yearAndMonth.split("-");
        int year = Integer.parseInt(yearAndMonthArr[0]);
        int month = Integer.parseInt(yearAndMonthArr[1]);
        year = 2019;
        month = 11;

        //存放每个月有多少天，闰年2月＋一天
        int [] monthDay = {31,28,31,30,31,30,31,31,30,31,30,31};
        if (year % 4 == 0){
            monthDay[1]++;
        }

        //每天一个线程
        ExecutorService service = Executors.newFixedThreadPool(monthDay[month]);
        for(int i = 1;i <= 1 ;i++){//monthDay[month]
            Future<Map> f = service.submit(new GetHighDistributeTask(listWg, String.valueOf(i)));
            futureList.add(f);
        }
        service.shutdown();

        for (Future<Map> future : futureList) {
            try {
                while (true) {
                    if (future.isDone() && !future.isCancelled()) {
                        //收集结果集
                        map.putAll(future.get());
                        System.out.println("Future:" + future + "");
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //第二步，将第一步中每个点放进List,得到List<List<Map<String,DriveHighDisData>>>
        List allDataList = new ArrayList();
        Set set = map.keySet();
        Iterator iterator = set.iterator();
        String key = "";
        while (iterator.hasNext()){
            key = (String)iterator.next();
            allDataList.add(map.get(key));
        }

        //第三步，计算找出每个黑点，同时将黑点中每个点在第一步的map中剔除
        //
        List list11 = new ArrayList();
        List compareList = null;
        List list1 = null;
        String keyForRemove = null;
        Map mapForRemove = null;
        for(int i = 0; i < allDataList.size(); i++){
            int flag = 0;
            int maxCount = 0;
            if (((List)allDataList.get(i)).size() < 5){
                continue;
            }
            for(int j = 0;j < allDataList.size(); j++){
                compareList = (List)allDataList.get(j);
                if (compareList.size() >= 5 &&!list11.contains(j)){
                    int count = compareList.size();
                    if (count > maxCount){
                        maxCount = count;
                        flag = j;
                    }
                }else{
                    continue;
                }
            }
            if (!list11.contains(flag)){
                list1 = (List)allDataList.get(flag);
                for (int k = 0; k < list1.size(); k++){
                    list1.get(k);
                    mapForRemove = (Map)list1.get(k);
                    keyForRemove = String.valueOf(mapForRemove.keySet().iterator().next());
                    if (map.containsKey(keyForRemove)){
                        String keyFlag1 = ((DriveHighDisData)mapForRemove.get(keyForRemove)).getFlag();
                        if (!keyForRemove.equals(keyFlag1)){
                            map.remove(keyForRemove);
                            allDataList.set(flag,new ArrayList());
                        }
                    }else{
                        continue;
                    }
                }
                list11.add(flag);
            }
        }

        //第四步，将第一步中剔除后的map中的所有点拿出来放进map，再放进List
        Set resultSet = map.keySet();
        Iterator iterator1 = resultSet.iterator();
        String key1 = "";
        while (iterator1.hasNext()) {
            key1 = (String) iterator1.next();
            List listR = (List) map.get(key1);
            String key2 = "";
            for (int t = 0; t < listR.size(); t++) {
                Map map2 = (Map) listR.get(t);
                key2 = (String) map2.keySet().iterator().next();
                DriveHighDisData driveHighDisData = (DriveHighDisData) map2.get(key2);
                Map treeMap = new TreeMap();
                treeMap.put("busRdId", driveHighDisData.getBusRdId());
                treeMap.put("productId", driveHighDisData.getProductId());
                treeMap.put("carNo", driveHighDisData.getCarNo());
                treeMap.put("longitude", driveHighDisData.getLongitude());
                treeMap.put("latitude", driveHighDisData.getLatitude());
                treeMap.put("wgTime", driveHighDisData.getWgTime());
                treeMap.put("routeId", driveHighDisData.getRouteId());
                treeMap.put("routeName", driveHighDisData.getRouteName());
                treeMap.put("wgContent", driveHighDisData.getWgContent());
                treeMap.put("flag", driveHighDisData.getFlag());
                treeMap.put("wgClusterSum", String.valueOf(driveHighDisData.getSgClusterSum()));
                resultList1.add(treeMap);
            }
        }

        //第五步，将所有黑点中心点放进map中，再放进List
        Set resultSet1 = map.keySet();
        Iterator iterator2 = resultSet1.iterator();
        while (iterator2.hasNext()) {
            key1 = (String) iterator2.next();
            List listR = (List) map.get(key1);
            String key2 = "";
            for (int t = 0; t < listR.size(); t++) {
                Map map2 = (Map) listR.get(t);
                key2 = (String) map2.keySet().iterator().next();
                DriveHighDisData driveHighDisData = (DriveHighDisData) map2.get(key2);
                String flag = driveHighDisData.getFlag();
                if (key2.equals(flag) && StringUtils.isBlank(flag)) {
                    DriveHighDisData driveHighDisData1 = (DriveHighDisData) map2.get(flag);
                    if(driveHighDisData1 != null){
                        Map treeMap = new TreeMap();
                        treeMap.put("busRdId", driveHighDisData1.getBusRdId());
                        treeMap.put("productId", driveHighDisData1.getProductId());
                        treeMap.put("carNo", driveHighDisData1.getCarNo());
                        treeMap.put("longitude", driveHighDisData1.getLongitude());
                        treeMap.put("latitude", driveHighDisData1.getLatitude());
                        treeMap.put("wgTime", driveHighDisData1.getWgTime());
                        treeMap.put("routeId", driveHighDisData1.getRouteId());
                        treeMap.put("routeName", driveHighDisData1.getRouteName());
                        treeMap.put("wgContent", driveHighDisData1.getWgContent());
                        treeMap.put("flag", driveHighDisData1.getFlag());
                        treeMap.put("wgClusterSum", String.valueOf(driveHighDisData1.getSgClusterSum()));
                        resultList2.add(treeMap);
                    }
                }
                break;
            }
        }
        resultList.add(resultList1);//所有点的信息
        resultList.add(resultList2);//聚集中心黑点信息
        System.out.println("-------------------违规数据计算---结束-----------" + new Date());
        return resultList;
    }






     /**
     * 结存违规500米范围高发点聚集详情
     * @param dateStr
     * @return
     */
    @Override
    public List calWgHighDisPointDetail(String dateStr){
        System.out.println("-------------------违规数据计算---开始-----------" + new Date());
        List resultList = new ArrayList();//总结果集
        List resultList1 = new ArrayList();//所有违规点的结果集
        List resultList2 = new ArrayList();//所有违规黑点的结果集
        Map map = new HashMap();//第一步中通过多线程取算的所有违规点数据放进map里面
        
        //第一步利用多线程将违规黑点数据计算放进map里面
        List<Future<Map>> futureList = new ArrayList<Future<Map>>();
        //获取所有违规数据
        List<DriveHighDisData> listWg = neGisMonitorService.getDirHighDisDataWg(dateStr);

        //获取上个月的时间，格式为yyyy-MM
        String yearAndMonth = DateUtil.getLastDate(new Date(),"yyyy-MM");
        String [] yearAndMonthArr = yearAndMonth.split("-");
        int year = Integer.parseInt(yearAndMonthArr[0]);
        int month = Integer.parseInt(yearAndMonthArr[1]);

        //存放每个月有多少天，闰年2月＋一天
        int [] monthDay = {31,28,31,30,31,30,31,31,30,31,30,31};
        if (year % 4 == 0){
            monthDay[1]++;
        }

        //每天一个线程
        ExecutorService service = Executors.newFixedThreadPool(monthDay[month]);
        for(int i = 1;i < monthDay[month] ;i++){
            Future<Map> f = service.submit(new GetHighDistributeTask(listWg, String.valueOf(i)));
            futureList.add(f);
        }
        service.shutdown();

        for (Future<Map> future : futureList) {
            try {
                while (true) {
                    if (future.isDone() && !future.isCancelled()) {
                        //收集结果集，这里map里面的数据会很大，hash冲突预计会比较多，
                        // 第三步中对map的操作可能会变慢
                        map.putAll(future.get());
                        System.out.println("Future:" + future + "");
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //第二步，将第一步中每个点放进List,得到List<List<Map<String,DriveHighDisData>>>
        List allDataList = new ArrayList();
        Set set = map.keySet();
        Iterator iterator = set.iterator();
        String key = "";
        while (iterator.hasNext()){
            key = (String)iterator.next();
            allDataList.add(map.get(key));
        }

        //第三步，计算找出每个黑点，同时将黑点中每个点在第一步的map中剔除
        //
        List list11 = new ArrayList();
        List compareList = null;
        List list1 = null;
        String keyForRemove = null;
        Map mapForRemove = null;
        for(int i = 0; i < allDataList.size(); i++){
            int flag = 0; //记录黑点在list中的位置
            int maxCount = 0;
            if (((List)allDataList.get(i)).size() < 5){
                continue;
            }
            for(int j = 0;j < allDataList.size(); j++){
                compareList = (List)allDataList.get(j);
                if (compareList.size() >= 5 &&!list11.contains(j)){
                    int count = compareList.size();
                    if (count > maxCount){
                        maxCount = count;
                        flag = j;
                    }
                }else{
                    continue;
                }
            }
            
            //将黑点中包含的所有点，在map中干掉，这样map中剩下的数据就是所有黑点的数据
            if (!list11.contains(flag)){
                list1 = (List)allDataList.get(flag);
                for (int k = 0; k < list1.size(); k++){
                    list1.get(k);
                    mapForRemove = (Map)list1.get(k);
                    keyForRemove = String.valueOf(mapForRemove.keySet().iterator().next());
                    if (map.containsKey(keyForRemove)){
                        String keyFlag1 = ((DriveHighDisData)mapForRemove.get(keyForRemove)).getFlag();
                        if (!keyForRemove.equals(keyFlag1)){
                            map.remove(keyForRemove);
                            allDataList.set(flag,new ArrayList());
                        }
                    }else{
                        continue;
                    }
                }
                list11.add(flag);
            }
        }

        //第四步，将第一步中剔除后的map中的所有点拿出来放进map，再放进List
        Set resultSet = map.keySet();
        Iterator iterator1 = resultSet.iterator();
        String key1 = "";
        while (iterator1.hasNext()) {
            key1 = (String) iterator1.next();
            List listR = (List) map.get(key1);
            String key2 = "";
            for (int t = 0; t < listR.size(); t++) {
                Map map2 = (Map) listR.get(t);
                key2 = (String) map2.keySet().iterator().next();
                DriveHighDisData driveHighDisData = (DriveHighDisData) map2.get(key2);
                Map treeMap = new TreeMap();
                treeMap.put("busRdId", driveHighDisData.getBusRdId());
                treeMap.put("productId", driveHighDisData.getProductId());
                treeMap.put("carNo", driveHighDisData.getCarNo());
                treeMap.put("longitude", driveHighDisData.getLongitude());
                treeMap.put("latitude", driveHighDisData.getLatitude());
                treeMap.put("wgTime", driveHighDisData.getWgTime());
                treeMap.put("routeId", driveHighDisData.getRouteId());
                treeMap.put("routeName", driveHighDisData.getRouteName());
                treeMap.put("wgContent", driveHighDisData.getWgContent());
                treeMap.put("flag", driveHighDisData.getFlag());
                treeMap.put("wgClusterSum", String.valueOf(driveHighDisData.getSgClusterSum()));
                resultList1.add(treeMap);
            }
        }

        //第五步，将所有黑点中心点放进map中，再放进List
        Set resultSet1 = map.keySet();
        Iterator iterator2 = resultSet1.iterator();
        while (iterator2.hasNext()) {
            key1 = (String) iterator2.next();
            List listR = (List) map.get(key1);
            String key2 = "";
            for (int t = 0; t < listR.size(); t++) {
                Map map2 = (Map) listR.get(t);
                key2 = (String) map2.keySet().iterator().next();
                DriveHighDisData driveHighDisData = (DriveHighDisData) map2.get(key2);
                String flag = driveHighDisData.getFlag();
                //key和flag相等的点即为黑点
                if (key2.equals(flag) && !StringUtils.isBlank(flag)) {
                    DriveHighDisData driveHighDisData1 = (DriveHighDisData) map2.get(flag);
                    if(driveHighDisData1 != null){
                        Map treeMap = new TreeMap();
                        treeMap.put("busRdId", driveHighDisData1.getBusRdId());
                        treeMap.put("productId", driveHighDisData1.getProductId());
                        treeMap.put("carNo", driveHighDisData1.getCarNo());
                        treeMap.put("longitude", driveHighDisData1.getLongitude());
                        treeMap.put("latitude", driveHighDisData1.getLatitude());
                        treeMap.put("wgTime", driveHighDisData1.getWgTime());
                        treeMap.put("routeId", driveHighDisData1.getRouteId());
                        treeMap.put("routeName", driveHighDisData1.getRouteName());
                        treeMap.put("wgContent", driveHighDisData1.getWgContent());
                        treeMap.put("flag", driveHighDisData1.getFlag());
                        treeMap.put("wgClusterSum", String.valueOf(driveHighDisData1.getSgClusterSum()));
                        resultList2.add(treeMap);
                    }
                    break;
                }
            }
        }
        resultList.add(resultList1);//所有点的信息
        resultList.add(resultList2);//聚集中心黑点信息
        System.out.println("-------------------违规数据计算---结束-----------" + new Date());
        return resultList;
    }




    
