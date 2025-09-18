package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    // 定义切入点
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){}

    // 定义前置通知
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) throws NoSuchMethodException {
        log.info("开始进行数据填充");

        // 1.获取方法的注解
//        OperationType value = joinPoint.getTarget().getClass().getAnnotation(AutoFill.class).value();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        OperationType value = method.getAnnotation(AutoFill.class).value();


        // 2.获取方法的参数
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0){
            return;
        }
        Object object = args[0];

        // 3.准备赋值的数据
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        // 4.根据注解为对于的属性赋值
        if (value == OperationType.INSERT){
            // 插入操作
            // 通过反射获取方法并调用该方法为其赋值
            try {
                object.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class).invoke(object, now);
                object.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class).invoke(object, now);
                object.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class).invoke(object, currentId);
                object.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class).invoke(object, currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (value == OperationType.UPDATE){
            // 更新操作
            try {
                object.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class).invoke(object, now);
                object.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class).invoke(object, currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }


        } else {
            // 不支持的操作
            throw new RuntimeException("不支持的操作");
        }

    }
}
