package no.infoskjermen;


import no.infoskjermen.tjenester.CalendarService;
import no.infoskjermen.tjenester.NetatmoService;
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

	private String eventer;



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
		eventer = "<HTML><BODY>";
		try {
			calendar.getCalendarEvents("fredrik").forEach(event ->
				eventer = eventer + event.debug() + "<Br/>"
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		eventer = eventer + "</BODY></HTML>";
		return eventer;

	}
}


