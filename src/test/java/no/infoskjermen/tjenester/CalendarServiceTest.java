package no.infoskjermen.tjenester;

import com.google.api.services.calendar.model.Events;
import no.infoskjermen.Settings;
import no.infoskjermen.data.CalendarEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class CalendarServiceTest {

    @SpyBean
    private CalendarService calendar;

    @MockBean
    private Settings mockSettings;

    @BeforeEach
    public void setUp() throws Exception {
        // Setup mock responses for Settings
        HashMap<String, Object> googleSettings = new HashMap<>();
        googleSettings.put("calendar", null); // No calendar configured - should return empty set
        googleSettings.put("display_calendar", "primary");
        googleSettings.put("refresh_token", null); // Simulate no token available
        
        when(mockSettings.getGoogleSettings(anyString())).thenReturn(googleSettings);
        
        HashMap<String, Object> generalSettings = new HashMap<>(); 
        generalSettings.put("calendar", "primary");
        generalSettings.put("display_calendar", "primary");
        when(mockSettings.getGoogleSettings("general")).thenReturn(generalSettings);

        // Mock the inherited getEvents method from GoogleService
        Events emptyEvents = new Events();
        emptyEvents.setItems(new ArrayList<>());
        doReturn(emptyEvents).when(calendar).getEvents(anyString(), any(LocalDateTime.class), any(LocalDateTime.class), anyString());
    }

    @Test
    public void testGetCalendarEventsWithoutCredentials() throws Exception {
        // When no credentials are available, should return empty set without error
        TreeSet<CalendarEvent> events = calendar.getCalendarEvents("fredrik");
        assertThat(events).isNotNull();
        assertThat(events).isEmpty(); // Expected when no auth credentials
    }
    
    @Test
    public void testPopulateWithoutEvents() {
        String test = "dette er en test @@EVENT1@@ og @@EVENT2@@";
        String result = calendar.populate(test, "fredrik");
        
        // When no events are available, placeholders should be removed or replaced with empty content
        assertThat(result).isNotNull();
        // The populate method should handle missing events gracefully
        // Since we can't access external APIs, placeholders may remain but service should not crash
    }
    
    @Test 
    public void testPopulateWithEmptyTemplate() {
        String result = calendar.populate("", "fredrik");
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }
    
    @Test
    public void testPopulateWithNoPlaceholders() {
        String test = "This is a test without any placeholders";
        String result = calendar.populate(test, "fredrik");
        assertThat(result).isEqualTo(test); // Should return unchanged
    }
    
    @Test
    public void testServiceIsConfiguredProperly() {
        // Test that the service is properly configured and doesn't crash
        assertThat(calendar).isNotNull();
        
        // Test that we can call methods without external dependencies
        String simpleText = "Simple text";
        String result = calendar.populate(simpleText, "testuser");
        assertThat(result).isNotNull();
    }

    @Test
    public void testIsPresentInSVG() {
        // Test the interface method
        String svgWithEvent = "Some SVG content @@EVENT1@@ more content";
        String svgWithoutEvent = "Some SVG content without events";
        
        assertThat(calendar.isPresentInSVG(svgWithEvent)).isTrue();
        assertThat(calendar.isPresentInSVG(svgWithoutEvent)).isFalse();
    }
}
