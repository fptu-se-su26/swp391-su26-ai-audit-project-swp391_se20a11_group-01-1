package com.restaurant.health;

import com.restaurant.common.api.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public ApiResponse<Map<String, Object>> health() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("application", "Restaurant Backend");
        data.put("status", "UP");
        data.put("message", "Backend project is running successfully");

        return ApiResponse.success("Application health check successful", data);
    }
}
