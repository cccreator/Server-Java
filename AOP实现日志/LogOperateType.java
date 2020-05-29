package com.hisense.smartroad.hiose.web.controller.log.constant;

public enum LogOperateType {
    LIST("主界面", "主界面描述"),
    LOGIN("登录", ""),
    QUERY("查询", ""),
    DELETE("删除", ""),
    ADD("增加", ""),
    UPDATE("修改", ""),
    EXPORT("导出", ""),
    PRINT("打印", ""),
    CHECK("审核", ""),
    GENERATE("生成数据", ""),
    REVIEW("复议","");

    private String name;
    private String desc;

    LogOperateType(String name, String desc) {
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
