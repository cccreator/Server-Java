//动态监测，搜车牌号和线路
    public List getCarNoOrRoute(String inputStr){
        StringBuffer inputUpStr = new StringBuffer();
        //若存在小写，则转换为大写
        for(int i =0;i<inputStr.length();i++) {
            inputUpStr.append(Character.toUpperCase(inputStr.charAt(i)));
        }
        Set<String> set = MapCacheManager.cacheMapBusInfoByBusId.keySet();
        Iterator itorCarNo1 = set.iterator();
        Iterator itorCarNo2 = set.iterator();
        Iterator itorCarNo3 = set.iterator(); //从第二位开始模糊  2  从‘粤B’后面开始模糊
        Iterator itorCarNo4 = set.iterator();//从第1位开始模糊 1   从‘B’开始模糊
        Iterator itorCarNo5 = set.iterator();//从第0位开始模糊 0   从‘粤’开始模糊
        String carNoKey;
        ClsBusInfo clsBusInfo;

        Set<String> set_route = MapCacheManager.cacheMapSubRouteByRouteID.keySet();
        Iterator itorRoute1 = set_route.iterator();
        Iterator itorRoute2 = set_route.iterator();
        Iterator itorRoute3 = set_route.iterator();
        String routeKey;
        ClsRouteInfo clsRouteInfo;

        List list = new ArrayList();

        int i = 0;

        while (itorRoute2.hasNext()){//先取相等的
            routeKey =(String)itorRoute2.next();
            clsRouteInfo = MapCacheManager.cacheMapSubRouteByRouteID.get(routeKey);
            if(clsRouteInfo.getRouteName().equals(inputUpStr.toString())){
                if(i>=10){
                    break;
                }
                Map<String,String> map = new HashMap<>();
                map.put("inputName",String.valueOf(clsRouteInfo.getRouteName()==null?"":clsRouteInfo.getRouteName()));
                map.put("inputId",String.valueOf(clsRouteInfo.getRouteID()==null?"":clsRouteInfo.getRouteID()));
                map.put("inputType","1");//类型1:线路
                map.put("parentOrgName",String.valueOf(clsRouteInfo.getParentOrgName()==null?"":clsRouteInfo.getParentOrgName()));
                list.add(map);
                i++;
            }
        }
        while (itorCarNo2.hasNext()){//先取相等的
            carNoKey =(String)itorCarNo2.next();
            clsBusInfo = MapCacheManager.cacheMapBusInfoByBusId.get(carNoKey);
            if(clsBusInfo.getBusCardNo().equals(inputUpStr.toString())){
                if(i>=10){
                    break;
                }
                Map<String,String> map = new HashMap<>();
                map.put("inputName",String.valueOf(clsBusInfo.getBusCardNo()==null?"":clsBusInfo.getBusCardNo()));
                map.put("inputId",String.valueOf(clsBusInfo.getBusID()==null?"":clsBusInfo.getBusID()));
                map.put("inputType","2");//类型2：车牌号
                map.put("parentOrgName",String.valueOf(clsBusInfo.getParentOrgName()==null?"":clsBusInfo.getParentOrgName()));
                list.add(map);
                i++;
            }
        }

        while (itorRoute3.hasNext()){//再取右模糊
            routeKey =(String)itorRoute3.next();
            clsRouteInfo = MapCacheManager.cacheMapSubRouteByRouteID.get(routeKey);
            if(clsRouteInfo.getRouteName().indexOf(inputUpStr.toString())==0 && !clsRouteInfo.getRouteName().equals(inputUpStr.toString())){
                if(i>=10){
                    break;
                }
                Map<String,String> map = new HashMap<>();
                map.put("inputName",String.valueOf(clsRouteInfo.getRouteName()==null?"":clsRouteInfo.getRouteName()));
                map.put("inputId",String.valueOf(clsRouteInfo.getRouteID()==null?"":clsRouteInfo.getRouteID()));
                map.put("inputType","1");//类型1:线路
                map.put("parentOrgName",String.valueOf(clsRouteInfo.getParentOrgName()==null?"":clsRouteInfo.getParentOrgName()));
                list.add(map);
                i++;
            }
        }
        while (itorCarNo5.hasNext()){//再取右模糊  车1
            carNoKey =(String)itorCarNo5.next();
            clsBusInfo = MapCacheManager.cacheMapBusInfoByBusId.get(carNoKey);
            if(clsBusInfo.getBusCardNo().indexOf(inputUpStr.toString()) == 0 && !clsBusInfo.getBusCardNo().equals(inputUpStr.toString())){
                if(i>=10){
                    break;
                }
                Map<String,String> map = new HashMap<>();
                map.put("inputName",String.valueOf(clsBusInfo.getBusCardNo()==null?"":clsBusInfo.getBusCardNo()));
                map.put("inputId",String.valueOf(clsBusInfo.getBusID()==null?"":clsBusInfo.getBusID()));
                map.put("inputType","2");//类型2：站点
                map.put("parentOrgName",String.valueOf(clsBusInfo.getParentOrgName()==null?"":clsBusInfo.getParentOrgName()));
                list.add(map);
                i++;
            }
        }
        while (itorCarNo4.hasNext()){//再取右模糊  车2
            carNoKey =(String)itorCarNo4.next();
            clsBusInfo = MapCacheManager.cacheMapBusInfoByBusId.get(carNoKey);
            if(clsBusInfo.getBusCardNo().indexOf(inputUpStr.toString()) == 1 && !clsBusInfo.getBusCardNo().equals(inputUpStr.toString())){
                if(i>=10){
                    break;
                }
                Map<String,String> map = new HashMap<>();
                map.put("inputName",String.valueOf(clsBusInfo.getBusCardNo()==null?"":clsBusInfo.getBusCardNo()));
                map.put("inputId",String.valueOf(clsBusInfo.getBusID()==null?"":clsBusInfo.getBusID()));
                map.put("inputType","2");//类型2：站点
                map.put("parentOrgName",String.valueOf(clsBusInfo.getParentOrgName()==null?"":clsBusInfo.getParentOrgName()));
                list.add(map);
                i++;
            }
        }
        while (itorCarNo3.hasNext()){//再取右模糊  车3
            carNoKey =(String)itorCarNo3.next();
            clsBusInfo = MapCacheManager.cacheMapBusInfoByBusId.get(carNoKey);
            if(clsBusInfo.getBusCardNo().indexOf(inputUpStr.toString()) == 2 && !clsBusInfo.getBusCardNo().equals(inputUpStr.toString())){
                if(i>=10){
                    break;
                }
                Map<String,String> map = new HashMap<>();
                map.put("inputName",String.valueOf(clsBusInfo.getBusCardNo()==null?"":clsBusInfo.getBusCardNo()));
                map.put("inputId",String.valueOf(clsBusInfo.getBusID()==null?"":clsBusInfo.getBusID()));
                map.put("inputType","2");//类型2：站点
                map.put("parentOrgName",String.valueOf(clsBusInfo.getParentOrgName()==null?"":clsBusInfo.getParentOrgName()));
                list.add(map);
                i++;
            }
        }

        while (itorRoute1.hasNext()){//再取相似的
            routeKey =(String)itorRoute1.next();
            clsRouteInfo = MapCacheManager.cacheMapSubRouteByRouteID.get(routeKey);
            if(clsRouteInfo.getRouteName().contains(inputUpStr.toString()) && !clsRouteInfo.getRouteName().equals(inputUpStr.toString()) &&
                    clsRouteInfo.getRouteName().indexOf(inputUpStr.toString())>0){
                if(i>=10){
                    break;
                }
                Map<String,String> map = new HashMap<>();
                map.put("inputName",String.valueOf(clsRouteInfo.getRouteName()==null?"":clsRouteInfo.getRouteName()));
                map.put("inputId",String.valueOf(clsRouteInfo.getRouteID()==null?"":clsRouteInfo.getRouteID()));
                map.put("inputType","1");//类型1:线路
                map.put("parentOrgName",String.valueOf(clsRouteInfo.getParentOrgName()==null?"":clsRouteInfo.getParentOrgName()));
                list.add(map);
                i++;
            }
        }
//        List<Map<String,String>> list = new ArrayList<>();
        while (itorCarNo1.hasNext()){
            carNoKey =(String)itorCarNo1.next();
            clsBusInfo = MapCacheManager.cacheMapBusInfoByBusId.get(carNoKey);
            if(clsBusInfo.getBusCardNo().contains(inputUpStr.toString()) && !clsBusInfo.getBusCardNo().equals(inputUpStr.toString()) &&
                    clsBusInfo.getBusCardNo().indexOf(inputUpStr.toString()) > 2){
                if(i>=10){
                    break;
                }
                Map<String,String> map = new HashMap<>();
                map.put("inputName",String.valueOf(clsBusInfo.getBusCardNo()==null?"":clsBusInfo.getBusCardNo()));
                map.put("inputId",String.valueOf(clsBusInfo.getBusID()==null?"":clsBusInfo.getBusID()));
                map.put("inputType","2");//类型2：站点
                map.put("parentOrgName",String.valueOf(clsBusInfo.getParentOrgName()==null?"":clsBusInfo.getParentOrgName()));
                list.add(map);
                i++;
            }
        }
        return list;
    }