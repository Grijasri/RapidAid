package com.rapidaid.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitingService {

    private final Map<String, List<Long>> requestTimesMap = new ConcurrentHashMap<>();
    private static final int MAX_REQUESTS = 5;
    private static final long TIME_WINDOW_MS = 10 * 60 * 1000; // 10 minutes

    public boolean allowRequest(String clientIp) {
        long now = System.currentTimeMillis();
        requestTimesMap.putIfAbsent(clientIp, new ArrayList<>());
        List<Long> timestamps = requestTimesMap.get(clientIp);

        synchronized (timestamps) {
            // Remove timestamps older than 10 minutes
            timestamps.removeIf(time -> now - time > TIME_WINDOW_MS);
            if (timestamps.size() >= MAX_REQUESTS) {
                return false;
            }
            timestamps.add(now);
            return true;
        }
    }
}
