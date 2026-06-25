package com.ecommerce.controller;

import com.ecommerce.model.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/cache")
@RequiredArgsConstructor
public class CacheMetricsController {

    private final CacheManager cacheManager;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCacheStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("cache_names", cacheManager.getCacheNames());
        stats.put("status", "Cache is active and running");
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<String>> clearCache() {
        cacheManager.getCacheNames().forEach(name ->
                cacheManager.getCache(name).clear()
        );
        return ResponseEntity.ok(
                ApiResponse.success("All caches cleared successfully")
        );
    }

    @DeleteMapping("/clear/{cacheName}")
    public ResponseEntity<ApiResponse<String>> clearSpecificCache(@PathVariable String cacheName) {
        var cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
            return ResponseEntity.ok(
                    ApiResponse.success("Cache " + cacheName + " cleared successfully")
            );
        }
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("Cache not found: " + cacheName));
    }
}