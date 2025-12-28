package no.infoskjermen.utils;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

public class DivUtilsTest {
    
    @Test
    public void shouldPrintHashMapWithSingleEntryWhenOnePairProvided() {
        // Given
        HashMap<String, String> map = new HashMap<>();
        map.put("key1", "value1");
        
        // When
        String result = DivUtils.printHashMap(map);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).contains("key1=value1");
        assertThat(result).startsWith("[");
        assertThat(result).endsWith("]");
    }
    
    @Test
    public void shouldPrintHashMapWithMultipleEntriesWhenMultiplePairsProvided() {
        // Given
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", "Test User");
        map.put("age", 25);
        map.put("active", true);
        
        // When
        String result = DivUtils.printHashMap(map);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).contains("name=Test User");
        assertThat(result).contains("age=25");
        assertThat(result).contains("active=true");
        assertThat(result).startsWith("[");
        assertThat(result).endsWith("]");
    }
    
    @Test
    public void shouldPrintEmptyHashMapWhenEmptyMapProvided() {
        // Given
        HashMap<String, String> emptyMap = new HashMap<>();
        
        // When
        String result = DivUtils.printHashMap(emptyMap);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo("[{}]");
    }
    
    @Test
    public void shouldHandleNullValuesWhenMapContainsNulls() {
        // Given
        HashMap<String, String> map = new HashMap<>();
        map.put("validKey", "validValue");
        map.put("nullKey", null);
        
        // When
        String result = DivUtils.printHashMap(map);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).contains("validKey=validValue");
        assertThat(result).contains("nullKey=null");
        assertThat(result).startsWith("[");
        assertThat(result).endsWith("]");
    }
    
    @Test
    public void shouldHandleSpecialCharactersWhenMapContainsSpecialCharacters() {
        // Given
        HashMap<String, String> map = new HashMap<>();
        map.put("norwegian", "æøå");
        map.put("symbols", "!@#$%^&*()");
        map.put("quotes", "\"quoted text\"");
        
        // When
        String result = DivUtils.printHashMap(map);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).contains("norwegian=æøå");
        assertThat(result).contains("symbols=!@#$%^&*()");
        assertThat(result).contains("quotes=\"quoted text\"");
    }
    
    @Test
    public void shouldHandleMixedDataTypesWhenMapContainsDifferentTypes() {
        // Given
        HashMap<String, Object> map = new HashMap<>();
        map.put("string", "text");
        map.put("integer", 42);
        map.put("double", 3.14);
        map.put("boolean", false);
        
        // When
        String result = DivUtils.printHashMap(map);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).contains("string=text");
        assertThat(result).contains("integer=42");
        assertThat(result).contains("double=3.14");
        assertThat(result).contains("boolean=false");
    }
    
    @Test
    public void shouldHandleEmptyStringsWhenMapContainsEmptyValues() {
        // Given
        HashMap<String, String> map = new HashMap<>();
        map.put("empty", "");
        map.put("spaces", "   ");
        map.put("normal", "value");
        
        // When
        String result = DivUtils.printHashMap(map);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).contains("empty=");
        assertThat(result).contains("spaces=   ");
        assertThat(result).contains("normal=value");
    }
    
    @Test
    public void shouldHandleNestedObjectsWhenMapContainsComplexObjects() {
        // Given
        HashMap<String, Object> innerMap = new HashMap<>();
        innerMap.put("inner", "value");
        
        HashMap<String, Object> map = new HashMap<>();
        map.put("simple", "text");
        map.put("nested", innerMap);
        
        // When
        String result = DivUtils.printHashMap(map);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).contains("simple=text");
        assertThat(result).contains("nested=");
        // The nested map should be represented as its toString() result
    }
    
    @Test
    public void shouldProduceConsistentOutputWhenCalledMultipleTimesWithSameMap() {
        // Given
        HashMap<String, String> map = new HashMap<>();
        map.put("consistent", "value");
        
        // When
        String result1 = DivUtils.printHashMap(map);
        String result2 = DivUtils.printHashMap(map);
        
        // Then
        assertThat(result1).isEqualTo(result2);
    }
    
    @Test
    public void shouldHandleNumericKeysWhenMapHasIntegerKeys() {
        // Given
        HashMap<Integer, String> map = new HashMap<>();
        map.put(1, "one");
        map.put(2, "two");
        
        // When
        String result = DivUtils.printHashMap(map);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).contains("1=one");
        assertThat(result).contains("2=two");
        assertThat(result).startsWith("[");
        assertThat(result).endsWith("]");
    }
}