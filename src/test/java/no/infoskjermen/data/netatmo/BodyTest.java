package no.infoskjermen.data.netatmo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class BodyTest {
    
    private Body body;
    
    @BeforeEach
    public void setUp() {
        body = new Body();
    }
    
    @Test
    public void shouldSetAndGetBegTimeWhenProvidingValidTime() {
        // Given
        Integer testTime = 1640995200; // Unix timestamp
        
        // When
        body.setBegTime(testTime);
        
        // Then
        assertThat(body.getBegTime()).isEqualTo(testTime);
    }
    
    @Test
    public void shouldHandleNullBegTimeWhenSettingToNull() {
        // Given
        body.setBegTime(12345);
        
        // When
        body.setBegTime(null);
        
        // Then
        assertThat(body.getBegTime()).isNull();
    }
    
    @Test
    public void shouldSetAndGetValueWhenProvidingValidMeasurements() {
        // Given
        List<List<Double>> testValues = new ArrayList<>();
        List<Double> measurement = Arrays.asList(22.5, 45.0, 800.0);
        testValues.add(measurement);
        
        // When
        body.setValue(testValues);
        
        // Then
        assertThat(body.getValue()).isNotNull();
        assertThat(body.getValue()).hasSize(1);
        assertThat(body.getValue().get(0)).hasSize(3);
        assertThat(body.getValue().get(0)).containsExactly(22.5, 45.0, 800.0);
    }
    
    @Test
    public void shouldHandleEmptyValueListWhenEmptyListProvided() {
        // Given
        List<List<Double>> emptyValues = new ArrayList<>();
        
        // When
        body.setValue(emptyValues);
        
        // Then
        assertThat(body.getValue()).isNotNull();
        assertThat(body.getValue()).isEmpty();
    }
    
    @Test
    public void shouldHandleMultipleMeasurementsWhenMultipleValuesProvided() {
        // Given
        List<List<Double>> testValues = new ArrayList<>();
        testValues.add(Arrays.asList(20.0, 40.0));
        testValues.add(Arrays.asList(21.0, 42.0));
        testValues.add(Arrays.asList(22.0, 44.0));
        
        // When
        body.setValue(testValues);
        
        // Then
        assertThat(body.getValue()).hasSize(3);
        assertThat(body.getValue().get(0)).containsExactly(20.0, 40.0);
        assertThat(body.getValue().get(1)).containsExactly(21.0, 42.0);
        assertThat(body.getValue().get(2)).containsExactly(22.0, 44.0);
    }
    
    @Test
    public void shouldHandleNullValueWhenSettingToNull() {
        // Given
        List<List<Double>> testValues = new ArrayList<>();
        testValues.add(Arrays.asList(1.0, 2.0));
        body.setValue(testValues);
        
        // When
        body.setValue(null);
        
        // Then
        assertThat(body.getValue()).isNull();
    }
    
    @Test
    public void shouldSetAdditionalPropertyWhenProvidingKeyValue() {
        // Given
        String key = "deviceType";
        String value = "NAMain";
        
        // When
        body.setAdditionalProperty(key, value);
        
        // Then
        assertThat(body.getAdditionalProperties()).containsEntry(key, value);
    }
    
    @Test
    public void shouldSetMultipleAdditionalPropertiesWhenMultipleKeysProvided() {
        // Given/When
        body.setAdditionalProperty("temperature_unit", "celsius");
        body.setAdditionalProperty("humidity_unit", "percent");
        body.setAdditionalProperty("station_name", "Home Station");
        
        // Then
        assertThat(body.getAdditionalProperties()).hasSize(3);
        assertThat(body.getAdditionalProperties()).containsEntry("temperature_unit", "celsius");
        assertThat(body.getAdditionalProperties()).containsEntry("humidity_unit", "percent");
        assertThat(body.getAdditionalProperties()).containsEntry("station_name", "Home Station");
    }
    
    @Test
    public void shouldOverwriteAdditionalPropertyWhenSameKeyUsedTwice() {
        // Given
        String key = "status";
        
        // When
        body.setAdditionalProperty(key, "initial");
        body.setAdditionalProperty(key, "updated");
        
        // Then
        assertThat(body.getAdditionalProperties()).containsEntry(key, "updated");
        assertThat(body.getAdditionalProperties()).hasSize(1);
    }
    
    @Test
    public void shouldHandleNullKeyInAdditionalPropertyWhenKeyIsNull() {
        // Given
        String nullKey = null;
        String value = "test_value";
        
        // When
        body.setAdditionalProperty(nullKey, value);
        
        // Then
        assertThat(body.getAdditionalProperties()).containsEntry(null, value);
    }
    
    @Test
    public void shouldHandleNullValueInAdditionalPropertyWhenValueIsNull() {
        // Given
        String key = "nullable_property";
        
        // When
        body.setAdditionalProperty(key, null);
        
        // Then
        assertThat(body.getAdditionalProperties()).containsEntry(key, null);
    }
    
    @Test
    public void shouldInitializeAdditionalPropertiesAsEmptyMapWhenCreated() {
        // Given/When - body created in setUp
        
        // Then
        assertThat(body.getAdditionalProperties()).isNotNull();
        assertThat(body.getAdditionalProperties()).isEmpty();
    }
    
    @Test
    public void shouldHandleDifferentDataTypesInAdditionalPropertiesWhenMixedTypesUsed() {
        // Given/When
        body.setAdditionalProperty("string_prop", "text");
        body.setAdditionalProperty("int_prop", 42);
        body.setAdditionalProperty("double_prop", 3.14);
        body.setAdditionalProperty("boolean_prop", true);
        
        // Then
        assertThat(body.getAdditionalProperties()).hasSize(4);
        assertThat(body.getAdditionalProperties().get("string_prop")).isEqualTo("text");
        assertThat(body.getAdditionalProperties().get("int_prop")).isEqualTo(42);
        assertThat(body.getAdditionalProperties().get("double_prop")).isEqualTo(3.14);
        assertThat(body.getAdditionalProperties().get("boolean_prop")).isEqualTo(true);
    }
}