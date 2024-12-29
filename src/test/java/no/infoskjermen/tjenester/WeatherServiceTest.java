package no.infoskjermen.tjenester;

import no.infoskjermen.data.WeatherData;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class WeatherServiceTest {

    @Autowired
    private  WeatherService weather;

    @Test
    public void testGetWeatherReport() throws Exception {
        WeatherData weatherData = weather.getWeatherReport("fredrik");
        assertThat(weatherData.main).isNotNull();

    }

    @Test
    public void testGetnewYr() throws Exception {
        //WeatherData weatherData = weather.getWeatherReport("fredrik");
       WeatherData wd= weather.getWeatherFromYrNew("https://api.met.no/weatherapi/locationforecast/2.0/compact.json?lat=60.1603&lon=11.1862");

        assertThat(wd).isNotNull();

    }


}
