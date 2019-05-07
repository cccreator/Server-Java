//获取单程里程
public Map getSegMileStartEndInfo(){
    StringBuilder sql = new StringBuilder();
    sql.append(" select a.segmentid, " +
            "       a.sngmile, " +
            "       a.rundirection " +
            "  from mcsegmentinfogs a " +
            " where a.isactive = '1' " +
            "  and a.sngmile is not null " +
            "  and a.sngmile <> 0 " +
            " and a.segmentid is not null ");
    List<Object[]> result = getBySql(sql.toString());
    List list = new ArrayList();
    Map allData = new HashMap();
    for (Object [] obj:result) {
        Map map = new HashMap();
        if(obj[0] != null && obj[1] != null&& obj[2] != null){
            map.put("segmentId",String.valueOf(obj[0] == null ? "" : obj[0]));
            map.put("sngMile",String.valueOf(obj[1] == null ? "" : obj[1]));
            map.put("runDirection",String.valueOf(obj[2] == null ? "" : obj[2]));

            allData.put(String.valueOf(obj[0]),map);
        }
    }
    return allData;
}

//获取所有单程上的站点信息,以segmentid为key，站点组成的list为value，而每个list又是一个个站点信息map
public Map<String,List> getSngStationInfo(){
    StringBuffer sql = new StringBuffer();
    sql.append(" select a.segmentid, b.longitude, b.latitude,a.sngserialid " +
            "  from mcrsegmentstationgs a " +
            "  left join mcstationinfogs b " +
            "    on a.stationid = b.stationid " +
            "   where b.longitude <> 0 " +
            "   and b.latitude <> 0 " +
            "   and b.longitude is not null " +
            "   and b.latitude is not null " +
            " order by a.segmentid,a.sngserialid");
    List<Object[]> listResult = getBySql(sql.toString());
    Map allDataMap = new HashMap();
    String objectSegmentId = "";
    int j = 0;
    for(int i = 0; i < listResult.size();i++){
        objectSegmentId = String.valueOf(((Object[]) listResult.get(i))[0]);
        if(!allDataMap.keySet().contains(objectSegmentId)){
            Map map = new HashMap();
            List perSegmentStationList = new ArrayList();
            map.put("segmentId",String.valueOf(((Object[]) listResult.get(i))[0] == null ? "" : ((Object[]) listResult.get(i))[0]));
            map.put("longitude",String.valueOf(((Object[]) listResult.get(i))[1] == null ? "" : ((Object[]) listResult.get(i))[1]));
            map.put("latitude",String.valueOf(((Object[]) listResult.get(i))[2] == null ? "" : ((Object[]) listResult.get(i))[2]));
            map.put("sngserialid",String.valueOf(((Object[]) listResult.get(i))[3] == null ? "" : ((Object[]) listResult.get(i))[3]));
            perSegmentStationList.add(map);
            allDataMap.put(objectSegmentId,perSegmentStationList);
        }else{
            Map map = new HashMap();
            map.put("segmentId",String.valueOf(((Object[]) listResult.get(i))[0] == null ? "" : ((Object[]) listResult.get(i))[0]));
            map.put("longitude",String.valueOf(((Object[]) listResult.get(i))[1] == null ? "" : ((Object[]) listResult.get(i))[1]));
            map.put("latitude",String.valueOf(((Object[]) listResult.get(i))[2] == null ? "" : ((Object[]) listResult.get(i))[2]));
            map.put("sngserialid",String.valueOf(((Object[]) listResult.get(i))[3] == null ? "" : ((Object[]) listResult.get(i))[3]));
            ((ArrayList)allDataMap.get(objectSegmentId)).add(map);
            allDataMap.put(objectSegmentId,allDataMap.get(objectSegmentId));
        }
    }

    return allDataMap;
}