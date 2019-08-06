package no.infoskjermen;

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






	public static void main(String[] args) {
		SpringApplication.run(InfoskjermenApplication.class, args);
	}

	@GetMapping("/")
	public String hello() {

		try {
            //return "hei";
			return netatmo.getToken("fredrik");
		} catch (Exception e) {
			e.printStackTrace();
			return "error: " + e.getMessage();
		}

	}

	@GetMapping("/kindle")
	public String kindle() {
		Settings settings = new Settings();
		try {
			return ""; //settings.hentSettings("fredrik");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "this is my kindle";
	}
}
