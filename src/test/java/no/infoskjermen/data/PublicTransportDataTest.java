package no.infoskjermen.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;

public class PublicTransportDataTest {
    
    private TreeSet<LocalDateTime> arrivals;
    private TreeSet<LocalDateTime> departures;
    private PublicTransportData transportData;
    
    @BeforeEach
    public void setUp() {
        arrivals = new TreeSet<>();
        departures = new TreeSet<>();
    }
    
    @Test
    public void shouldCreatePublicTransportDataWithAllPropertiesWhenGivenValidInputs() {
        // Given
        String stationName = "Oslo Central Station";
        LocalDateTime now = LocalDateTime.now();
        arrivals.add(now.plusMinutes(5));
        arrivals.add(now.plusMinutes(15));
        departures.add(now.plusMinutes(2));
        departures.add(now.plusMinutes(12));
        
        // When
        transportData = new PublicTransportData(stationName, arrivals, departures);
        
        // Then
        assertThat(transportData.name).isEqualTo(stationName);
        assertThat(transportData.arrivals).hasSize(2);
        assertThat(transportData.departures).hasSize(2);
        assertThat(transportData.arrivals).containsExactly(now.plusMinutes(5), now.plusMinutes(15));
        assertThat(transportData.departures).containsExactly(now.plusMinutes(2), now.plusMinutes(12));
    }
    
    @Test
    public void shouldMaintainArrivalsInChronologicalOrderWhenAddedOutOfOrder() {
        // Given
        String stationName = "Nationaltheatret";
        LocalDateTime now = LocalDateTime.now();
        
        // Add arrivals in random order
        arrivals.add(now.plusMinutes(30));
        arrivals.add(now.plusMinutes(10));
        arrivals.add(now.plusMinutes(20));
        
        // When
        transportData = new PublicTransportData(stationName, arrivals, departures);
        
        // Then
        LocalDateTime[] orderedArrivals = transportData.arrivals.toArray(new LocalDateTime[0]);
        assertThat(orderedArrivals[0]).isEqualTo(now.plusMinutes(10));
        assertThat(orderedArrivals[1]).isEqualTo(now.plusMinutes(20));
        assertThat(orderedArrivals[2]).isEqualTo(now.plusMinutes(30));
    }
    
    @Test
    public void shouldMaintainDeparturesInChronologicalOrderWhenAddedOutOfOrder() {
        // Given
        String stationName = "Stortinget";
        LocalDateTime now = LocalDateTime.now();
        
        // Add departures in random order
        departures.add(now.plusMinutes(25));
        departures.add(now.plusMinutes(5));
        departures.add(now.plusMinutes(15));
        
        // When
        transportData = new PublicTransportData(stationName, arrivals, departures);
        
        // Then
        LocalDateTime[] orderedDepartures = transportData.departures.toArray(new LocalDateTime[0]);
        assertThat(orderedDepartures[0]).isEqualTo(now.plusMinutes(5));
        assertThat(orderedDepartures[1]).isEqualTo(now.plusMinutes(15));
        assertThat(orderedDepartures[2]).isEqualTo(now.plusMinutes(25));
    }
    
    @Test
    public void shouldHandleEmptyArrivalsSetWhenNoArrivalsProvided() {
        // Given
        String stationName = "Jernbanetorget";
        departures.add(LocalDateTime.now().plusMinutes(10));
        
        // When
        transportData = new PublicTransportData(stationName, arrivals, departures);
        
        // Then
        assertThat(transportData.name).isEqualTo(stationName);
        assertThat(transportData.arrivals).isEmpty();
        assertThat(transportData.departures).hasSize(1);
    }
    
    @Test
    public void shouldHandleEmptyDeparturesSetWhenNoDeparturesProvided() {
        // Given
        String stationName = "Grønland";
        arrivals.add(LocalDateTime.now().plusMinutes(8));
        
        // When
        transportData = new PublicTransportData(stationName, arrivals, departures);
        
        // Then
        assertThat(transportData.name).isEqualTo(stationName);
        assertThat(transportData.arrivals).hasSize(1);
        assertThat(transportData.departures).isEmpty();
    }
    
    @Test
    public void shouldHandleBothEmptyCollectionsWhenNoTimesProvided() {
        // Given
        String stationName = "Tøyen";
        
        // When
        transportData = new PublicTransportData(stationName, arrivals, departures);
        
        // Then
        assertThat(transportData.name).isEqualTo(stationName);
        assertThat(transportData.arrivals).isEmpty();
        assertThat(transportData.departures).isEmpty();
    }
    
    @Test
    public void shouldHandleNullStationNameWhenNameNotProvided() {
        // Given
        arrivals.add(LocalDateTime.now().plusMinutes(5));
        
        // When
        transportData = new PublicTransportData(null, arrivals, departures);
        
        // Then
        assertThat(transportData.name).isNull();
        assertThat(transportData.arrivals).hasSize(1);
        assertThat(transportData.departures).isEmpty();
    }
    
    @Test
    public void shouldPreventDuplicateTimesWhenSameTimeAddedMultipleTimes() {
        // Given
        String stationName = "Majorstuen";
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sameTime = now.plusMinutes(10);
        
        // Add same time multiple times
        arrivals.add(sameTime);
        arrivals.add(sameTime);
        arrivals.add(sameTime);
        
        departures.add(sameTime);
        departures.add(sameTime);
        
        // When
        transportData = new PublicTransportData(stationName, arrivals, departures);
        
        // Then
        assertThat(transportData.arrivals).hasSize(1); // TreeSet prevents duplicates
        assertThat(transportData.departures).hasSize(1); // TreeSet prevents duplicates
        assertThat(transportData.arrivals.first()).isEqualTo(sameTime);
        assertThat(transportData.departures.first()).isEqualTo(sameTime);
    }
}