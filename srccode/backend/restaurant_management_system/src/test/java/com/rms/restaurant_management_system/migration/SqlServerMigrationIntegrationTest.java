package com.rms.restaurant_management_system.migration;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.core.io.ClassPathResource;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
@Testcontainers
@EnabledIfEnvironmentVariable(named = "RUN_SQLSERVER_TESTS", matches = "(?i)true")
class SqlServerMigrationIntegrationTest {

    @Container
    static final MSSQLServerContainer<?> SQL_SERVER = new MSSQLServerContainer<>(
            DockerImageName.parse("mcr.microsoft.com/mssql/server:2022-CU14-ubuntu-22.04"))
            .acceptLicense();

    @Test
    void hardeningMigrationsAreRerunnableOnCleanBaseline() throws Exception {
        try (Connection connection = SQL_SERVER.createConnection("")) {
            createBaseline(connection);

            executeClasspathScript(connection, "db_migration_uc01_uc04_uc05_uc06.sql");
            executeClasspathScript(connection, "db_migration_backend_security_coordination.sql");
            executeClasspathScript(connection, "db_migration_uc01_uc04_uc05_uc06.sql");
            executeClasspathScript(connection, "db_migration_backend_security_coordination.sql");

            assertThat(columnExists(connection, "users", "token_version")).isTrue();
            assertThat(columnExists(connection, "reservations", "assigned_table_id")).isTrue();
            assertThat(columnExists(connection, "reservations", "start_at")).isTrue();
            assertThat(columnExists(connection, "restaurant_tables", "version")).isTrue();
            assertThat(tableExists(connection, "refresh_tokens")).isTrue();
            assertThat(tableExists(connection, "security_audit_logs")).isTrue();
            assertThat(tableExists(connection, "password_reset_tokens")).isTrue();
            assertThat(indexExists(connection, "reservations", "ux_reservations_created_order_id")).isTrue();
        }
    }

    @Test
    void failedMigrationRollsBackItsOwnChanges() throws Exception {
        try (Connection connection = SQL_SERVER.createConnection("");
             Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE IF EXISTS password_reset_tokens");
            statement.execute("DROP TABLE IF EXISTS reservations");
            statement.execute("DROP TABLE IF EXISTS restaurant_tables");
            statement.execute("CREATE TABLE reservations (reservation_id BIGINT IDENTITY PRIMARY KEY, status VARCHAR(30))");
            statement.execute("CREATE TABLE restaurant_tables (table_id BIGINT IDENTITY PRIMARY KEY, capacity INT NULL)");
            statement.execute("INSERT INTO restaurant_tables(capacity) VALUES (NULL)");

            boolean failed = false;
            try {
                executeClasspathScript(connection, "db_migration_backend_security_coordination.sql");
            } catch (Exception expected) {
                failed = true;
            }

            assertThat(failed).isTrue();
            assertThat(tableExists(connection, "password_reset_tokens")).isFalse();
            assertThat(columnExists(connection, "reservations", "assigned_table_id")).isFalse();
        }
    }

    @Test
    void rowLockSerializesConcurrentTableUpdates() throws Exception {
        try (Connection setup = SQL_SERVER.createConnection("")) {
            createBaseline(setup);
        }

        try (Connection first = SQL_SERVER.createConnection("");
             Connection second = SQL_SERVER.createConnection("")) {
            first.setAutoCommit(false);
            second.setAutoCommit(false);

            try (Statement lock = first.createStatement()) {
                lock.executeQuery("SELECT table_id FROM restaurant_tables WITH (UPDLOCK, ROWLOCK) WHERE table_id = 1")
                        .close();
            }

            try (Statement competingUpdate = second.createStatement()) {
                competingUpdate.setQueryTimeout(2);
                assertThatThrownBySql(() -> competingUpdate.executeUpdate(
                        "UPDATE restaurant_tables SET capacity = 8 WHERE table_id = 1"));
            }

            first.rollback();
            try (Statement retry = second.createStatement()) {
                assertThat(retry.executeUpdate("UPDATE restaurant_tables SET capacity = 8 WHERE table_id = 1"))
                        .isEqualTo(1);
            }
            second.rollback();
        }
    }

    private void createBaseline(Connection connection) throws Exception {
        try (Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE IF EXISTS refresh_tokens");
            statement.execute("DROP TABLE IF EXISTS security_audit_logs");
            statement.execute("DROP TABLE IF EXISTS password_reset_tokens");
            statement.execute("DROP TABLE IF EXISTS reservations");
            statement.execute("DROP TABLE IF EXISTS restaurant_tables");
            statement.execute("DROP TABLE IF EXISTS users");
            statement.execute("CREATE TABLE users (user_id BIGINT IDENTITY PRIMARY KEY)");
            statement.execute("CREATE TABLE reservations (reservation_id BIGINT IDENTITY PRIMARY KEY, status VARCHAR(30))");
            statement.execute("CREATE TABLE restaurant_tables (table_id BIGINT IDENTITY PRIMARY KEY, capacity INT NOT NULL)");
            statement.execute("INSERT INTO restaurant_tables(capacity) VALUES (4)");
        }
    }

    private void executeClasspathScript(Connection connection, String resource) throws Exception {
        String sql = new ClassPathResource(resource).getContentAsString(StandardCharsets.UTF_8);
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    private boolean tableExists(Connection connection, String table) throws Exception {
        return scalarExists(connection, "SELECT 1 FROM sys.tables WHERE name = '" + table + "'");
    }

    private boolean columnExists(Connection connection, String table, String column) throws Exception {
        return scalarExists(connection, "SELECT 1 WHERE COL_LENGTH('" + table + "', '" + column + "') IS NOT NULL");
    }

    private boolean indexExists(Connection connection, String table, String index) throws Exception {
        return scalarExists(connection, "SELECT 1 FROM sys.indexes WHERE object_id = OBJECT_ID('" + table
                + "') AND name = '" + index + "'");
    }

    private boolean scalarExists(Connection connection, String sql) throws Exception {
        try (Statement statement = connection.createStatement(); ResultSet result = statement.executeQuery(sql)) {
            return result.next();
        }
    }

    private void assertThatThrownBySql(SqlOperation operation) {
        org.assertj.core.api.Assertions.assertThatThrownBy(operation::run)
                .isInstanceOf(SQLException.class);
    }

    @FunctionalInterface
    private interface SqlOperation {
        void run() throws SQLException;
    }
}
