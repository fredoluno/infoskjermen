package no.infoskjermen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableCaching
@RestController
public class InfoskjermenApplication {

	public static void main(String[] args) {
		SpringApplication.run(InfoskjermenApplication.class, args);
	}

	@GetMapping("/")
	public String hello() {
		Settings settings = new Settings();
		try {
			//return settings.hentSettings();
			return "hei";
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
