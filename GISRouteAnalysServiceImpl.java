 public void calNonlinearCoefficient()throws  ParseException{
        Map sngMileMap = gisRouteAnalyseDao.getSegMileStartEndInfo(); //获取里程
        Map<String,List> sngStationMap = gisRouteAnalyseDao.getSngStationInfo();//获取所有单程上的站点
        List calResultList = new ArrayList();
        Iterator it = sngStationMap.keySet().iterator();
        while(it.hasNext()){
            Map calResultMap = new HashMap();
            String key = it.next().toString();
            List sngStationList = sngStationMap.get(key);
            if(sngMileMap.containsKey(key)){
                //单程id
                String segmentId  = String.valueOf(((HashMap)sngStationList.get(0)).get("segmentId"));
                //单程里程
                Double sngMile  = Double.parseDouble(String.valueOf(((HashMap)sngMileMap.get(key)).get("sngMile")));
                //首站经度
                Double startLon  = Double.parseDouble(String.valueOf(((HashMap)sngStationList.get(0)).get("longitude")));
                //首站纬度
                Double startLat  = Double.parseDouble(String.valueOf(((HashMap)sngStationList.get(0)).get("latitude")));
                //末站经度
                Double endLon  = Double.parseDouble(String.valueOf(((HashMap)sngStationList.get(sngStationList.size()-1)).get("longitude")));
                //末站纬度
                Double endLat  = Double.parseDouble(String.valueOf(((HashMap)sngStationList.get(sngStationList.size()-1)).get("latitude")));
                //运行方向
                String runDirection  = String.valueOf(((HashMap)((HashMap)sngMileMap).get(segmentId)).get("runDirection"));
                Double startAndEndMile = 0.0;
                //当不是环形线路时
                if((!startLon.equals(endLon) && !startLat.equals(endLat)) || !runDirection.equals("3")){//不是环行直接用首末站距离
                    startAndEndMile = GpsUtil.getDistanceKm(startLat,startLon,endLat,endLon);
                }else{//环行，取第一个停靠站与其他站最远距离
                    List perSngStationInfoList = sngStationMap.get(segmentId);
                    Double firstStationLon = Double.parseDouble(String.valueOf(((HashMap)perSngStationInfoList.get(0)).get("longitude")));
                    Double firstStationLat = Double.parseDouble(String.valueOf(((HashMap)perSngStationInfoList.get(0)).get("latitude")));
                    Double maxDistance = 0.0;
                    for(int j = 1 ;j < perSngStationInfoList.size();j++){//环形线路计算距离首站最远距离
                        Double stationLon = Double.parseDouble(String.valueOf(((HashMap)perSngStationInfoList.get(j)).get("longitude")));
                        Double stationLat = Double.parseDouble(String.valueOf(((HashMap)perSngStationInfoList.get(j)).get("latitude")));
                        Double distance = GpsUtil.getDistanceKm(firstStationLat,firstStationLon,stationLat,stationLon);
                        if(distance > maxDistance){
                            maxDistance = distance;
                        }
                    }
                    startAndEndMile = maxDistance;
                }
                //非直线系数
                String onlinearCoefficient = String.valueOf(sngMile/startAndEndMile);
                calResultMap.put("segmentId",segmentId);
                calResultMap.put("onlinearCoefficient",onlinearCoefficient);
                calResultList.add(calResultMap);
            }

        }
        /**
         * update 将非直线系数update进单程基础信息表里面
         * **/
        if (Constants.CITYNAME_WLMQ.equals(propertiesUtil.getCityName())) {
            try{
                long t0 = System.currentTimeMillis();
                String deleteData = "delete from errorInfoCheck ";
                gisRouteAnalyseDao.executeUpdateBySql(deleteData);
                for(int i=0;i<calResultList.size();i++){
                    Map<String,String> map = (Map) calResultList.get(i);
                    if(!map.get("onlinearCoefficient").equals("Infinity")&&
                          Double.parseDouble(map.get("onlinearCoefficient")) > 1 &&
                            Double.parseDouble(map.get("onlinearCoefficient")) < 20){
                        String updateSql = " update mcsegmentinfogs  set hold8='"+map.get("onlinearCoefficient")+"' where segmentid = '"+map.get("segmentId")+"'";
                        gisRouteAnalyseDao.executeUpdateBySql(updateSql);
                    }else{
                        //当发生数据错误时，把segmentId，sngMile，startLon，startLat，endLon，endLat，onlinearCoefficient存进表errorInfoCheck
                        String segmentId = map.get("segmentId");
                        String sngMile = String.valueOf(((HashMap)sngMileMap.get(segmentId)).get("sngMile"));
                        List sngStationErrorList = sngStationMap.get(segmentId);
                        String startLon  = String.valueOf(((HashMap)sngStationErrorList.get(0)).get("longitude"));
                        String startLat  = String.valueOf(((HashMap)sngStationErrorList.get(0)).get("latitude"));
                        String endLon  = String.valueOf(((HashMap)sngStationErrorList.get(sngStationErrorList.size()-1)).get("longitude"));
                        String endLat  = String.valueOf(((HashMap)sngStationErrorList.get(sngStationErrorList.size()-1)).get("latitude"));
                        String onlinearCoefficient = map.get("onlinearCoefficient");
                        String updateSql = "insert into errorInfoCheck(segmentId,sngMile,startLon,startLat,endLon,endLat,onlinearCoefficient)values("
                        + segmentId + "," + sngMile + "," + startLon + "," + startLat + "," + endLon + "," + endLat + ",'" +onlinearCoefficient + "') ";
                        //将异常数据导入到表里面以便检查
                        gisRouteAnalyseDao.executeUpdateBySql(updateSql);
                    }
                }
                System.out.print("单程非直线系数更新时间：" + (double)(System.currentTimeMillis() - t0)/1000 + "秒");
            }catch (Exception e){
                System.out.println("====================单程非直线系数更新报错： " + new Date() + " =======================");
                e.printStackTrace();
            }
        }

    }