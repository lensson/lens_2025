package com.lens.blog.admin.annotion.OperationLogger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.lens.blog.xo.constant.RedisConstants;
import com.lens.blog.xo.constant.SysConstants;
import com.lens.common.base.constant.Constants;
import com.lens.common.core.security.SecurityUser;
import com.lens.common.core.utils.AspectUtil;
import com.lens.common.core.utils.IpUtils;
import com.lens.common.core.utils.StringUtils;
import com.lens.common.db.entity.ExceptionLog;
import com.lens.common.db.utils.AopUtils;
import com.lens.common.redis.utils.RedisUtil;
import com.lens.common.web.holder.RequestHolder;
import com.lens.common.web.utils.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
/**
 * 日志切面
 *
 * @author 陌溪
 * @date 2020年12月31日21:26:04
 */
@Aspect
@Component
@Slf4j
public class LoggerAspect {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    ThreadPoolTaskExecutor threadPoolTaskExecutor;

    /**
     * 开始时间
     */
    Date startTime;

    @Pointcut(value = "@annotation(operationLogger)")
    public void pointcut(OperationLogger operationLogger) {

    }

    @Around(value = "pointcut(operationLogger)")
    public Object doAround(ProceedingJoinPoint joinPoint, OperationLogger operationLogger) throws Throwable {

        startTime = new Date();

        //先执行业务
        Object result = joinPoint.proceed();

        try {
            // 日志收集
            handle(joinPoint);

        } catch (Exception e) {
            log.error("日志记录出错!", e);
        }

        return result;
    }


    @AfterThrowing(value = "pointcut(operationLogger)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, OperationLogger operationLogger, Throwable e) throws Exception {

        ExceptionLog exception = new ExceptionLog();
        HttpServletRequest request = RequestHolder.getRequest();
        String ip = IpUtils.getIpAddr(request);
        exception.setIp(ip);
        String operationName = AspectUtil.INSTANCE.parseParams(joinPoint.getArgs(), operationLogger.value());

        //从Redis中获取IP来源
        String jsonResult = redisUtil.get(RedisConstants.IP_SOURCE + Constants.SYMBOL_COLON + ip);
        if (StringUtils.isEmpty(jsonResult)) {
            String addresses = IpUtils.getAddresses(SysConstants.IP + SysConstants.EQUAL_TO + ip, SysConstants.UTF_8);
            if (StringUtils.isNotEmpty(addresses)) {
                exception.setIpSource(addresses);
                redisUtil.setEx(RedisConstants.IP_SOURCE + Constants.SYMBOL_COLON + ip, addresses, 24, TimeUnit.HOURS);
            }
        } else {
            exception.setIpSource(jsonResult);
        }

        //设置请求信息
        exception.setIp(ip);

        //设置调用的方法
        exception.setMethod(joinPoint.getSignature().getName());

        exception.setExceptionJson(JSON.toJSONString(e,
                SerializerFeature.DisableCircularReferenceDetect,
                SerializerFeature.WriteMapNullValue));
        exception.setExceptionMessage(e.getMessage());

        exception.setOperation(operationName);
        exception.setCreateTime(new Date());
        exception.setUpdateTime(new Date());

        exception.insert();
    }


    /**
     * 管理员日志收集
     *
     * @param point
     * @throws Exception
     */
    private void handle(ProceedingJoinPoint point) throws Exception {

        HttpServletRequest request = RequestHolder.getRequest();

        Method currentMethod = AspectUtil.INSTANCE.getMethod(point);

        //获取操作名称
        OperationLogger annotation = currentMethod.getAnnotation(OperationLogger.class);

        boolean save = annotation.save();

        String bussinessName = AspectUtil.INSTANCE.parseParams(point.getArgs(), annotation.value());

        String ua = RequestUtil.getUa();

        log.info("{} | {} - {} {} - {}", bussinessName, IpUtils.getIpAddr(request), RequestUtil.getMethod(), RequestUtil.getRequestUrl(), ua);
        if (!save) {
            return;
        }

        // 获取参数名称和值
        Map<String, Object> nameAndArgsMap = AopUtils.getFieldsName(point);
        // 当前操作用户
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String paramsJson = JSONObject.toJSONString(nameAndArgsMap);
        String type = request.getMethod();
        String ip = IpUtils.getIpAddr(request);
        String url = request.getRequestURI();

        // 异步存储日志
        threadPoolTaskExecutor.execute(
                new SysLogHandle(ip, type, url, securityUser,
                        paramsJson, point.getTarget().getClass().getName(),
                        point.getSignature().getName(), bussinessName,
                        startTime, redisUtil));
    }
}
