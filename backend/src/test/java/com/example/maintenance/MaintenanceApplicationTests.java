package com.example.maintenance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class MaintenanceApplicationTests {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("Application context loads, Flyway runs V1 migration, and Hibernate validates schema")
    void contextLoadsAndSchemaIsValidated() throws SQLException {
        assertThat(dataSource).isNotNull();

        try (Connection connection = dataSource.getConnection()) {
            assertThat(connection.isValid(2)).isTrue();
        }

        // Verify that Flyway V1 migration executed and populated default categories
        Integer categoryCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM complaint_categories WHERE society_id IS NULL",
                Integer.class
        );
        assertThat(categoryCount).isNotNull();
        assertThat(categoryCount).isGreaterThanOrEqualTo(8);

        // Verify partial unique indexes exist
        Integer residencyIndexCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM pg_indexes WHERE indexname = 'uq_active_residency_per_unit'",
                Integer.class
        );
        assertThat(residencyIndexCount).isEqualTo(1);

        Integer complaintIndexCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM pg_indexes WHERE indexname = 'uq_one_active_complaint_per_apartment'",
                Integer.class
        );
        assertThat(complaintIndexCount).isEqualTo(1);
    }
}
