package com.ecommerce.controller;

import com.ecommerce.model.ApiResponse;
import com.ecommerce.service.PerformanceTestingService;
import com.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/performance")
@RequiredArgsConstructor
public class PerformanceTestingController {

    private final ProductService productService;
    private final PerformanceTestingService performanceTestingService;

    @GetMapping("/test-caching")
    public ResponseEntity<ApiResponse<Map<String, Object>>> testCachingPerformance(
            @RequestParam(defaultValue = "100") int iterations) {

        Map<String, Object> results = new HashMap<>();

        // First call - Cache miss (database hit)
        long firstCallTime = performanceTestingService.measureExecutionTime(() ->
                productService.getAllActiveProducts()
        );

        // Subsequent calls - Cache hit
        long[] cachedTimes = new long[iterations];
        for (int i = 0; i < iterations; i++) {
            cachedTimes[i] = performanceTestingService.measureExecutionTime(() ->
                    productService.getAllActiveProducts()
            );
        }

        double avgCachedTime = java.util.Arrays.stream(cachedTimes).average().orElse(0);
        double improvement = ((double) firstCallTime - avgCachedTime) / firstCallTime * 100;

        results.put("first_call_time_ms", firstCallTime);
        results.put("avg_cached_call_time_ms", avgCachedTime);
        results.put("number_of_cached_calls", iterations);
        results.put("performance_improvement_percent", Math.round(improvement * 100.0) / 100.0);
        results.put("speedup_multiplier", Math.round((firstCallTime / avgCachedTime) * 100.0) / 100.0);

        return ResponseEntity.ok(
                ApiResponse.success(results, "Cache performance test completed")
        );
    }
}
