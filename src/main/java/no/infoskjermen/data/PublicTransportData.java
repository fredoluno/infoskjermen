package no.infoskjermen.data;

import java.time.LocalDateTime;
import java.util.TreeSet;

public class PublicTransportData {
    public final String name;
    public final TreeSet<LocalDateTime> arrivals;
    public final TreeSet<LocalDateTime> departures;

    public PublicTransportData(String name, TreeSet<LocalDateTime> arrivals, TreeSet<LocalDateTime> departures) {
        this.name = name;
        this.arrivals = arrivals;
        this.departures = departures;
    }
}

