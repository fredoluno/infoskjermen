package no.infoskjermen.data;

import java.util.TreeSet;

public class WeatherData {

    public WeatherDataDay main;
    public TreeSet<WeatherDataDay> longtimeForecast;
    public boolean mainFromToday = true;

    public WeatherData(){
        longtimeForecast = new TreeSet<>();
    }



}
