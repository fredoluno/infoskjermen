package no.infoskjermen.tjenester;


import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import no.infoskjermen.Settings;
import no.infoskjermen.utils.DivUtils;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;

@Service
public class DisplayService extends GoogleService {

    private final String DEFAULT="default";
    private final String PERSONAL = ".personal";

    public DisplayService(Settings settings) throws Exception{
        super(settings);
        log = LoggerFactory.getLogger(DisplayService.class);
        log.debug("generalSettings: "+ DivUtils.printHashMap(generalSettings));

    }

    public String getDisplay(String navn) throws Exception{
        log.debug("getDisplay");
        HashMap personalSettings  = settings.getGoogleSettings(navn);

        Events events = getEvents(personalSettings, LocalDateTime.now(), LocalDateTime.now().plusSeconds(1),this.DISPLAY_CALENDAR);
        String displayName = DEFAULT;
        if( events.getItems().size() > 0){
            Event event = events.getItems().get(0);
            displayName = event.getSummary();
        }
        log.debug("displayName: " + displayName);


        String svg = getSVG(displayName, navn );
        log.debug("svg: " + svg);

        return getSVG(displayName, navn );
    }



    private String getSVG(String displayName, String navn) throws Exception {
        HashMap<String,String> displayMap;
        String displayN = displayName;
        if(displayName.endsWith(PERSONAL)){
            displayMap = settings.getDisplaySettings(navn);
            displayN = displayName.replaceAll(PERSONAL,"");
        }else{
            displayMap = settings.getDisplaySettings("general");
        }

        return displayMap.get(displayN);

    }


}
