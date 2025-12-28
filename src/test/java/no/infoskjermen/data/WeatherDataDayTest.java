package no.infoskjermen.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class WeatherDataDayTest {
    
    private WeatherDataDay weatherDataDay;
    
    @BeforeEach
    public void setUp() {
        weatherDataDay = new WeatherDataDay();
    }
    
    @Test
    public void shouldAddMagicForNightPeriodWhenPeriodIsZero() {
        // Given
        WeatherDataPeriod nightPeriod = new WeatherDataPeriod();
        nightPeriod.period = "0";
        nightPeriod.date = LocalDate.now();
        nightPeriod.temperature = 5;
        nightPeriod.symbol = "clearsky_night";
        
        // When
        weatherDataDay.addMagic(nightPeriod);
        
        // Then
        assertThat(weatherDataDay.date).isEqualTo(LocalDate.now());
        assertThat(weatherDataDay.night).isNotNull();
        assertThat(weatherDataDay.night.temperature).isEqualTo(5);
        assertThat(weatherDataDay.night.symbol).isEqualTo("clearsky_night");
        assertThat(weatherDataDay.morning).isNull();
        assertThat(weatherDataDay.day).isNull();
        assertThat(weatherDataDay.evening).isNull();
    }
    
    @Test
    public void shouldAddMagicForMorningPeriodWhenPeriodIsOne() {
        // Given
        WeatherDataPeriod morningPeriod = new WeatherDataPeriod();
        morningPeriod.period = "1";
        morningPeriod.date = LocalDate.now();
        morningPeriod.temperature = 10;
        morningPeriod.symbol = "clearsky_day";
        
        // When
        weatherDataDay.addMagic(morningPeriod);
        
        // Then
        assertThat(weatherDataDay.date).isEqualTo(LocalDate.now());
        assertThat(weatherDataDay.morning).isNotNull();
        assertThat(weatherDataDay.morning.temperature).isEqualTo(10);
        assertThat(weatherDataDay.morning.symbol).isEqualTo("clearsky_day");
        assertThat(weatherDataDay.night).isNull();
        assertThat(weatherDataDay.day).isNull();
        assertThat(weatherDataDay.evening).isNull();
    }
    
    @Test
    public void shouldAddMagicForDayPeriodWhenPeriodIsTwo() {
        // Given
        WeatherDataPeriod dayPeriod = new WeatherDataPeriod();
        dayPeriod.period = "2";
        dayPeriod.date = LocalDate.now();
        dayPeriod.temperature = 15;
        dayPeriod.symbol = "partlycloudy_day";
        
        // When
        weatherDataDay.addMagic(dayPeriod);
        
        // Then
        assertThat(weatherDataDay.date).isEqualTo(LocalDate.now());
        assertThat(weatherDataDay.day).isNotNull();
        assertThat(weatherDataDay.day.temperature).isEqualTo(15);
        assertThat(weatherDataDay.day.symbol).isEqualTo("partlycloudy_day");
        assertThat(weatherDataDay.night).isNull();
        assertThat(weatherDataDay.morning).isNull();
        assertThat(weatherDataDay.evening).isNull();
    }
    
    @Test
    public void shouldAddMagicForEveningPeriodWhenPeriodIsThree() {
        // Given
        WeatherDataPeriod eveningPeriod = new WeatherDataPeriod();
        eveningPeriod.period = "3";
        eveningPeriod.date = LocalDate.now();
        eveningPeriod.temperature = 8;
        eveningPeriod.symbol = "cloudy";
        
        // When
        weatherDataDay.addMagic(eveningPeriod);
        
        // Then
        assertThat(weatherDataDay.date).isEqualTo(LocalDate.now());
        assertThat(weatherDataDay.evening).isNotNull();
        assertThat(weatherDataDay.evening.temperature).isEqualTo(8);
        assertThat(weatherDataDay.evening.symbol).isEqualTo("cloudy");
        assertThat(weatherDataDay.night).isNull();
        assertThat(weatherDataDay.morning).isNull();
        assertThat(weatherDataDay.day).isNull();
    }
    
    @Test
    public void shouldHandleUnknownPeriodGracefullyWhenPeriodNotRecognized() {
        // Given
        WeatherDataPeriod unknownPeriod = new WeatherDataPeriod();
        unknownPeriod.period = "99";
        unknownPeriod.date = LocalDate.now();
        unknownPeriod.temperature = 12;
        
        // When
        weatherDataDay.addMagic(unknownPeriod);
        
        // Then
        assertThat(weatherDataDay.date).isEqualTo(LocalDate.now());
        assertThat(weatherDataDay.night).isNull();
        assertThat(weatherDataDay.morning).isNull();
        assertThat(weatherDataDay.day).isNull();
        assertThat(weatherDataDay.evening).isNull();
    }
    
    @Test
    public void shouldGenerateDebugStringWhenAllPeriodsPresent() {
        // Given
        WeatherDataPeriod night = new WeatherDataPeriod();
        night.period = "0";
        night.date = LocalDate.now();
        night.temperature = 5;
        night.symbol = "night";
        
        WeatherDataPeriod morning = new WeatherDataPeriod();
        morning.period = "1";
        morning.date = LocalDate.now();
        morning.temperature = 10;
        morning.symbol = "morning";
        
        weatherDataDay.addMagic(night);
        weatherDataDay.addMagic(morning);
        
        // When
        String debug = weatherDataDay.debug();
        
        // Then
        assertThat(debug).contains("NewDay date: " + LocalDate.now());
        assertThat(debug).contains("night   -");
        assertThat(debug).contains("morning -");
        assertThat(debug).contains("temperature=5");
        assertThat(debug).contains("temperature=10");
    }
    
    @Test
    public void shouldCompareByDateWhenBothDatesPresent() {
        // Given
        WeatherDataDay day1 = new WeatherDataDay();
        day1.date = LocalDate.now();
        
        WeatherDataDay day2 = new WeatherDataDay();
        day2.date = LocalDate.now().plusDays(1);
        
        // When/Then
        assertThat(day1.compareTo(day2)).isLessThan(0);
        assertThat(day2.compareTo(day1)).isGreaterThan(0);
        assertThat(day1.compareTo(day1)).isEqualTo(-1); // As per existing implementation for equal dates
    }
    
    @Test
    public void shouldHandleNullDateInComparisonWhenDateIsNull() {
        // Given
        WeatherDataDay dayWithNullDate = new WeatherDataDay();
        dayWithNullDate.date = null;
        
        WeatherDataDay dayWithDate = new WeatherDataDay();
        dayWithDate.date = LocalDate.now();
        
        // When/Then
        assertThat(dayWithNullDate.compareTo(dayWithDate)).isEqualTo(1);
    }
}