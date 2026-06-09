package com.restaurant.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseConnectionChecker implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;
    private final boolean databaseCheckEnabled;

    public DatabaseConnectionChecker(
            JdbcTemplate jdbcTemplate,
            @Value("${app.database-check.enabled:true}") boolean databaseCheckEnabled
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.databaseCheckEnabled = databaseCheckEnabled;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!databaseCheckEnabled) {
            System.out.println("Database startup check is disabled.");
            return;
        }

        Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        System.out.println("SQL Server connection successful. Test query result = " + result);
    }
}
