package no.infoskjermen.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class WeatherDataPeriodTest {
    
    private WeatherDataPeriod weatherDataPeriod;
    
    @BeforeEach
    public void setUp() {
        weatherDataPeriod = new WeatherDataPeriod();
    }
    
    @Test
    public void shouldSetAllPropertiesWhenProvidingValues() {
        // Given
        LocalDate testDate = LocalDate.now();
        
        // When
        weatherDataPeriod.temperature = 15;
        weatherDataPeriod.symbol = "clearsky_day";
        weatherDataPeriod.date = testDate;
        weatherDataPeriod.period = "2";
        
        // Then
        assertThat(weatherDataPeriod.temperature).isEqualTo(15);
        assertThat(weatherDataPeriod.symbol).isEqualTo("clearsky_day");
        assertThat(weatherDataPeriod.date).isEqualTo(testDate);
        assertThat(weatherDataPeriod.period).isEqualTo("2");
    }
    
    @Test
    public void shouldGenerateDebugStringWithAllInformationWhenAllPropertiesSet() {
        // Given
        LocalDate testDate = LocalDate.now();
        weatherDataPeriod.temperature = 20;
        weatherDataPeriod.symbol = "rain";
        weatherDataPeriod.date = testDate;
        weatherDataPeriod.period = "1";
        
        // When
        String debug = weatherDataPeriod.debug();
        
        // Then
        assertThat(debug).contains("WeatherDataPeriod");
        assertThat(debug).contains("temperature=20");
        assertThat(debug).contains("symbol=rain");
        assertThat(debug).contains("date=" + testDate);
        assertThat(debug).contains("period=1");
    }
    
    @Test
    public void shouldHandleNegativeTemperatureWhenBelowZero() {
        // Given/When
        weatherDataPeriod.temperature = -10;
        
        // Then
        assertThat(weatherDataPeriod.temperature).isEqualTo(-10);
        
        String debug = weatherDataPeriod.debug();
        assertThat(debug).contains("temperature=-10");
    }
    
    @Test
    public void shouldHandleNullSymbolWhenNotSet() {
        // Given
        weatherDataPeriod.temperature = 5;
        weatherDataPeriod.date = LocalDate.now();
        weatherDataPeriod.period = "0";
        // symbol is null by default
        
        // When
        String debug = weatherDataPeriod.debug();
        
        // Then
        assertThat(debug).contains("symbol=null");
    }
    
    @Test
    public void shouldHandleNullDateWhenNotSet() {
        // Given
        weatherDataPeriod.temperature = 5;
        weatherDataPeriod.symbol = "snow";
        weatherDataPeriod.period = "3";
        // date is null by default
        
        // When
        String debug = weatherDataPeriod.debug();
        
        // Then
        assertThat(debug).contains("date=null");
    }
    
    @Test
    public void shouldHandleNullPeriodWhenNotSet() {
        // Given
        weatherDataPeriod.temperature = 12;
        weatherDataPeriod.symbol = "partlycloudy_night";
        weatherDataPeriod.date = LocalDate.now();
        // period is null by default
        
        // When
        String debug = weatherDataPeriod.debug();
        
        // Then
        assertThat(debug).contains("period=null");
    }
    
    @Test
    public void shouldHandleZeroTemperatureWhenExactlyZero() {
        // Given/When
        weatherDataPeriod.temperature = 0;
        weatherDataPeriod.symbol = "fog";
        
        // Then
        assertThat(weatherDataPeriod.temperature).isEqualTo(0);
        
        String debug = weatherDataPeriod.debug();
        assertThat(debug).contains("temperature=0");
    }
}