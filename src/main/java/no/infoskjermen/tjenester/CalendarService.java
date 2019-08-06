package no.infoskjermen.tjenester;


import no.infoskjermen.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;


@Service
public class CalendarService {

    private Logger log = LoggerFactory.getLogger(CalendarService.class);
    private Settings settings;

    @Autowired
    public CalendarService(Settings settings){
        this.settings = settings;


    }

    public HashMap getCalendarEvents(String navn) throws Exception {
        log.debug("getCalendarEvents");
        HashMap personalSettings = settings.getGmailSettings(navn);

        log.debug(personalSettings.toString());
        return personalSettings;
    }


}
