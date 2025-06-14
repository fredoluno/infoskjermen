package no.infoskjermen;


import io.github.cdimascio.dotenv.Dotenv;
import no.infoskjermen.data.NetatmoData;
import no.infoskjermen.data.PublicTransportData;
import no.infoskjermen.data.WeatherData;
import no.infoskjermen.tjenester.*;
import no.infoskjermen.utils.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

	@Autowired
   private PublicTransportService publicTransport;

	@Autowired
	private DisplayService display;
	@Autowired
	private WatchService watch;



	private String eventer;
	private static String HTML_START = "<HTML><BODY>";
	private static String HTML_END = "</BODY></HTML>";


	public static void main(String[] args) {
		// Load environment variables from .env file
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        // Optionally, you can access variables like:
        // String creds = dotenv.get("GOOGLE_APPLICATION_CREDENTIALS");
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

	@GetMapping("/yrny")
	public String yrny() throws Exception{
		WeatherData per = weather.getWeatherFromYrNew("noe");

		eventer="<h1>Yr Ny</h1>";
		eventer = eventer + per;
		return wrapHTML(eventer);
	}

	@GetMapping("/netatmo")
	public String netatmo() throws Exception{
		NetatmoData data = netatmo.getNetatmoData("fredrik");
		return wrapHTML(""+data.outdoorTemperature);
		
	}


	public String wrapHTML(String text){
		return HTML_START + text + HTML_END;
	}

	@GetMapping("/transport")
	public String transport() throws Exception{
		PublicTransportData data  = publicTransport.getPublicTransporSchedule("fredrik");
		eventer="<h1>"+data.name+"</h1>";
		eventer=  eventer + "<h2>ankomst</h2>";
		data.arrivals.forEach(event ->
				eventer = eventer + DateTimeUtils.getPublicTransportView(event) + ", ");
		eventer=  eventer + "<h2>avgang</h2>";
		data.departures.forEach(event ->
				eventer = eventer + DateTimeUtils.getPublicTransportView(event)+ ", ");

		return wrapHTML(eventer);
	}

	@GetMapping("/display.svg")
	public String svg() throws Exception{
		return svg("fredrik");
	}

	public String svg(String navn) throws Exception{

		String svg = display.getPopulatedSVG(navn);
		svg = display.populate(svg,navn);
		svg = calendar.populate(svg,navn);
		svg = weather.populate(svg, navn);
		svg = netatmo.populate(svg,navn);
		svg = publicTransport.populate(svg,navn);
		svg = watch.populate(svg,navn);

		return svg;
	}


	@GetMapping(value = "/bilde.png", produces=MediaType.IMAGE_PNG_VALUE)
	public byte[] getBildePNG() throws  Exception{
		return display.getKindleBilde(svg()).toByteArray();
	}

	@GetMapping(value = "/bilde.bmp", produces="image/bmp")
	public byte[] getBildeBMP() throws  Exception{
		return display.getBMPBilde(svg()).toByteArray();
	}

	@GetMapping(value = "/skjerm/{navn}.png", produces=MediaType.IMAGE_PNG_VALUE)
	public byte[] getBildePNG(@PathVariable String navn) throws  Exception{
		return display.getKindleBilde(svg(navn)).toByteArray();
	}
	@GetMapping(value = "/clear/{navn}.png", produces=MediaType.IMAGE_PNG_VALUE)
	public byte[] getCleanBildePNG(@PathVariable String navn) throws  Exception{
		display.clearCache(navn);
		return display.getKindleBilde(svg(navn)).toByteArray();
	}
	@GetMapping(value = "/clear/{navn}.svg")
	public String getCleanSvg(@PathVariable String navn) throws  Exception{
		display.clearCache(navn);
		return svg(navn);
	}


}


