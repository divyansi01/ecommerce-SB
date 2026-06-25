package com.ecommerce.service;

public interface PerformanceTestingService {
    long measureExecutionTime(Runnable task);
    void logPerformance(String operation, long duration);
}
