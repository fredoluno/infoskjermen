package no.infoskjermen.data;

import no.infoskjermen.data.netatmo.Body;
import no.infoskjermen.data.netatmo.NetatmoMeasure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class NetatmoDataTest {
    
    private NetatmoMeasure indoorMeasure;
    private NetatmoMeasure outdoorMeasure;
    
    @BeforeEach
    public void setUp() {
        indoorMeasure = new NetatmoMeasure();
        outdoorMeasure = new NetatmoMeasure();
    }
    
    @Test
    public void shouldCreateNetatmoDataWithValidMeasurementsWhenBothMeasuresProvided() {
        // Given
        setupValidIndoorMeasure();
        setupValidOutdoorMeasure();
        
        // When
        NetatmoData netatmoData = new NetatmoData(indoorMeasure, outdoorMeasure);
        
        // Then
        assertThat(netatmoData.indoorTemperature).isEqualTo(22);
        assertThat(netatmoData.indoorHumidity).isEqualTo(45);
        assertThat(netatmoData.outdoorTemperature).isEqualTo(15);
        assertThat(netatmoData.outdoorHumidity).isEqualTo(60);
        assertThat(netatmoData.co2).isEqualTo(800);
        assertThat(netatmoData.noise).isEqualTo(35);
        assertThat(netatmoData.pressure).isEqualTo(1013);
    }
    
    @Test
    public void shouldHandleNegativeTemperaturesWhenOutdoorTemperatureBelowZero() {
        // Given
        setupValidIndoorMeasure();
        setupOutdoorMeasureWithNegativeTemperature();
        
        // When
        NetatmoData netatmoData = new NetatmoData(indoorMeasure, outdoorMeasure);
        
        // Then
        assertThat(netatmoData.indoorTemperature).isEqualTo(22);
        assertThat(netatmoData.outdoorTemperature).isEqualTo(-5);
        assertThat(netatmoData.outdoorHumidity).isEqualTo(80);
    }
    
    @Test
    public void shouldThrowExceptionWhenIndoorMeasureIsNull() {
        // Given
        setupValidOutdoorMeasure();
        
        // When/Then
        assertThatThrownBy(() -> new NetatmoData(null, outdoorMeasure))
            .isInstanceOf(NullPointerException.class);
    }
    
    @Test
    public void shouldThrowExceptionWhenOutdoorMeasureIsNull() {
        // Given
        setupValidIndoorMeasure();
        
        // When/Then
        assertThatThrownBy(() -> new NetatmoData(indoorMeasure, null))
            .isInstanceOf(NullPointerException.class);
    }
    
    @Test
    public void shouldThrowExceptionWhenIndoorMeasureHasNoBody() {
        // Given
        indoorMeasure.setBody(new ArrayList<>());
        setupValidOutdoorMeasure();
        
        // When/Then
        assertThatThrownBy(() -> new NetatmoData(indoorMeasure, outdoorMeasure))
            .isInstanceOf(IndexOutOfBoundsException.class);
    }
    
    @Test
    public void shouldThrowExceptionWhenOutdoorMeasureHasNoBody() {
        // Given
        setupValidIndoorMeasure();
        outdoorMeasure.setBody(new ArrayList<>());
        
        // When/Then
        assertThatThrownBy(() -> new NetatmoData(indoorMeasure, outdoorMeasure))
            .isInstanceOf(IndexOutOfBoundsException.class);
    }
    
    @Test
    public void shouldHandleHighCO2LevelsWhenCO2Above1000() {
        // Given
        setupIndoorMeasureWithHighCO2();
        setupValidOutdoorMeasure();
        
        // When
        NetatmoData netatmoData = new NetatmoData(indoorMeasure, outdoorMeasure);
        
        // Then
        assertThat(netatmoData.co2).isEqualTo(1500);
        assertThat(netatmoData.indoorTemperature).isEqualTo(25);
        assertThat(netatmoData.noise).isEqualTo(40);
    }
    
    @Test
    public void shouldHandleHighHumidityLevelsWhenHumidityNearMaximum() {
        // Given
        setupValidIndoorMeasure();
        setupOutdoorMeasureWithHighHumidity();
        
        // When
        NetatmoData netatmoData = new NetatmoData(indoorMeasure, outdoorMeasure);
        
        // Then
        assertThat(netatmoData.outdoorHumidity).isEqualTo(95);
        assertThat(netatmoData.indoorHumidity).isEqualTo(45);
    }
    
    // Helper methods to setup test data following the expected structure
    private void setupValidIndoorMeasure() {
        Body body = new Body();
        List<List<Double>> values = new ArrayList<>();
        // Indoor measure structure: [temperature, co2, humidity, pressure, noise]
        List<Double> measurements = Arrays.asList(22.0, 800.0, 45.0, 1013.0, 35.0);
        values.add(measurements);
        body.setValue(values);
        
        List<Body> bodyList = new ArrayList<>();
        bodyList.add(body);
        indoorMeasure.setBody(bodyList);
    }
    
    private void setupValidOutdoorMeasure() {
        Body body = new Body();
        List<List<Double>> values = new ArrayList<>();
        // Outdoor measure structure: [temperature, humidity]
        List<Double> measurements = Arrays.asList(15.0, 60.0);
        values.add(measurements);
        body.setValue(values);
        
        List<Body> bodyList = new ArrayList<>();
        bodyList.add(body);
        outdoorMeasure.setBody(bodyList);
    }
    
    private void setupOutdoorMeasureWithNegativeTemperature() {
        Body body = new Body();
        List<List<Double>> values = new ArrayList<>();
        List<Double> measurements = Arrays.asList(-5.0, 80.0);
        values.add(measurements);
        body.setValue(values);
        
        List<Body> bodyList = new ArrayList<>();
        bodyList.add(body);
        outdoorMeasure.setBody(bodyList);
    }
    
    private void setupIndoorMeasureWithHighCO2() {
        Body body = new Body();
        List<List<Double>> values = new ArrayList<>();
        List<Double> measurements = Arrays.asList(25.0, 1500.0, 50.0, 1015.0, 40.0);
        values.add(measurements);
        body.setValue(values);
        
        List<Body> bodyList = new ArrayList<>();
        bodyList.add(body);
        indoorMeasure.setBody(bodyList);
    }
    
    private void setupOutdoorMeasureWithHighHumidity() {
        Body body = new Body();
        List<List<Double>> values = new ArrayList<>();
        List<Double> measurements = Arrays.asList(18.0, 95.0);
        values.add(measurements);
        body.setValue(values);
        
        List<Body> bodyList = new ArrayList<>();
        bodyList.add(body);
        outdoorMeasure.setBody(bodyList);
    }
}