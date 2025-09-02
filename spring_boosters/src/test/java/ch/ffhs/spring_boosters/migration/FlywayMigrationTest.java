package ch.ffhs.spring_boosters.migration;

import ch.ffhs.spring_boosters.TestcontainersConfiguration;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
class FlywayMigrationTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldApplyMigrationsSuccessfully() {
        // Given Flyway configuration
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .load();

        // When getting migration info
        var migrationInfos = flyway.info().all();

        // Then should have our migration
        assertThat(migrationInfos).hasSize(1);
        assertThat(migrationInfos[0].getDescription()).isEqualTo("Create immunization schema");
        assertThat(migrationInfos[0].getState().isApplied()).isTrue();
    }

    @Test
    void shouldCreateAllTablesFromMigration() {
        // When checking for tables in the database
        List<String> tables = jdbcTemplate.queryForList(
                "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'",
                String.class
        );

        // Then all expected tables should exist
        assertThat(tables).containsExactlyInAnyOrder(
                "users",
                "vaccine_type",
                "active_substance",
                "age_category",
                "vaccine_type_active_substance",
                "immunization_plan",
                "immunization_plan_series",
                "follow_up_rule",
                "immunization_record",
                "flyway_schema_history"
        );
    }

    @Test
    void shouldCreateProperIndexes() {
        // When checking for indexes
        List<Map<String, Object>> indexes = jdbcTemplate.queryForList(
                "SELECT indexname FROM pg_indexes WHERE schemaname = 'public' AND indexname LIKE 'idx_%'"
        );

        // Then should have our performance indexes
        assertThat(indexes).hasSizeGreaterThan(5);

        List<String> indexNames = indexes.stream()
                .map(row -> (String) row.get("indexname"))
                .toList();

        assertThat(indexNames).contains(
                "idx_users_username",
                "idx_vaccine_type_name",
                "idx_active_substance_name"
        );
    }

    @Test
    void shouldHaveProperForeignKeyConstraints() {
        // When checking foreign key constraints
        List<Map<String, Object>> constraints = jdbcTemplate.queryForList(
                """
                SELECT 
                    tc.constraint_name,
                    tc.table_name,
                    kcu.column_name,
                    ccu.table_name AS foreign_table_name,
                    ccu.column_name AS foreign_column_name
                FROM information_schema.table_constraints AS tc
                JOIN information_schema.key_column_usage AS kcu 
                    ON tc.constraint_name = kcu.constraint_name
                JOIN information_schema.constraint_column_usage AS ccu 
                    ON ccu.constraint_name = tc.constraint_name
                WHERE tc.constraint_type = 'FOREIGN KEY'
                    AND tc.table_schema = 'public'
                """
        );

        // Then should have foreign key relationships
        assertThat(constraints).hasSizeGreaterThan(5);

        // Check specific relationships exist
        boolean hasImmunizationPlanToVaccineType = constraints.stream()
                .anyMatch(row ->
                    "immunization_plan".equals(row.get("table_name")) &&
                    "vaccine_type".equals(row.get("foreign_table_name"))
                );
        assertThat(hasImmunizationPlanToVaccineType).isTrue();
    }

    @Test
    void shouldHaveUuidColumnsWithDefaults() {
        // When checking UUID columns with defaults
        List<Map<String, Object>> uuidColumns = jdbcTemplate.queryForList(
                """
                SELECT 
                    table_name, 
                    column_name, 
                    data_type, 
                    column_default
                FROM information_schema.columns 
                WHERE table_schema = 'public' 
                    AND column_name = 'id' 
                    AND data_type = 'uuid'
                """
        );

        // Then should have UUID columns with proper defaults
        assertThat(uuidColumns).hasSizeGreaterThan(5);

        // Verify specific tables have UUID primary keys
        List<String> tablesWithUuid = uuidColumns.stream()
                .map(row -> (String) row.get("table_name"))
                .toList();

        assertThat(tablesWithUuid).contains(
                "users",
                "vaccine_type",
                "active_substance",
                "age_category"
        );
    }
}
