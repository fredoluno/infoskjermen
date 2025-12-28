package no.infoskjermen.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class WeatherDataTest {
    
    private WeatherData weatherData;
    
    @BeforeEach
    public void setUp() {
        weatherData = new WeatherData();
    }
    
    @Test
    public void shouldInitializeWithEmptyLongtimeForecastWhenCreated() {
        // Given/When - object is created in setUp
        
        // Then
        assertThat(weatherData.longtimeForecast).isNotNull();
        assertThat(weatherData.longtimeForecast).isEmpty();
        assertThat(weatherData.mainFromToday).isTrue();
        assertThat(weatherData.main).isNull();
    }
    
    @Test
    public void shouldMaintainForecastOrderWhenAddingMultipleDays() {
        // Given
        WeatherDataDay today = new WeatherDataDay();
        today.date = LocalDate.now();
        
        WeatherDataDay tomorrow = new WeatherDataDay();
        tomorrow.date = LocalDate.now().plusDays(1);
        
        WeatherDataDay yesterday = new WeatherDataDay();
        yesterday.date = LocalDate.now().minusDays(1);
        
        // When - add in random order
        weatherData.longtimeForecast.add(tomorrow);
        weatherData.longtimeForecast.add(yesterday);
        weatherData.longtimeForecast.add(today);
        
        // Then - should be ordered by date
        assertThat(weatherData.longtimeForecast).hasSize(3);
        WeatherDataDay[] orderedDays = weatherData.longtimeForecast.toArray(new WeatherDataDay[0]);
        assertThat(orderedDays[0].date).isEqualTo(yesterday.date);
        assertThat(orderedDays[1].date).isEqualTo(today.date);
        assertThat(orderedDays[2].date).isEqualTo(tomorrow.date);
    }
    
    @Test
    public void shouldSetMainFromTodayToFalseWhenChanged() {
        // Given
        weatherData.mainFromToday = true;
        
        // When
        weatherData.mainFromToday = false;
        
        // Then
        assertThat(weatherData.mainFromToday).isFalse();
    }
    
    @Test
    public void shouldAllowSettingMainWeatherData() {
        // Given
        WeatherDataDay mainWeather = new WeatherDataDay();
        mainWeather.date = LocalDate.now();
        
        // When
        weatherData.main = mainWeather;
        
        // Then
        assertThat(weatherData.main).isNotNull();
        assertThat(weatherData.main.date).isEqualTo(LocalDate.now());
    }
}