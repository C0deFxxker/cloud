package com.lyl.study.cloud.base.log;

import com.lyl.study.cloud.base.util.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Aspect
@Component
public class CommonLoggerAspect {
    private Map<String, Class<?>> classMap = new HashMap<>();
    private Map<Method, String> methodNameCaches = new HashMap<>();

    public CommonLoggerAspect() {
        log.info("启用统一日志处理切片");
    }

    // 切所有dubbo以及web接口
    @Pointcut("execution(public * com.lyl.study.cloud..*Facade.*(..)) || execution(public * com.lyl.study.cloud..*Controller.*(..))")
    public void pointCut() {
    }

    // 环绕通知
    @Around("pointCut()")
    public Object aroundLog(ProceedingJoinPoint joinpoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinpoint.getSignature();
        Method method = signature.getMethod();
        String methodName = getMethodName(method);
        NoLog annotation = method.getAnnotation(NoLog.class);

        log.info("开始调用方法: {}", methodName);

        Object result;
        try {
            if (annotation == null || !annotation.params()) {
                logParameters(methodName, joinpoint.getArgs());
            }

            long start = System.currentTimeMillis();
            result = joinpoint.proceed(joinpoint.getArgs());
            long end = System.currentTimeMillis();

            log.info("方法[{}]执行时间: {}", methodName, (end - start) + " ms");

            if (annotation == null || !annotation.result()) {
                log.info("方法[{}]返回值: {}", methodName, result);
            }
        } catch (Throwable e) {
            log.error("方法[{}]抛出了异常: {}", methodName, e.toString());
            throw e;
        }
        return result;
    }

    private void logParameters(String methodName, Object[] args) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Object each : args) {
            /*
                特殊处理一些类参数
                使用字符串的类名为了防止项目没有引入相应类的包而引发NoClassDefFoundError
             */
            if (isInstanceOf(each, "javax.servlet.ServletRequest")
                    || isInstanceOf(each, "javax.servlet.ServletResponse")
                    || isInstanceOf(each, "javax.servlet.http.HttpSession")
                    || isInstanceOf(each, "org.springframework.ui.Model")
                    || isInstanceOf(each, "org.springframework.web.servlet.View")
                    || isInstanceOf(each, "org.springframework.web.servlet.ModelAndView")) {
                stringBuilder.append("[").append(each.getClass()).append("]").append(", ");
            } else {
                stringBuilder.append(each.toString()).append(", ");
            }
        }
        if (stringBuilder.length() > 0) {
            stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
        }
        log.info("方法[{}]传入参数: {}", methodName, stringBuilder.toString());
    }

    /**
     * 判断对象实例是否属于某个类的实例
     * 使用字符串的类名为了防止项目没有引入相应类的包而引发NoClassDefFoundError
     *
     * @param instance  实例
     * @param className 类名
     * @return 是/否属于className类的实例
     */
    private boolean isInstanceOf(Object instance, String className) {
        if (classMap.containsKey(className)) {
            Class<?> clazz = classMap.get(className);
            if (clazz != null) {
                return clazz.isInstance(instance);
            } else {
                return false;
            }
        } else {
            try {
                Class<?> clazz = Class.forName(className);
                classMap.put(className, clazz);
                return clazz.isInstance(instance);
            } catch (ClassNotFoundException e) {
                classMap.put(className, null);
                return false;
            }
        }
    }

    /**
     * 获取修正过的方法名
     *
     * @param method 方法
     * @return 修正过的方法名
     */
    private String getMethodName(Method method) {
        if (methodNameCaches.containsKey(method)) {
            return methodNameCaches.get(method);
        }

        String returnType = method.getReturnType().getSimpleName();
        String name = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();

        StringBuilder stringBuilder = new StringBuilder();
        String[] methodParamNames = null;
        try {
            methodParamNames = ReflectionUtils.getMethodParamNames(method);
        } catch (IOException ignored) {
            // 什么都不做
        }

        for (int i = 0; i < parameterTypes.length; i++) {
            stringBuilder.append(parameterTypes[i].getSimpleName());
            // 填充参数名称
            if (methodParamNames != null) {
                stringBuilder.append(" ").append(methodParamNames[i]);
            }
            stringBuilder.append(", ");
        }

        if (stringBuilder.length() > 0) {
            stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
        }
        String className = getSimpleClassName(method.getDeclaringClass());

        String methodName = returnType + " " + className + "." + name + "(" + stringBuilder.toString() + ")";
        methodNameCaches.put(method, methodName);
        return methodName;
    }

    private String getSimpleClassName(Class<?> clazz) {
        String className = clazz.toString();
        int len = className.length();
        if (len >= 40) {
            String[] tokens = StringUtils.tokenizeToStringArray(className, ".");
            for (int i = 0; i < tokens.length - 1; i++) {
                String token = tokens[i];
                if (len < 40) break;
                int tokenLength = token.length();
                String firstCase = token.substring(0, 1);
                tokens[i] = firstCase;
                len -= tokenLength - 1;
            }
            return StringUtils.arrayToDelimitedString(tokens, ".");
        }
        return className;
    }
}