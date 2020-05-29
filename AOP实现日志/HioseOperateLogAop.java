package com.hisense.smartroad.hiose.web.controller.log.aop;

import com.hisense.hiose.entity.SystemLog;
import com.hisense.hiose.service.SystemLogService;
import com.hisense.smartroad.base.utils.UserUtils;
import com.hisense.smartroad.hiose.web.controller.log.annotation.HioseOperateLog;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.UUID;

@Aspect
@Component
public class HioseOperateLogAop {
    private static Logger logger = LoggerFactory.getLogger(HioseOperateLogAop.class);

    @Autowired
    private SystemLogService systemLogService;

    HttpServletRequest request = null;
    ThreadLocal<Long> time = new ThreadLocal<>();
    //生成操作日志的唯一标识
    public static ThreadLocal<String> tag = new ThreadLocal<>();

    //声明AOP切入点，凡是使用了HioseOperateLog的方法均被拦截
    @Pointcut("@annotation(com.hisense.smartroad.hiose.web.controller.log.annotation.HioseOperateLog)")
    public void log() {
        System.out.println("我是一个切入点");
    }

    /**
     * 在所有标注@Log的地方切入
     *
     * @param joinPoint
     */
    @Before("log()")
    public void beforeExec(JoinPoint joinPoint) {
        time.set(System.currentTimeMillis());
        info(joinPoint);
        //设置日志记录的唯一标识号
        tag.set(UUID.randomUUID().toString().replace("-", ""));
        request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    @After("log()")
    public void afterExec(JoinPoint joinPoint) {
        MethodSignature ms = (MethodSignature) joinPoint.getSignature();
        Method method = ms.getMethod();
        logger.debug("标记为" + tag.get() + "的方法" + method.getName()
                + "运行消耗" + (System.currentTimeMillis() - time.get()) + "ms");
        time.remove();
    }

    //在执行目标方法的过程中，会执行这个方法，可以在这里实现日志的记录
    @Around("log()")
    public Object aroundExec(ProceedingJoinPoint pjp) throws Throwable {
        Object ret = pjp.proceed();
        try {
            Object[] orgs = pjp.getArgs();
            SystemLog log = null;
            for (int i = 0; i < orgs.length; i++) {
                if (orgs[i] instanceof SystemLog) {
                    log = (SystemLog) orgs[i];
                }
            }
            if (log == null) {
                log = new SystemLog();
            }
            if (log != null && request != null) {

                MethodSignature ms = (MethodSignature) pjp.getSignature();
                Method method = ms.getMethod();
                //获取注解的操作日志信息
                HioseOperateLog logAnn = method.getAnnotation(HioseOperateLog.class);

                String businessType = logAnn.bussType().getName();
                String businessDesc = logAnn.bussTypeDesc().getDesc();

                log.setBussType(businessType);
                log.setBussTypeDesc(businessDesc);

                log.setMoudleCode(logAnn.moudleCode());
                log.setMoudleName(logAnn.moudleName());
                log.setOperateType(logAnn.operateType().getName());
                log.setUserId(UserUtils.getUser().getUserId());
                log.setOperateTypeDesc(logAnn.operateTypeDesc());
                log.setRequestIp(getRemoteHost(request));
                log.setRequestUrl(request.getRequestURI());
                log.setServerIp(request.getLocalAddr());
                log.setLogDate(new Date());
                log.setUserName(UserUtils.getUser().getUserName());

                log.setLogId(tag.get());
                //保存操作日志
                systemLogService.save(log);
            } else {
                logger.info("不记录日志信息");
            }
            //保存操作结果
        } catch (Exception e) {
            //e.printStackTrace();
        } finally {
            tag.remove();
        }
        return ret;
    }

    //记录异常日志
    @AfterThrowing(pointcut = "log()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Throwable e) {
        try {
            info(joinPoint);
            Object[] orgs = joinPoint.getArgs();
            SystemLog valueReturn = null;
            for (int i = 0; i < orgs.length; i++) {
                if (orgs[i] instanceof SystemLog) {
                    valueReturn = (SystemLog) orgs[i];
                }
            }
            if (valueReturn == null) {
                valueReturn = new SystemLog();
            }
            if (valueReturn != null && request != null) {
                MethodSignature ms = (MethodSignature) joinPoint.getSignature();
                Method method = ms.getMethod();
                HioseOperateLog log = method.getAnnotation(HioseOperateLog.class);
                String businessType = log.bussType().getName();
                String businessDesc = log.bussTypeDesc().getDesc();
                valueReturn.setBussType(businessType);
                valueReturn.setBussTypeDesc(businessDesc);
                valueReturn.setMoudleCode(log.moudleCode());
                valueReturn.setMoudleName(log.moudleName());
                valueReturn.setOperateType(log.operateType().getName());
                valueReturn.setOperateTypeDesc(log.operateTypeDesc());
                valueReturn.setUserId(UserUtils.getUser().getUserId());
                String errMes = e.getMessage();
                if (errMes != null && errMes.length() > 800) {
                    errMes = errMes.substring(0, 800);
                }
                valueReturn.setErrMsg(errMes);
                valueReturn.setRequestIp(getRemoteHost(request));
                valueReturn.setRequestUrl(request.getRequestURI());
                valueReturn.setServerIp(request.getLocalAddr());
                valueReturn.setLogId(tag.get());
                valueReturn.setLogDate(new Date());
                valueReturn.setUserName(UserUtils.getUser().getUserName());
                systemLogService.save(valueReturn);
            } else {
                logger.info("不记录日志信息");
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private void info(JoinPoint joinPoint) {
        logger.debug("--------------------------------------------------");
        logger.debug("King:\t" + joinPoint.getKind());
        logger.debug("Target:\t" + joinPoint.getTarget().toString());
        Object[] os = joinPoint.getArgs();
        logger.debug("Args:");
        for (int i = 0; i < os.length; i++) {
            logger.debug("\t==>参数[" + i + "]:\t" + os[i].toString());
        }
        logger.debug("Signature:\t" + joinPoint.getSignature());
        logger.debug("SourceLocation:\t" + joinPoint.getSourceLocation());
        logger.debug("StaticPart:\t" + joinPoint.getStaticPart());
        logger.debug("--------------------------------------------------");
    }

    /**
     * 获取远程客户端Ip
     *
     * @param request
     * @return
     */
    private String getRemoteHost(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null) {
            return ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
        } else {
            logger.info("ip--"+ip);
            return ip;
        }

    }
}