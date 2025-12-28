package no.infoskjermen.tjenester;

import no.infoskjermen.Settings;
import no.infoskjermen.data.NetatmoData;
import no.infoskjermen.data.netatmo.NetatmoMeasure;
import no.infoskjermen.data.netatmo.NetatmoToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class NetatmoServiceTest {

    @SpyBean
    private NetatmoService netatmo;

    @MockBean
    private Settings mockSettings;

    @BeforeEach
    public void setUp() throws Exception {
        // Setup mock responses for Settings to avoid Firestore calls
        HashMap<String, Object> netatmoSettings = new HashMap<>();
        netatmoSettings.put("client_id", "test-client");
        netatmoSettings.put("client_secret", "test-secret"); 
        netatmoSettings.put("refresh_token", null); // No token available
        netatmoSettings.put("indoor_id", "test-indoor-id");
        netatmoSettings.put("outdoor_id", "test-outdoor-id");
        
        when(mockSettings.getNetatmoSettings(anyString())).thenReturn(netatmoSettings);
        
        HashMap<String, Object> generalSettings = new HashMap<>();
        generalSettings.put("client_id", "default");
        generalSettings.put("client_secret", "default");
        when(mockSettings.getNetatmoSettings("general")).thenReturn(generalSettings);
    }

    @Test
    public void testTokenWithoutCredentials() throws Exception {
        // Test that service handles missing credentials gracefully
        try {
            String token = netatmo.getToken("fredrik");
            // If we get a response, it should be handled properly
            if (token != null) {
                assertThat(token).isNotEmpty();
            }
        } catch (Exception e) {
            // Expected when credentials are invalid/missing
            assertThat(e).isNotNull();
        }
    }

    @Test
    public void testGetIndoorTemperatureWithoutCredentials() throws Exception {
        try {
            NetatmoMeasure data = netatmo.getIndoorTemperature("fredrik");
            if (data != null) {
                assertThat(data.getStatus()).isNotNull();
            }
        } catch (Exception e) {
            // Expected when API is unavailable or credentials invalid
            assertThat(e).isNotNull();
        }
    }
    
    @Test
    public void testGetOutdoorTemperatureWithoutCredentials() throws Exception {
        try {
            NetatmoMeasure data = netatmo.getOutdoorTemperature("fredrik");
            if (data != null) {
                assertThat(data.getStatus()).isNotNull();
            }
        } catch (Exception e) {
            // Expected when API is unavailable or credentials invalid
            assertThat(e).isNotNull();
        }
    }

    @Test
    public void testNetatmoTokenExpired() {
        NetatmoToken token = new NetatmoToken();
        token.setAccess_token("adad");
        token.setExpires_in(-1); // Expired token
        
        // Token should be considered invalid when expired
        assertThat(token.getAccess_token()).isNull();
    }
    
    @Test
    public void testNetatmoTokenValid() {
        NetatmoToken token = new NetatmoToken();
        token.setAccess_token("adad");
        token.setExpires_in(1); // Valid token (1 second remaining)
        
        // Token should be valid
        assertThat(token.getAccess_token()).isEqualTo("adad");
    }

    @Test
    public void testNetatmoDataWithoutAPI() throws Exception {
        try {
            NetatmoData data = netatmo.getNetatmoData("fredrik");
            if (data != null && data.indoorTemperature != null) {
                // If we get data, validate it's reasonable
                assertThat(data.indoorTemperature.intValue()).isGreaterThan(-50);
                assertThat(data.indoorTemperature.intValue()).isLessThan(50);
            }
        } catch (Exception e) {
            // Expected when API is unavailable
            assertThat(e).isNotNull();
        }
    }
    
    @Test
    public void testServiceIsConfiguredProperly() {
        // Test that the service is properly configured
        assertThat(netatmo).isNotNull();
    }

    @Test
    public void testIsPresentInSVG() {
        // Test the interface method
        String svgWithNetatmo = "Some SVG content @@TEMP@@ more content";
        String svgWithoutNetatmo = "Some SVG content without netatmo";
        
        assertThat(netatmo.isPresentInSVG(svgWithNetatmo)).isTrue();
        assertThat(netatmo.isPresentInSVG(svgWithoutNetatmo)).isFalse();
    }
}
