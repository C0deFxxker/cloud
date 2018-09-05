
package com.lyl.study.cloud.base.lock;

import com.lyl.study.cloud.base.lock.annotation.ClusterLock;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * Redis分布式锁处理切片
 * 对所有带有 @RedisLock 注解的方法进行拦截
 * 方法执行前，自动获取锁资源，并在方法执行完毕后释放锁资源
 *
 * @author 黎毅麟
 */
@Aspect
@Order
public class RedisLockAware {
    private static Logger logger = LoggerFactory.getLogger(RedisLockAware.class);

    @Autowired(required = false)
    private RedisTemplate<String, String> redisTemplate;

    private final SpelExpressionParser spelExpressionParser = new SpelExpressionParser();

    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    public RedisLockAware() {
        logger.info("RedisLock开启...");
    }


    @Pointcut("@annotation(com.lyl.study.cloud.base.lock.annotation.ClusterLock)")
    private void allRedisLockAnnotationMethods() {
    }

    @Around("allRedisLockAnnotationMethods()")
    public Object lock(ProceedingJoinPoint joinPoint) throws Throwable {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        Object[] args = joinPoint.getArgs();
        Object target = joinPoint.getTarget();

        ClusterLock annotation = method.getAnnotation(ClusterLock.class);
        int duration = annotation.duration();
        int timeout = annotation.timeout();
        String spelExpression = annotation.value();

        // 根据SpEL表达式计算Key
        EvaluationContext evaluationContext = new MethodBasedEvaluationContext(target, method, args, this.parameterNameDiscoverer);
        String key = spelExpressionParser.parseExpression(spelExpression).getValue(evaluationContext, String.class);

        logger.info("开始申请锁资源 - " + key);

        RedisLock redisLock = new RedisLock(key, duration, redisTemplate);
        try {
            // 上锁
            redisLock.lock(timeout, TimeUnit.SECONDS);
            logger.info("获取锁资源成功 - " + key);
            // 执行方法
            return joinPoint.proceed(args);
        } finally {
            // 解锁
            logger.info("释放锁资源 - " + key);
            redisLock.unlock();
        }
    }
}
