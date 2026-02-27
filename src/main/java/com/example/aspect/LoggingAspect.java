package com.example.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    @Pointcut("execution(* com.example.demo.service.*.*(..))")
    public void serviceMethods() {
    }

    @Around("serviceMethods()")
    public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        System.out.println(">>> [AOP Start]: Вызов метода " + methodName +
            " с аргументами: " + Arrays.toString(args));

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            System.err.println("!!! [AOP Error]: В методе " + methodName +
                " произошла ошибка: " + throwable.getMessage());
            throw throwable;
        }

        if (result != null) {
            System.out.println("<<< [AOP End]: Метод " + methodName +
                " завершен. Результат: " + result);
        } else {
            System.out.println("<<< [AOP End]: Метод " + methodName +
                " завершен (результат не ожидается/void)");
        }

        return result;
    }
}
