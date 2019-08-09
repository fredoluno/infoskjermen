package no.infoskjermen.data;

import java.time.LocalDate;

public class WeatherDataPeriod {



    public int temperature;
    public String symbol;
    public LocalDate date;
    public String period;

    public String debug(){

        return "WeatherDataPeriod - temperature=" + temperature + " symbol=" + symbol + " date=" + date+ " period=" +period ;

    }

}
