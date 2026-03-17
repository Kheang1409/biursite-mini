package com.biursite.config;

import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;

@Component
public class DatabaseStartupChecker implements CommandLineRunner {
    private final DataSource dataSource;
    private final Logger log = LoggerFactory.getLogger(DatabaseStartupChecker.class);

    public DatabaseStartupChecker(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) {
        log.info("Performing startup DB connectivity check...");
        try (Connection c = dataSource.getConnection()) {
            String url = c.getMetaData().getURL();
            String user = c.getMetaData().getUserName();
            log.info("Successfully obtained JDBC connection. URL={}, user={}", url, user);
        } catch (SQLException e) {
            log.error("Failed to obtain JDBC connection at startup: {}", e.toString());
            log.debug("DB connection exception", e);
            // Fail fast so local runs immediately report the root cause
            System.exit(1);
        }
    }
}
