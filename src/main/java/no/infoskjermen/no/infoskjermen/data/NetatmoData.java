package no.infoskjermen.no.infoskjermen.data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NetatmoData {

    private String something;

    public NetatmoData(String something) {
        this.something = something;
    }
}
