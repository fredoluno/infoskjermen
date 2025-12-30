package no.infoskjermen.data;

import java.time.LocalDate;

public class WeatherDataPeriod {

    public int temperature;
    public String symbol;
    public LocalDate date;
    public String period;

    // Wind data from Yr
    public Double windSpeed; // m/s
    public String windDirection; // e.g. "N" or degrees

    public String debug() {

        return "WeatherDataPeriod - temperature=" + temperature + " symbol=" + symbol + " date=" + date + " period="
                + period;

    }

}
