package com.restaurant.health;

import com.restaurant.common.api.ApiResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DatabaseHealthController {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseHealthController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/db-check")
    public ApiResponse<Map<String, Object>> checkDatabaseConnection() {
        Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("database", "SQL Server");
        data.put("query", "SELECT 1");
        data.put("result", result);
        data.put("status", "CONNECTED");

        return ApiResponse.success("Database connection successful", data);
    }
}
