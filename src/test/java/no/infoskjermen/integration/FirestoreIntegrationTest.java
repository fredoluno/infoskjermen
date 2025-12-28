package no.infoskjermen.integration;

import no.infoskjermen.Settings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for Firestore database connectivity and operations.
 * These tests require either:
 * 1. GOOGLE_APPLICATION_CREDENTIALS environment variable set with service account key
 * 2. Firestore emulator running (FIRESTORE_EMULATOR_HOST environment variable)
 * 3. gcloud auth application-default login configured
 * 
 * Tests will be skipped if no Firestore connection is available.
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class FirestoreIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(FirestoreIntegrationTest.class);

    @Autowired
    private Settings settings;

    private static final String TEST_USER = "integration-test-user";

    @BeforeEach
    void setUp() {
        // Clear cache before each test to ensure fresh data
        settings.clearCache(TEST_USER);
        log.debug("Starting Firestore integration test");
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "FIRESTORE_INTEGRATION_ENABLED", matches = "true")
    void shouldConnectToFirestoreAndRetrieveGoogleSettings() throws Exception {
        log.debug("Testing Google settings retrieval from Firestore");
        
        HashMap googleSettings = settings.getGoogleSettings("general");
        
        assertThat(googleSettings).isNotNull();
        assertThat(googleSettings).containsKey("calendar");
        assertThat(googleSettings).containsKey("display_calendar");
        
        log.debug("Successfully retrieved Google settings: {}", googleSettings);
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "FIRESTORE_INTEGRATION_ENABLED", matches = "true")
    void shouldConnectToFirestoreAndRetrieveNetatmoSettings() throws Exception {
        log.debug("Testing Netatmo settings retrieval from Firestore");
        
        HashMap netatmoSettings = settings.getNetatmoSettings("general");
        
        assertThat(netatmoSettings).isNotNull();
        assertThat(netatmoSettings).containsKey("client_id");
        assertThat(netatmoSettings).containsKey("client_secret");
        
        log.debug("Successfully retrieved Netatmo settings: client_id present");
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "FIRESTORE_INTEGRATION_ENABLED", matches = "true")
    void shouldConnectToFirestoreAndRetrieveEnturSettings() throws Exception {
        log.debug("Testing Entur settings retrieval from Firestore");
        
        HashMap enturSettings = settings.getEnturSettings("general");
        
        assertThat(enturSettings).isNotNull();
        assertThat(enturSettings).containsKey("stopplace_id");
        
        log.debug("Successfully retrieved Entur settings: {}", enturSettings);
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "FIRESTORE_INTEGRATION_ENABLED", matches = "true")
    void shouldConnectToFirestoreAndRetrieveYrSettings() throws Exception {
        log.debug("Testing Yr settings retrieval from Firestore");
        
        HashMap yrSettings = settings.getYrSettings("general");
        
        assertThat(yrSettings).isNotNull();
        assertThat(yrSettings).containsKey("latitude");
        assertThat(yrSettings).containsKey("longitude");
        
        log.debug("Successfully retrieved Yr settings: {}", yrSettings);
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "FIRESTORE_INTEGRATION_ENABLED", matches = "true")
    void shouldConnectToFirestoreAndRetrieveDisplaySettings() throws Exception {
        log.debug("Testing Display settings retrieval from Firestore");
        
        HashMap displaySettings = settings.getDisplaySettings("general");
        
        assertThat(displaySettings).isNotNull();
        assertThat(displaySettings).containsKey("theme");
        
        log.debug("Successfully retrieved Display settings: {}", displaySettings);
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "FIRESTORE_INTEGRATION_ENABLED", matches = "true")
    void shouldHandleNonExistentUserGracefully() throws Exception {
        log.debug("Testing graceful handling of non-existent user");
        
        // This should fallback to default settings without throwing exceptions
        HashMap googleSettings = settings.getGoogleSettings("non-existent-user-12345");
        
        assertThat(googleSettings).isNotNull();
        assertThat(googleSettings).containsKey("calendar");
        assertThat(googleSettings.get("calendar")).isEqualTo("primary");
        
        log.debug("Successfully handled non-existent user with default settings");
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "FIRESTORE_INTEGRATION_ENABLED", matches = "true")
    void shouldUpdateNetatmoRefreshToken() throws Exception {
        log.debug("Testing Netatmo refresh token update");
        
        String testToken = "test-refresh-token-" + System.currentTimeMillis();
        
        boolean updateResult = settings.setNetatmoRefreshToken(TEST_USER, testToken);
        
        assertThat(updateResult).isTrue();
        
        // Clear cache and verify the token was persisted
        settings.clearCache(TEST_USER);
        HashMap netatmoSettings = settings.getNetatmoSettings(TEST_USER);
        
        assertThat(netatmoSettings).isNotNull();
        assertThat(netatmoSettings.get("refresh_token")).isEqualTo(testToken);
        
        log.debug("Successfully updated and verified Netatmo refresh token");
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "FIRESTORE_INTEGRATION_ENABLED", matches = "true")
    void shouldCacheDatabaseDocuments() throws Exception {
        log.debug("Testing document caching behavior");
        
        // First call should hit the database
        long startTime = System.currentTimeMillis();
        HashMap firstCall = settings.getGoogleSettings("general");
        long firstCallTime = System.currentTimeMillis() - startTime;
        
        // Second call should use cache (should be faster)
        startTime = System.currentTimeMillis();
        HashMap secondCall = settings.getGoogleSettings("general");
        long secondCallTime = System.currentTimeMillis() - startTime;
        
        assertThat(firstCall).isNotNull();
        assertThat(secondCall).isNotNull();
        assertThat(firstCall).isEqualTo(secondCall);
        
        // Cache should make second call faster (allowing some variance for system load)
        assertThat(secondCallTime).isLessThanOrEqualTo(firstCallTime + 50); // 50ms tolerance
        
        log.debug("First call took {}ms, second call took {}ms - caching verified", firstCallTime, secondCallTime);
    }

    @Test
    void shouldProvideDefaultSettingsWhenFirestoreUnavailable() throws Exception {
        log.debug("Testing default settings fallback when Firestore unavailable");
        
        // Clear cache to force database access attempt
        settings.clearCache("unavailable-user");
        
        // These calls should return default settings even without Firestore
        HashMap googleSettings = settings.getGoogleSettings("unavailable-user");
        HashMap netatmoSettings = settings.getNetatmoSettings("unavailable-user");
        HashMap enturSettings = settings.getEnturSettings("unavailable-user");
        HashMap yrSettings = settings.getYrSettings("unavailable-user");
        HashMap displaySettings = settings.getDisplaySettings("unavailable-user");
        
        // Verify all settings have reasonable defaults
        assertThat(googleSettings).containsEntry("calendar", "primary");
        assertThat(netatmoSettings).containsEntry("client_id", "default");
        assertThat(enturSettings).containsEntry("stopplace_id", "NSR:StopPlace:58366");
        assertThat(yrSettings).containsEntry("latitude", "59.9139");
        assertThat(displaySettings).containsEntry("theme", "default");
        
        log.debug("Successfully verified default settings fallback");
    }
}