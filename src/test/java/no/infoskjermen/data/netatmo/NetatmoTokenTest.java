package no.infoskjermen.data.netatmo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Calendar;

import static org.assertj.core.api.Assertions.assertThat;

public class NetatmoTokenTest {
    
    private NetatmoToken netatmoToken;
    
    @BeforeEach
    public void setUp() {
        netatmoToken = new NetatmoToken();
    }
    
    @Test
    public void shouldReturnAccessTokenWhenTokenNotExpired() throws InterruptedException {
        // Given
        String accessToken = "valid_access_token";
        netatmoToken.setAccess_token(accessToken);
        netatmoToken.setExpires_in(3600); // 1 hour from now
        
        // When
        String result = netatmoToken.getAccess_token();
        
        // Then
        assertThat(result).isEqualTo(accessToken);
    }
    
    @Test
    public void shouldReturnNullWhenTokenIsExpired() throws InterruptedException {
        // Given
        String accessToken = "expired_access_token";
        netatmoToken.setAccess_token(accessToken);
        netatmoToken.setExpires_in(-1); // Already expired (1 second ago)
        
        // Small delay to ensure expiration
        Thread.sleep(10);
        
        // When
        String result = netatmoToken.getAccess_token();
        
        // Then
        assertThat(result).isNull();
    }
    
    @Test
    public void shouldSetAndGetRefreshTokenWhenProvidingValidToken() {
        // Given
        String refreshToken = "test_refresh_token";
        
        // When
        netatmoToken.setRefresh_token(refreshToken);
        
        // Then
        assertThat(netatmoToken.getRefresh_token()).isEqualTo(refreshToken);
    }
    
    @Test
    public void shouldHandleNullRefreshTokenWhenNullProvided() {
        // Given
        netatmoToken.setRefresh_token("initial_token");
        
        // When
        netatmoToken.setRefresh_token(null);
        
        // Then
        assertThat(netatmoToken.getRefresh_token()).isNull();
    }
    
    @Test
    public void shouldSetAndGetExpiresInWhenProvidingValidDuration() {
        // Given
        Integer expiresIn = 3600; // 1 hour
        
        // When
        netatmoToken.setExpires_in(expiresIn);
        
        // Then
        assertThat(netatmoToken.getExpires_in()).isEqualTo(3600);
    }
    
    @Test
    public void shouldCalculateExpirationTimeWhenSettingExpiresIn() {
        // Given
        Integer expiresIn = 3600; // 1 hour
        Calendar beforeSetting = Calendar.getInstance();
        
        // When
        netatmoToken.setExpires_in(expiresIn);
        String accessToken = "test_token";
        netatmoToken.setAccess_token(accessToken);
        
        // Then
        // Token should still be valid since we just set it for 1 hour
        String result = netatmoToken.getAccess_token();
        assertThat(result).isEqualTo(accessToken);
    }
    
    @Test
    public void shouldSetAndGetExpireInWhenProvidingValue() {
        // Given
        Integer expireIn = 1800; // 30 minutes
        
        // When
        netatmoToken.setExpire_in(expireIn);
        
        // Then
        assertThat(netatmoToken.getExpire_in()).isEqualTo(1800);
    }
    
    @Test
    public void shouldHandleNullExpireInWhenNullProvided() {
        // Given
        netatmoToken.setExpire_in(3600);
        
        // When
        netatmoToken.setExpire_in(null);
        
        // Then
        assertThat(netatmoToken.getExpire_in()).isNull();
    }
    
    @Test
    public void shouldHandleZeroExpiresInWhenZeroProvided() {
        // Given
        String accessToken = "immediate_expire_token";
        netatmoToken.setAccess_token(accessToken);
        
        // When
        netatmoToken.setExpires_in(0); // Expires immediately
        
        // Then
        // Token should be expired immediately
        String result = netatmoToken.getAccess_token();
        assertThat(result).isNull();
    }
    
    @Test
    public void shouldHandleNegativeExpiresInWhenNegativeValueProvided() {
        // Given
        String accessToken = "already_expired_token";
        netatmoToken.setAccess_token(accessToken);
        
        // When
        netatmoToken.setExpires_in(-3600); // Expired 1 hour ago
        
        // Then
        String result = netatmoToken.getAccess_token();
        assertThat(result).isNull();
    }
    
    @Test
    public void shouldHandleLargeExpiresInValueWhenLongDurationProvided() {
        // Given
        String accessToken = "long_lived_token";
        netatmoToken.setAccess_token(accessToken);
        Integer largeExpiresIn = 86400; // 24 hours
        
        // When
        netatmoToken.setExpires_in(largeExpiresIn);
        
        // Then
        String result = netatmoToken.getAccess_token();
        assertThat(result).isEqualTo(accessToken);
        assertThat(netatmoToken.getExpires_in()).isEqualTo(86400);
    }
    
    @Test
    public void shouldAllowMultipleTokenUpdatesWhenCalledSequentially() {
        // Given
        String firstToken = "first_token";
        String secondToken = "second_token";
        
        // When
        netatmoToken.setAccess_token(firstToken);
        netatmoToken.setExpires_in(3600);
        String firstResult = netatmoToken.getAccess_token();
        
        netatmoToken.setAccess_token(secondToken);
        netatmoToken.setExpires_in(7200);
        String secondResult = netatmoToken.getAccess_token();
        
        // Then
        assertThat(firstResult).isEqualTo(firstToken);
        assertThat(secondResult).isEqualTo(secondToken);
    }
    
    @Test
    public void shouldMaintainIndependenceOfFieldsWhenSettingDifferentValues() {
        // Given/When
        netatmoToken.setAccess_token("access_token");
        netatmoToken.setRefresh_token("refresh_token");
        netatmoToken.setExpires_in(3600);
        netatmoToken.setExpire_in(1800);
        
        // Then
        assertThat(netatmoToken.getRefresh_token()).isEqualTo("refresh_token");
        assertThat(netatmoToken.getExpires_in()).isEqualTo(3600);
        assertThat(netatmoToken.getExpire_in()).isEqualTo(1800);
    }
    
    @Test
    public void shouldHandleEmptyStringTokensWhenEmptyStringsProvided() {
        // Given/When
        netatmoToken.setAccess_token("");
        netatmoToken.setRefresh_token("");
        netatmoToken.setExpires_in(3600);
        
        // Then
        String result = netatmoToken.getAccess_token();
        assertThat(result).isEmpty(); // Empty string, not null, because token is not expired
        assertThat(netatmoToken.getRefresh_token()).isEmpty();
    }
}