package com.ecommerce.service.impl;

import com.ecommerce.service.PerformanceTestingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PerformanceTestingServiceImpl implements PerformanceTestingService {
    private static final Logger logger = LoggerFactory.getLogger(PerformanceTestingServiceImpl.class);

    @Override
    public long measureExecutionTime(Runnable task) {
        long startTime = System.nanoTime();
        task.run();
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1_000_000; // Convert to milliseconds
        return duration;
    }

    @Override
    public void logPerformance(String operation, long duration) {
        logger.info("Performance: {} took {} ms", operation, duration);
    }
}
