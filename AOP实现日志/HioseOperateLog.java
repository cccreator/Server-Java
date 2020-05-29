package com.hisense.smartroad.hiose.web.controller.log.annotation;

import com.hisense.smartroad.hiose.web.controller.log.constant.LogBussType;
import com.hisense.smartroad.hiose.web.controller.log.constant.LogOperateType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HioseOperateLog {

    /**
     * 操作类型描述
     *
     * @return
     */
    String operateTypeDesc() default "";

    /**
     * 操作类型
     *
     * @return
     */
    LogOperateType operateType();

    /**
     * 模块编码
     *
     * @return
     */
    String moudleCode() default "";

    /**
     * 模块名称
     *
     * @return
     */
    String moudleName() default "XX模块";

    /**
     * 业务类型
     *
     * @return
     */
    LogBussType bussType();

    /**
     * 业务类型描述
     *
     * @return
     */
    LogBussType bussTypeDesc();
}

