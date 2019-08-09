package no.infoskjermen.tjenester;


import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import no.infoskjermen.Settings;
import no.infoskjermen.data.CalendarEvent;
import no.infoskjermen.utils.DateTimeUtils;
import no.infoskjermen.utils.DivUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.TreeSet;


@Service
public class CalendarService {

    private Logger log = LoggerFactory.getLogger(CalendarService.class);
    private Settings settings;
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private HashMap generalSettings;


    @Autowired
    public CalendarService(Settings settings) throws Exception{
        this.settings = settings;
        generalSettings = settings.getGoogleSettings("general");
        log.debug("generalSettings: "+ DivUtils.printHashMap(generalSettings));



    }

    public TreeSet<CalendarEvent> getCalendarEvents(String navn) throws Exception {
        log.debug("getCalendarEvents");
        HashMap personalSettings  = settings.getGoogleSettings(navn);
        TreeSet<CalendarEvent> calEvents = new TreeSet<>();
        Events events = getEvents(personalSettings, LocalDateTime.now(), LocalDateTime.now().plusYears(1));
        log.debug("number of events:  " + events.size());
        for( Event googleEvent: events.getItems()){

            CalendarEvent event = populateEvent(googleEvent);
            calEvents.add(event);
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

    private LocalDateTime getDateTime(EventDateTime eventTime) {
        if(eventTime.getDateTime() != null)
            return DateTimeUtils.getLocalDateTimefromLong(eventTime.getDateTime().getValue());

        return DateTimeUtils.getLocalDateTimefromLongDate(eventTime.getDate().getValue());
    }


    private Events getEvents(HashMap personalSettings, LocalDateTime from, LocalDateTime to) throws java.io.IOException {

        log.debug("getEvents from=" + DateTimeUtils.formatRFC3339(from) + " to=" + DateTimeUtils.formatRFC3339(to)  +" calendar=" + personalSettings.get("calendar"));

        Calendar client = new Calendar.Builder(new NetHttpTransport(), JSON_FACTORY, createCredential((String)personalSettings.get("refresh_token")))
                .setApplicationName("Infoskjermen")
                .build();
        return client.events().list((String)personalSettings.get("calendar"))
                .setTimeMin(new com.google.api.client.util.DateTime(DateTimeUtils.formatRFC3339(from)))
                .setTimeMax(new com.google.api.client.util.DateTime(DateTimeUtils.formatRFC3339(to)))
                .setSingleEvents(true)
                .execute();
    }


    private Credential createCredential(String navn) {
        return new Credential.Builder(BearerToken.authorizationHeaderAccessMethod()).setTransport(
                new NetHttpTransport())
                .setJsonFactory(new JacksonFactory())
                .setTokenServerUrl(
                        new GenericUrl((String)generalSettings.get("token_url")))
                .setClientAuthentication(new ClientParametersAuthentication((String)generalSettings.get("client_id"), (String)generalSettings.get("client_secret")))
                .build()
                .setRefreshToken(navn);

    }




}
