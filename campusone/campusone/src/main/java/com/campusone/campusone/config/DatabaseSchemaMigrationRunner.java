package com.campusone.campusone.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabaseSchemaMigrationRunner implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        alterColumnToText("events", "banner_image");
        alterColumnToText("events", "description");
        alterColumnToText("events", "pdf_file");
        alterColumnToText("events", "venue");
    }

    private void alterColumnToText(String tableName, String columnName) {
        try {
            jdbcTemplate.execute("ALTER TABLE " + tableName + " ALTER COLUMN " + columnName + " TYPE TEXT");
            System.out.println(">>> DATABASE MIGRATION SUCCESS: " + tableName + "." + columnName + " altered to TEXT");
        } catch (Exception e) {
            System.out.println(">>> DATABASE MIGRATION INFO (" + tableName + "." + columnName + "): " + e.getMessage());
        }
    }
}
