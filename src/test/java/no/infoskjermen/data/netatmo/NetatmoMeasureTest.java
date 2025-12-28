package no.infoskjermen.data.netatmo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class NetatmoMeasureTest {
    
    private NetatmoMeasure netatmoMeasure;
    
    @BeforeEach
    public void setUp() {
        netatmoMeasure = new NetatmoMeasure();
    }
    
    @Test
    public void shouldSetAndGetBodyWhenValidBodyListProvided() {
        // Given
        Body body1 = new Body();
        body1.setBegTime(1640995200);
        Body body2 = new Body();
        body2.setBegTime(1640995300);
        
        List<Body> bodyList = Arrays.asList(body1, body2);
        
        // When
        netatmoMeasure.setBody(bodyList);
        
        // Then
        assertThat(netatmoMeasure.getBody()).isNotNull();
        assertThat(netatmoMeasure.getBody()).hasSize(2);
        assertThat(netatmoMeasure.getBody().get(0).getBegTime()).isEqualTo(1640995200);
        assertThat(netatmoMeasure.getBody().get(1).getBegTime()).isEqualTo(1640995300);
    }
    
    @Test
    public void shouldHandleEmptyBodyListWhenEmptyListProvided() {
        // Given
        List<Body> emptyBodyList = new ArrayList<>();
        
        // When
        netatmoMeasure.setBody(emptyBodyList);
        
        // Then
        assertThat(netatmoMeasure.getBody()).isNotNull();
        assertThat(netatmoMeasure.getBody()).isEmpty();
    }
    
    @Test
    public void shouldHandleNullBodyWhenNullProvided() {
        // Given
        Body body = new Body();
        netatmoMeasure.setBody(Arrays.asList(body));
        
        // When
        netatmoMeasure.setBody(null);
        
        // Then
        assertThat(netatmoMeasure.getBody()).isNull();
    }
    
    @Test
    public void shouldSetAndGetStatusWhenValidStatusProvided() {
        // Given
        String status = "ok";
        
        // When
        netatmoMeasure.setStatus(status);
        
        // Then
        assertThat(netatmoMeasure.getStatus()).isEqualTo("ok");
    }
    
    @Test
    public void shouldHandleDifferentStatusValuesWhenVariousStatusesSet() {
        // Given/When/Then
        netatmoMeasure.setStatus("ok");
        assertThat(netatmoMeasure.getStatus()).isEqualTo("ok");
        
        netatmoMeasure.setStatus("error");
        assertThat(netatmoMeasure.getStatus()).isEqualTo("error");
        
        netatmoMeasure.setStatus(null);
        assertThat(netatmoMeasure.getStatus()).isNull();
    }
    
    @Test
    public void shouldSetAndGetTimeExecWhenValidTimeProvided() {
        // Given
        Double timeExec = 0.045;
        
        // When
        netatmoMeasure.setTimeExec(timeExec);
        
        // Then
        assertThat(netatmoMeasure.getTimeExec()).isEqualTo(0.045);
    }
    
    @Test
    public void shouldHandleNullTimeExecWhenNullProvided() {
        // Given
        netatmoMeasure.setTimeExec(0.123);
        
        // When
        netatmoMeasure.setTimeExec(null);
        
        // Then
        assertThat(netatmoMeasure.getTimeExec()).isNull();
    }
    
    @Test
    public void shouldSetAndGetTimeServerWhenValidTimeProvided() {
        // Given
        Integer timeServer = 1640995200;
        
        // When
        netatmoMeasure.setTimeServer(timeServer);
        
        // Then
        assertThat(netatmoMeasure.getTimeServer()).isEqualTo(1640995200);
    }
    
    @Test
    public void shouldHandleNullTimeServerWhenNullProvided() {
        // Given
        netatmoMeasure.setTimeServer(123456);
        
        // When
        netatmoMeasure.setTimeServer(null);
        
        // Then
        assertThat(netatmoMeasure.getTimeServer()).isNull();
    }
    
    @Test
    public void shouldInitializeAdditionalPropertiesAsEmptyMapWhenCreated() {
        // Given/When - object created in setUp
        
        // Then
        assertThat(netatmoMeasure.getAdditionalProperties()).isNotNull();
        assertThat(netatmoMeasure.getAdditionalProperties()).isEmpty();
    }
    
    @Test
    public void shouldAllowModificationOfAdditionalPropertiesWhenAccessingMap() {
        // Given/When
        netatmoMeasure.getAdditionalProperties().put("custom_property", "custom_value");
        netatmoMeasure.getAdditionalProperties().put("api_version", "2.0");
        
        // Then
        assertThat(netatmoMeasure.getAdditionalProperties()).hasSize(2);
        assertThat(netatmoMeasure.getAdditionalProperties()).containsEntry("custom_property", "custom_value");
        assertThat(netatmoMeasure.getAdditionalProperties()).containsEntry("api_version", "2.0");
    }
    
    @Test
    public void shouldHandleComplexBodyStructureWhenBodyContainsMeasurements() {
        // Given
        Body body = new Body();
        body.setBegTime(1640995200);
        
        List<List<Double>> measurements = new ArrayList<>();
        measurements.add(Arrays.asList(22.5, 800.0, 45.0, 1013.0, 35.0)); // Indoor measurements
        body.setValue(measurements);
        
        body.setAdditionalProperty("module_id", "70:ee:50:xx:xx:xx");
        
        // When
        netatmoMeasure.setBody(Arrays.asList(body));
        netatmoMeasure.setStatus("ok");
        netatmoMeasure.setTimeExec(0.034);
        netatmoMeasure.setTimeServer(1640995200);
        
        // Then
        assertThat(netatmoMeasure.getBody()).hasSize(1);
        assertThat(netatmoMeasure.getBody().get(0).getValue().get(0)).hasSize(5);
        assertThat(netatmoMeasure.getBody().get(0).getAdditionalProperties())
            .containsEntry("module_id", "70:ee:50:xx:xx:xx");
        assertThat(netatmoMeasure.getStatus()).isEqualTo("ok");
        assertThat(netatmoMeasure.getTimeExec()).isEqualTo(0.034);
        assertThat(netatmoMeasure.getTimeServer()).isEqualTo(1640995200);
    }
    
    @Test
    public void shouldHandleZeroAndNegativeValuesWhenProvidingEdgeCaseNumbers() {
        // Given/When
        netatmoMeasure.setTimeExec(0.0);
        netatmoMeasure.setTimeServer(0);
        
        // Then
        assertThat(netatmoMeasure.getTimeExec()).isEqualTo(0.0);
        assertThat(netatmoMeasure.getTimeServer()).isEqualTo(0);
    }
    
    @Test
    public void shouldMaintainIndependenceOfPropertiesWhenSettingDifferentValues() {
        // Given/When
        netatmoMeasure.setStatus("ok");
        netatmoMeasure.setTimeExec(0.123);
        netatmoMeasure.setTimeServer(123456);
        
        Body body = new Body();
        body.setBegTime(999);
        netatmoMeasure.setBody(Arrays.asList(body));
        
        // Modify one property
        netatmoMeasure.setStatus("error");
        
        // Then - other properties should remain unchanged
        assertThat(netatmoMeasure.getStatus()).isEqualTo("error");
        assertThat(netatmoMeasure.getTimeExec()).isEqualTo(0.123);
        assertThat(netatmoMeasure.getTimeServer()).isEqualTo(123456);
        assertThat(netatmoMeasure.getBody().get(0).getBegTime()).isEqualTo(999);
    }
}