package com.practice.student_management.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger =
            LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("execution(* com.practice.student_management.StudentService.*(..))")
    public void studentServiceMethods() {
    }

    @Around("studentServiceMethods()")
    public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {

        String methodName = joinPoint.getSignature().getName();

        long start = System.currentTimeMillis();

        logger.info("Entering method: {}", methodName);

        try {

            Object result = joinPoint.proceed();

            long time = System.currentTimeMillis() - start;

            logger.info(
                    "Method {} completed successfully in {} ms",
                    methodName,
                    time
            );

            return result;

        } catch (Throwable ex) {

            long time = System.currentTimeMillis() - start;

            logger.error(
                    "Method {} failed after {} ms",
                    methodName,
                    time
            );

            throw ex;
        }
    }
}