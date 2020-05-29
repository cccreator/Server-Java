package com.hisense.smartroad.hiose.web.controller.log.constant;

public enum LogBussType {

    LOGIN("系统登录", "系统登录"),
    DOC("综合业务管理系统", "综合业务管理系统"),
    ZHYWFXZSPT("综合业务分析展示平台", "综合业务分析展示平台"),
    YXJCYYJXT("运行监测与预警系统", "运行监测与预警系统"),
    HYJCFXXT("行业决策分析系统", "行业决策分析系统"),
    YWXTXT("业务协同系统", "业务协同系统"),
    YYKHXT("运营考核系统", "运营考核系统"),
    CZBT("财政补贴", "财政补贴"),
    YJCL("应急处理", "应急处理"),
    YJGL("应急管理", "应急管理"),
    YJZS("应急值守", "应急值守"),
    TEST("测试系统","测试系统"),
    THIRDLODIN("第三方登录验证","第三方登录验证"),
    YWGLXT("运维管理系统", "运维管理系统");

    private String name;
    private String desc;

    LogBussType(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
