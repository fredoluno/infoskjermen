package no.infoskjermen.tjenester;


import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import no.infoskjermen.Settings;
import no.infoskjermen.data.CalendarEvent;
import no.infoskjermen.utils.DivUtils;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.TreeSet;


@Service
public class CalendarService extends GoogleService implements PopulateInterface{


    public CalendarService(Settings settings) throws Exception{
        super(settings);
        log = LoggerFactory.getLogger(CalendarService.class);
        log.debug("generalSettings: "+ DivUtils.printHashMap(generalSettings));

    }

    public TreeSet<CalendarEvent> getCalendarEvents(String navn) throws Exception {
        log.debug("getCalendarEvents");
        HashMap personalSettings  = settings.getGoogleSettings(navn);
        TreeSet<CalendarEvent> calEvents = new TreeSet<>();

        String calendarsStr =(String)personalSettings.get(this.CALENDAR);
        if (calendarsStr == null) return calEvents;
        String refresh_token = (String)personalSettings.get("refresh_token");
        String[] calendars = calendarsStr.split(",");
        for (String calendar : calendars){
            log.debug("Henter fra calender: " + calendar);
            Events events = getEvents(refresh_token, LocalDateTime.now(), LocalDateTime.now().plusYears(1),calendar.trim());
            log.debug("number of events:  " + events.size());
            for( Event googleEvent: events.getItems()){

                CalendarEvent event = populateEvent(googleEvent);
                calEvents.add(event);
            }
        }

        logEventer(calEvents);

        log.debug(personalSettings.toString());
        
        
        
        return calEvents ;
    }

    private void logEventer(TreeSet<CalendarEvent> events)
    {
        log.debug("sortert");
        events.forEach(event -> log.debug(event.debug())
                );
    }

    private CalendarEvent populateEvent(Event googleEvent ) {
        CalendarEvent eventen = new CalendarEvent();
        eventen.title = googleEvent.getSummary();
        eventen.eventStart = getDateTime(googleEvent.getStart());
        eventen.eventEnd = getDateTime(googleEvent.getEnd());
        log.debug(eventen.debug());
        return eventen;
    }

    @Override
    public String populate(String svg, String navn){
        if(!isPresentInSVG(svg)){
            log.debug("svg inneholder ikke kalenderinformasjon");
            return svg;
        }
        try{
            TreeSet<CalendarEvent> myEvents = this.getCalendarEvents(navn);
            int i = 1;
            for(CalendarEvent myEvent: myEvents){
                svg = svg.replaceAll("@@EVENT"+i+"@@", myEvent.print());
                i++;
            }
        }catch(Exception e) {
            log.error("Fikk ikke hentet Calender " + e.getMessage());
            e.printStackTrace();
        }

        return svg;
    }

    @Override
    public boolean isPresentInSVG(String svg) {
        return svg.contains("@@EVENT");
    }


}
