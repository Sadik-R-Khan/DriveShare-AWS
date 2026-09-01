package com.example.demo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Component
@RequiredArgsConstructor
public class DatabaseSchemaRepair implements ApplicationRunner {

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        if (!isMySql()) {
            return;
        }
        repairCarsIdAutoIncrement();
    }

    private boolean isMySql() {
        try (Connection connection = dataSource.getConnection()) {
            String productName = connection.getMetaData().getDatabaseProductName();
            return productName != null && productName.toLowerCase().contains("mysql");
        } catch (Exception ex) {
            return false;
        }
    }

    private void repairCarsIdAutoIncrement() {
        try {
            Integer count = jdbcTemplate.queryForObject("""
                    SELECT COUNT(*)
                    FROM information_schema.columns
                    WHERE table_schema = DATABASE()
                      AND table_name = 'cars'
                      AND column_name = 'id'
                      AND extra NOT LIKE '%auto_increment%'
                    """, Integer.class);

            if (count != null && count > 0) {
                jdbcTemplate.execute("ALTER TABLE cars MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT");
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Could not repair cars.id auto-increment column", ex);
        }
    }
}
