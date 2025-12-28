package no.infoskjermen.tjenester;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import no.infoskjermen.Settings;
import no.infoskjermen.utils.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashMap;

public class GoogleService {
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();


    protected Logger log = LoggerFactory.getLogger(GoogleService.class);
    protected Settings settings;
    protected HashMap generalSettings;

    protected final String DISPLAY_CALENDAR="display_calendar";
    protected final String CALENDAR="calendar";

    public GoogleService(Settings settings)throws Exception {
        this.settings = settings;
        generalSettings = settings.getGoogleSettings("general");
    }

    protected LocalDateTime getDateTime(EventDateTime eventTime) {
        if(eventTime.getDateTime() != null)
            return DateTimeUtils.getLocalDateTimefromLong(eventTime.getDateTime().getValue());

        return DateTimeUtils.getLocalDateTimefromLongDate(eventTime.getDate().getValue());
    }

    protected Events getEvents(HashMap personalSettings, LocalDateTime from, LocalDateTime to, String calendarName) throws java.io.IOException {


        log.debug("getEvents from=" + DateTimeUtils.formatRFC3339(from) + " to=" + DateTimeUtils.formatRFC3339(to)  +" calendar=" + personalSettings.get(calendarName));
        return getEvents((String)personalSettings.get("refresh_token"),from,to,(String)personalSettings.get(calendarName));

    }


    protected  Events getEvents(String refresh_token,LocalDateTime from, LocalDateTime to, String calendarID )  throws java.io.IOException{
        Calendar client = new Calendar.Builder(new NetHttpTransport(), JSON_FACTORY, createCredential(refresh_token))
                .setApplicationName("Infoskjermen")
                .build();
        return client.events().list(calendarID)
                .setTimeMin(new com.google.api.client.util.DateTime(DateTimeUtils.formatRFC3339(from)))
                .setTimeMax(new com.google.api.client.util.DateTime(DateTimeUtils.formatRFC3339(to)))
                .setSingleEvents(true)
                .execute();

    }

    private Credential createCredential(String navn) {
        return new Credential.Builder(BearerToken.authorizationHeaderAccessMethod()).setTransport(
                new NetHttpTransport())
                .setJsonFactory(new GsonFactory())
                .setTokenServerUrl(
                        new GenericUrl((String)generalSettings.get("token_url")))
                .setClientAuthentication(new ClientParametersAuthentication((String)generalSettings.get("client_id"), (String)generalSettings.get("client_secret")))
                .build()
                .setRefreshToken(navn);

    }
}
