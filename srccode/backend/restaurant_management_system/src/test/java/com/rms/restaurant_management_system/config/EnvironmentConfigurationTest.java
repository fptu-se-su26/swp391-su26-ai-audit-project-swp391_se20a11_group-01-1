package com.rms.restaurant_management_system.config;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class EnvironmentConfigurationTest {

    @Test
    void commonConfigurationContainsNoCredentials() throws IOException {
        Properties properties = load("application.properties");

        assertThat(properties).doesNotContainKeys(
                "spring.datasource.url",
                "spring.datasource.username",
                "spring.datasource.password",
                "app.jwt-secret",
                "payos.client-id",
                "payos.api-key",
                "payos.checksum-key");
    }

    @Test
    void productionSecretsHaveNoFallbackValues() throws IOException {
        Properties properties = load("application-prod.properties");

        assertThat(properties.getProperty("spring.datasource.username")).isEqualTo("${DB_USERNAME}");
        assertThat(properties.getProperty("spring.datasource.password")).isEqualTo("${DB_PASSWORD}");
        assertThat(properties.getProperty("app.jwt-secret")).isEqualTo("${JWT_SECRET}");
        assertThat(properties.getProperty("payos.client-id")).isEqualTo("${PAYOS_CLIENT_ID}");
        assertThat(properties.getProperty("payos.api-key")).isEqualTo("${PAYOS_API_KEY}");
        assertThat(properties.getProperty("payos.checksum-key")).isEqualTo("${PAYOS_CHECKSUM_KEY}");
        assertThat(properties.getProperty("spring.jpa.hibernate.ddl-auto")).isEqualTo("validate");
    }

    @Test
    void localTemplateReferencesEnvironmentForEverySecret() throws IOException {
        Properties properties = load("application-local.example.properties");

        assertThat(properties.getProperty("spring.datasource.password")).isEqualTo("${DB_PASSWORD}");
        assertThat(properties.getProperty("app.jwt-secret")).isEqualTo("${JWT_SECRET}");
        assertThat(properties.getProperty("spring.mail.password")).isEqualTo("${MAIL_PASSWORD}");
        assertThat(properties.getProperty("payos.api-key")).isEqualTo("${PAYOS_API_KEY}");
        assertThat(properties.getProperty("payos.checksum-key")).isEqualTo("${PAYOS_CHECKSUM_KEY}");
    }

    private Properties load(String resourceName) throws IOException {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(resourceName)) {
            assertThat(input).as("classpath resource %s", resourceName).isNotNull();
            properties.load(input);
        }
        return properties;
    }
}
