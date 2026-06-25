package com.ecommerce.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PerformanceMonitoringAspect {
    private static final Logger logger = LoggerFactory.getLogger(PerformanceMonitoringAspect.class);
    /**
     * Monitor performance of all service methods
     * Measures execution time of every method in *Service classes
     */
    @Around("execution(* com.ecommerce.service.*Service.*(..))")
    public Object monitorPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        // Get method and class details
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        logger.info("🚀 Starting: {}.{}", className, methodName);

        try {
            // Execute the actual method
            Object result = joinPoint.proceed();

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            // Log based on duration
            if (duration > 1000) {
                logger.warn("⚠️  SLOW: {}.{} took {}ms", className, methodName, duration);
            } else if (duration > 100) {
                logger.info("✓ {}.{} completed in {}ms", className, methodName, duration);
            } else {
                logger.debug("⚡ {}.{} completed in {}ms", className, methodName, duration);
            }

            return result;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            logger.error("❌ ERROR in {}.{} after {}ms: {}", className, methodName, duration, e.getMessage());
            throw e;
        }
    }
}