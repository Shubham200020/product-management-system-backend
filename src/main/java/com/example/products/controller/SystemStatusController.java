package com.example.products.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/system")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class SystemStatusController {

    @Autowired
    private DataSource dataSource;

    private static final long START_TIME = System.currentTimeMillis();

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getSystemStatus() {
        Map<String, Object> status = new HashMap<>();
        
        // 1. Check Database
        boolean dbConnected = false;
        try (Connection connection = dataSource.getConnection()) {
            dbConnected = connection.isValid(1);
        } catch (Exception e) {
            dbConnected = false;
        }
        
        // 2. Memory Usage
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
        long maxMemory = runtime.maxMemory() / 1024 / 1024;
        
        // 3. Uptime
        long uptimeSeconds = (System.currentTimeMillis() - START_TIME) / 1000;

        status.put("databaseConnected", dbConnected);
        status.put("memoryUsedMB", usedMemory);
        status.put("memoryMaxMB", maxMemory);
        status.put("uptimeSeconds", uptimeSeconds);
        status.put("apiStatus", "OPTIMAL");
        status.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(status);
    }
}
