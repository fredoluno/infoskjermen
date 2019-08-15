package no.infoskjermen.tjenester;

import no.infoskjermen.Settings;
import no.infoskjermen.data.PublicTransportData;
import no.infoskjermen.utils.DivUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

@Service
public class PublicTransportService {

    private static final String STOPPLACE="@@STOPPLACE_ID@@";
    private static final String ET_CLIENT_NAME = "fredoluno@gmail.com - Infoskjermen";

    private Logger log = LoggerFactory.getLogger(PublicTransportService.class);
    private Settings settings;
    private HashMap generalSettings;


    @Autowired
    public PublicTransportService(Settings settings) throws Exception{
        this.settings = settings;
        generalSettings = settings.getEnturSettings("general");
        log.debug("generalSettings: "+ DivUtils.printHashMap(generalSettings));


    }

    public HttpEntity<String> getHttpEntity(HashMap personalSettings){
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        headers.add("ET-Client-Name",ET_CLIENT_NAME );

        return new HttpEntity(getQuery(personalSettings), headers);

    }


    public PublicTransportData getPublicTransporSchedule(String navn) throws Exception{
        HashMap personalSettings  = settings.getEnturSettings(navn);
        RestTemplate restTemplate = new RestTemplate();

        HashMap map = restTemplate.postForObject((String) generalSettings.get("endpoint"), getHttpEntity(personalSettings), HashMap.class);
        log.debug(DivUtils.printHashMap(map));

        return populateData(map, personalSettings);


    }

    private PublicTransportData populateData(HashMap map, HashMap personalSettings) {
        final String lineIds = ((String)personalSettings.get("line_ids")).toLowerCase();
        final String arrivalFronttexts  = ((String)personalSettings.get("arrival_fronttexts")).toLowerCase();
        final String departureFronttexts  = ((String)personalSettings.get("departure_fronttext")).toLowerCase();

        log.debug(""+map.get("data"));
        HashMap stopPlace = (HashMap)((HashMap)map.get("data")).get("stopPlace");
        List estimatedCalls = (List)stopPlace.get("estimatedCalls");
        TreeSet<LocalDateTime> arrivalDates  = new TreeSet<>();
        TreeSet<LocalDateTime> departureDates  = new TreeSet<>();
        for (Object estimatedCall: estimatedCalls) {
            if(lineIds.contains(getLineId((HashMap) estimatedCall))){
                String frontText = getFrontText((HashMap) estimatedCall);
                if(arrivalFronttexts.contains(frontText)) {
                    LocalDateTime date = LocalDateTime.parse((String)((HashMap)estimatedCall).get("expectedArrivalTime"), getFormatter());
                    arrivalDates.add(date);
                }
                else if(departureFronttexts.contains(frontText)) {
                    LocalDateTime date = LocalDateTime.parse((String)((HashMap)estimatedCall).get("expectedDepartureTime"),  getFormatter());
                    departureDates.add(date);
                }
                else{
                    log.warn("frontText was not in any listst:  " +  frontText);
                }

            }


        }

        return new PublicTransportData((String)stopPlace.get("name"), arrivalDates,departureDates);
    }

    private DateTimeFormatter getFormatter(){
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                .appendOffset("+HHmm","Z")
                .toFormatter();
        return formatter  ;
    }

    private String getFrontText(HashMap estimatedCall) {
        return ((String)(((HashMap)estimatedCall.get("destinationDisplay")).get("frontText"))).toLowerCase();
    }

    private String getLineId(HashMap estimatedCall) {
        return ((String)((HashMap)((HashMap)((HashMap)estimatedCall.get("serviceJourney")).get("journeyPattern")).get("line")).get("id")).toLowerCase();
    }

    private String getQuery(HashMap personalSettings) {
        String query = (String)generalSettings.get("query");
        String stopplace = (String)personalSettings.get("stopplace_id");
        log.debug("stopplace" + stopplace);
        query = query.replaceAll(STOPPLACE, stopplace);
        log.debug("query:" + query);
        return query;
    }

}
