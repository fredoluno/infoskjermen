package no.infoskjermen;


import no.infoskjermen.data.WeatherData;
import no.infoskjermen.tjenester.CalendarService;
import no.infoskjermen.tjenester.NetatmoService;
import no.infoskjermen.tjenester.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableCaching
@RestController
public class InfoskjermenApplication {

   @Autowired
    private NetatmoService netatmo;

   @Autowired
   	private CalendarService calendar;

   @Autowired
	private WeatherService weather;


	private String eventer;
	private static String HTML_START = "<HTML><BODY>";
	private static String HTML_END = "</BODY></HTML>";


	public static void main(String[] args) {
		SpringApplication.run(InfoskjermenApplication.class, args);
	}

	@GetMapping("/")
	public String hello() {

		try {
			//return (String)calendar.getCalendarEvents("fredrik").get("something");
			return "hei";
			//return netatmo.getToken("fredrik");
		} catch (Exception e) {
			e.printStackTrace();
			return "error: " + e.getMessage();
		}

	}

	@GetMapping("/kindle")
	public String kindle() {
		Settings settings = new Settings();
		try {
			return "This is my Kindle!"; //settings.hentSettings("fredrik");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "this is my kindle";
	}

	@GetMapping("/calendar")
	public String calendar() {
		eventer = "";
		try {
			calendar.getCalendarEvents("fredrik").forEach(event ->
				eventer = eventer + event.debug() + "<Br/>"
			);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return wrapHTML(eventer);
	}
	@GetMapping("/weather")
	public String weather() throws Exception{
		WeatherData weatherData = weather.getWeatherReport("fredrik");
		eventer="<h1>Hoved</h1>";
		eventer = eventer + weatherData.main.debug().replaceAll("\n","<br/>");
		eventer=  eventer + "<h1>Hoved</h1>";
		weatherData.longtimeForecast.forEach(event ->
				eventer = eventer + event.debug().replaceAll("\n","<br/>"));
		return wrapHTML(eventer);
	}

	public String wrapHTML(String text){
		return HTML_START + text + HTML_END;
	}
}


