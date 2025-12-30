package no.infoskjermen.tjenester;

import com.google.cloud.firestore.GeoPoint;
import no.infoskjermen.Settings;
import no.infoskjermen.data.Cache;
import no.infoskjermen.data.WeatherData;
import no.infoskjermen.data.WeatherDataDay;
import no.infoskjermen.data.WeatherDataPeriod;
import no.infoskjermen.utils.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

@Service
public class WeatherService implements PopulateInterface {
    private Logger log = LoggerFactory.getLogger(WeatherService.class);

    private Settings settings;
    private final long expireTime = 60 * 60 * 1000;
    private static final String CLIENT_NAME = "fredoluno@gmail.com - Infoskjermen";
    private static final String URL = "https://api.met.no/weatherapi/locationforecast/2.0/compact.json?lat=%s&lon=%s";

    private static HashMap<String, String> WEATHER_SYMBOL_CONVERSION;

    // Instantiating the static map
    static {
        WEATHER_SYMBOL_CONVERSION = new HashMap<>();
        WEATHER_SYMBOL_CONVERSION.put("clearsky", "1");
        WEATHER_SYMBOL_CONVERSION.put("fair", "2");
        WEATHER_SYMBOL_CONVERSION.put("partlycloudy", "3");
        WEATHER_SYMBOL_CONVERSION.put("cloudy", "4");
        WEATHER_SYMBOL_CONVERSION.put("rainshowers", "5");
        WEATHER_SYMBOL_CONVERSION.put("rainshowersandthunder", "6");
        WEATHER_SYMBOL_CONVERSION.put("sleetshowers", "7");
        WEATHER_SYMBOL_CONVERSION.put("snowshowers", "8");
        WEATHER_SYMBOL_CONVERSION.put("rain", "9");
        WEATHER_SYMBOL_CONVERSION.put("heavyrain", "10");
        WEATHER_SYMBOL_CONVERSION.put("heavyrainandthunder", "11");
        WEATHER_SYMBOL_CONVERSION.put("sleet", "12");
        WEATHER_SYMBOL_CONVERSION.put("snow", "13");
        WEATHER_SYMBOL_CONVERSION.put("snowandthunder", "14");
        WEATHER_SYMBOL_CONVERSION.put("fog", "15");
        WEATHER_SYMBOL_CONVERSION.put("sleetshowersandthunder", "20");
        WEATHER_SYMBOL_CONVERSION.put("snowshowersandthunder", "21");
        WEATHER_SYMBOL_CONVERSION.put("rainandthunder", "22");
        WEATHER_SYMBOL_CONVERSION.put("sleetandthunder", "23");
        WEATHER_SYMBOL_CONVERSION.put("lightrainshowersandthunder", "6");
        WEATHER_SYMBOL_CONVERSION.put("heavyrainshowersandthunder", "11");
        WEATHER_SYMBOL_CONVERSION.put("lightssleetshowersandthunder", "20");
        WEATHER_SYMBOL_CONVERSION.put("heavysleetshowersandthunder", "20");
        WEATHER_SYMBOL_CONVERSION.put("lightssnowshowersandthunder", "14");
        WEATHER_SYMBOL_CONVERSION.put("heavysnowshowersandthunder", "14");
        WEATHER_SYMBOL_CONVERSION.put("lightrainandthunder", "5");
        WEATHER_SYMBOL_CONVERSION.put("lightsleetandthunder", "20");
        WEATHER_SYMBOL_CONVERSION.put("heavysleetandthunder", "20");
        WEATHER_SYMBOL_CONVERSION.put("lightsnowandthunder", "14");
        WEATHER_SYMBOL_CONVERSION.put("heavysnowandthunder", "14");
        WEATHER_SYMBOL_CONVERSION.put("lightrainshowers", "5");
        WEATHER_SYMBOL_CONVERSION.put("heavyrainshowers", "5");
        WEATHER_SYMBOL_CONVERSION.put("lightsleetshowers", "7");
        WEATHER_SYMBOL_CONVERSION.put("heavysleetshowers", "7");
        WEATHER_SYMBOL_CONVERSION.put("lightsnowshowers", "8");
        WEATHER_SYMBOL_CONVERSION.put("heavysnowshowers", "8");
        WEATHER_SYMBOL_CONVERSION.put("lightrain", "9");
        WEATHER_SYMBOL_CONVERSION.put("lightsleet", "12");
        WEATHER_SYMBOL_CONVERSION.put("heavysleet", "12");
        WEATHER_SYMBOL_CONVERSION.put("lightsnow", "13");
        WEATHER_SYMBOL_CONVERSION.put("heavysnow", "13");

    }

    private Cache weatherReports;

    public WeatherService(Settings settings) {
        this.settings = settings;
        weatherReports = new Cache();

    }

    public String getURL(GeoPoint location) {

        return URL.formatted("" + location.getLatitude(), "" + location.getLongitude());
    }

    public WeatherData getWeatherReport(String navn) throws Exception {

        boolean varselFraIdag = true;
        boolean legacy = false;
        HashMap personalSettings = settings.getYrSettings(navn);
        GeoPoint location = personalSettings != null ? (GeoPoint) personalSettings.get("location") : null;
        String url;
        if (location == null) {
            legacy = true;
            url = personalSettings != null && personalSettings.get("url") != null ? (String) personalSettings.get("url")
                    : "";
        } else {
            url = getURL(location);
        }

        WeatherData returnData = (WeatherData) weatherReports.get(url);
        if (returnData != null) {
            log.debug("found in Cache");
            return returnData;
        }
        log.debug("didn't find it in cache. Going to get it");
        WeatherData weatherData = null;

        if (legacy) {
            weatherData = getWeatherData(url);
        } else {
            weatherData = getWeatherFromYrNew(url);
        }

        weatherReports.add(url, weatherData, expireTime);
        return weatherData;
    }

    private WeatherData getWeatherData(String url) throws Exception {
        WeatherData weatherData = new WeatherData();
        Document doc = getWeatherFromYr(url);
        if (doc == null)
            return weatherData;

        Node tabular = doc.getElementsByTagName("tabular").item(0);

        boolean foersteElement = true;
        boolean hovedFerdig = false;
        final int dagPeriode = 2;

        NodeList nodeList = tabular.getChildNodes();
        log.debug("nodeList.getLength()=" + nodeList.getLength());
        WeatherDataDay weatherDay = new WeatherDataDay();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                WeatherDataPeriod data = getWeatherDataPeriod(element);
                weatherDay.date = data.date;
                if (foersteElement && Integer.parseInt(data.period) > dagPeriode) {
                    // det er da kveld og vi henter heller morgendagens varsel.
                    foersteElement = false;
                    weatherData.mainFromToday = false;
                    continue;

                }
                foersteElement = false;

                weatherDay.addMagic(data);

                if (data.period.equals("3")) {
                    weatherDay.evening = data;
                    if (!hovedFerdig) {
                        log.debug("hovedvarsel");
                        weatherData.main = weatherDay;
                        hovedFerdig = true;
                    } else {
                        log.debug("langtidsvarsel");
                        weatherData.longtimeForecast.add(weatherDay);
                    }
                    weatherDay = new WeatherDataDay();
                }
            }
        }
        return weatherData;
    }

    private WeatherDataPeriod getWeatherDataPeriod(Element element) {
        WeatherDataPeriod data = new WeatherDataPeriod();
        data.symbol = ((Element) element.getElementsByTagName("symbol").item(0)).getAttribute("number");
        data.temperature = Integer
                .parseInt(((Element) element.getElementsByTagName("temperature").item(0)).getAttribute("value"));
        data.date = DateTimeUtils.getDateFromYr(element.getAttribute("from"));
        data.period = element.getAttribute("period");
        log.debug(data.debug());
        return data;
    }

    private Document getWeatherFromYr(String url) throws Exception {
        if (url == null || url.equals(""))
            return null;
        log.debug("getWeatherFromYr for " + url);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(url);

        doc.getDocumentElement().normalize();
        log.debug(doc.toString());
        return doc;
    }

    public WeatherData getWeatherFromYrNew(String url) {
        log.debug("URL" + url);

        RestTemplate restTemplate = new RestTemplate();
        // HashMap map = restTemplate.postForObject((String)
        // generalSettings.get("endpoint"), getHttpEntity(personalSettings),
        // HashMap.class);
        ResponseEntity<HashMap> response = restTemplate.exchange(url, HttpMethod.GET, requestYr(), HashMap.class, 1);

        if (response.getStatusCode() != HttpStatus.OK) {
            log.debug("Request Failed");
            log.debug("" + response.getStatusCode());
            return null;
        }

        HashMap map = response.getBody();
        WeatherData weatherData = convertNewYRtoKemKaKorr(map);
        return weatherData;

    }

    private HttpEntity requestYr() {
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("User-Agent", CLIENT_NAME);
        HttpEntity request = new HttpEntity(headers);
        return request;
    }

    private WeatherData convertNewYRtoKemKaKorr(HashMap yr) {
        WeatherData weatherData = new WeatherData();
        WeatherDataDay weatherDay = new WeatherDataDay();
        boolean foersteElement = true;
        boolean hovedFerdig = false;
        final int dagPeriode = 2;

        String test = "";
        List timeslots = (List) ((HashMap) yr.get("properties")).get("timeseries");

        for (Object timeslot : timeslots) {
            LocalDateTime date = DateTimeUtils.getYrDateTime((String) ((HashMap) timeslot).get("time"));
            int period = getPeriod(date);
            if (period >= 0) {
                WeatherDataPeriod data = getWeatherDataPeriodNew((HashMap) timeslot);
                log.debug(data.debug());

                if (foersteElement && period > dagPeriode) {
                    // det er da kveld og vi henter heller morgendagens varsel.
                    foersteElement = false;
                    weatherData.mainFromToday = false;
                    continue;

                }
                if (foersteElement && !DateTimeUtils.erIdag(date)) {
                    weatherData.mainFromToday = false;
                }
                foersteElement = false;

                weatherDay.addMagic(data);

                if (data.period.equals("3")) {
                    weatherDay.evening = data;
                    if (!hovedFerdig) {
                        log.debug("hovedvarsel");
                        weatherData.main = weatherDay;
                        hovedFerdig = true;
                    } else {
                        log.debug("langtidsvarsel");
                        weatherData.longtimeForecast.add(weatherDay);
                    }
                    weatherDay = new WeatherDataDay();

                }

            }
            log.debug(date.toString() + " " + LocalDateTime.now().toString());

        }

        return weatherData;

    }

    private WeatherDataPeriod getWeatherDataPeriodNew(HashMap timeslot) {
        WeatherDataPeriod wd = new WeatherDataPeriod();
        LocalDateTime date = DateTimeUtils.getYrDateTime((String) timeslot.get("time"));
        wd.period = "" + getPeriod(date);
        wd.date = date.toLocalDate();
        wd.symbol = getSymbol((HashMap) timeslot.get("data"));
        wd.temperature = getTemperature((HashMap) timeslot.get("data"));
        wd.windSpeed = getWindSpeed((HashMap) timeslot.get("data"));
        wd.windDirection = getWindDirection((HashMap) timeslot.get("data"));
        return wd;

    }

    private int getTemperature(HashMap timeslot) {
        HashMap instant = (HashMap) timeslot.get("instant");
        HashMap details = (HashMap) instant.get("details");
        Double air_temperature = (Double) details.get("air_temperature");

        return air_temperature.intValue();

    }

    private Double getWindSpeed(HashMap data) {
        HashMap instant = (HashMap) data.get("instant");
        HashMap details = (HashMap) instant.get("details");
        return (Double) details.get("wind_speed");
    }

    private String getWindDirection(HashMap data) {
        HashMap instant = (HashMap) data.get("instant");
        HashMap details = (HashMap) instant.get("details");
        Double degrees = (Double) details.get("wind_from_direction");
        if (degrees == null)
            return "";

        String[] directions = { "N", "NE", "E", "SE", "S", "SW", "W", "NW" };
        return directions[(int) Math.round(((degrees % 360) / 45)) % 8];
    }

    private String getSymbol(HashMap timeslot) {
        HashMap next = (HashMap) timeslot.get("next_6_hours");
        if (next == null) {
            return null;
        }
        HashMap summary = (HashMap) next.get("summary");
        String symbol_code = (String) summary.get("symbol_code");
        // fjerner night day osv
        symbol_code = symbol_code.split("_")[0];

        return WEATHER_SYMBOL_CONVERSION.get(symbol_code);
    }

    private int getPeriod(LocalDateTime date) {
        // log.debug(date.getHour() + " hour= " + date.getHour());
        int period = -1;
        switch (date.getHour()) {
            case 0:
                period = 0;
                break;
            case 6:
                period = 1;
                break;
            case 12:
                period = 2;
                break;
            case 18:
                period = 3;
        }
        return period;
    }

    @Override
    public String populate(String svg, String navn) {
        if (!isPresentInSVG(svg)) {
            log.debug("svg inneholder ikke vær");
            return svg;
        }
        try {
            WeatherData myWeatherData = this.getWeatherReport(navn);
            String tittel = myWeatherData.mainFromToday ? "Været i dag" : "I morgen";
            svg = svg.replaceAll("@@VARSETITTEL@@", tittel);
            if (myWeatherData.main != null) {

                svg = svg.replaceAll("@@VARSELDAG@@", getTemperature(myWeatherData.main.day));
                svg = svg.replaceAll("@@VARSELNATT@@", getTemperature(myWeatherData.main.night));
                svg = svg.replaceAll("@@VARSELMORGEN@@", getTemperature(myWeatherData.main.morning));
                svg = svg.replaceAll("@@VARSELKVELD@@", getTemperature(myWeatherData.main.evening));

                svg = svg.replaceAll("@@SYMBOL@@", "v" + getSymbol(myWeatherData.main.day));
                svg = svg.replaceAll("@@VARSELNATTSYMBOL@@", "v" + getSymbol(myWeatherData.main.night));
                svg = svg.replaceAll("@@VARSELMORGENSYMBOL@@", "v" + getSymbol(myWeatherData.main.morning));
                svg = svg.replaceAll("@@VARSELKVELDSYMBOL@@", "v" + getSymbol(myWeatherData.main.evening));

            }

            svg = populateLongTime(myWeatherData.longtimeForecast, svg);

        } catch (Exception e) {
            log.error("Fikk ikke hentet Været " + e.getMessage());
            e.printStackTrace();
        }

        return svg;
    }

    private String populateLongTime(TreeSet<WeatherDataDay> longtimeForecast, String svg) {
        if (longtimeForecast == null) {
            return svg;
        }
        int i = 0;
        for (WeatherDataDay myPeriod : longtimeForecast) {
            log.debug(myPeriod.debug());
            svg = svg.replaceAll("@@langtidsvarsel." + i + ".dag@@", DateTimeUtils.getDay(myPeriod.date));
            svg = svg.replaceAll("@@langtidsvarsel." + i + ".temperatur@@", getTemperature(myPeriod.night));
            svg = svg.replaceAll("@@langtidsvarsel." + i + ".symbol@@", getSymbol(myPeriod.night));
            i++;
            svg = svg.replaceAll("@@langtidsvarsel." + i + ".dag@@", DateTimeUtils.getDay(myPeriod.date));
            svg = svg.replaceAll("@@langtidsvarsel." + i + ".temperatur@@", getTemperature(myPeriod.morning));
            svg = svg.replaceAll("@@langtidsvarsel." + i + ".symbol@@", getSymbol(myPeriod.morning));
            i++;
            svg = svg.replaceAll("@@langtidsvarsel." + i + ".dag@@", DateTimeUtils.getDay(myPeriod.date));
            svg = svg.replaceAll("@@langtidsvarsel." + i + ".temperatur@@", getTemperature(myPeriod.day));
            svg = svg.replaceAll("@@langtidsvarsel." + i + ".symbol@@", getSymbol(myPeriod.day));
            i++;
            svg = svg.replaceAll("@@langtidsvarsel." + i + ".dag@@", DateTimeUtils.getDay(myPeriod.date));
            svg = svg.replaceAll("@@langtidsvarsel." + i + ".temperatur@@", getTemperature(myPeriod.evening));
            svg = svg.replaceAll("@@langtidsvarsel." + i + ".symbol@@", getSymbol(myPeriod.evening));
            i++;

        }
        return svg;
    }

    private String getTemperature(WeatherDataPeriod period) {
        return period == null ? "" : "" + period.temperature;
    }

    private String getSymbol(WeatherDataPeriod period) {
        return period == null || period.symbol == null ? "" : period.symbol;
    }

    @Override
    public boolean isPresentInSVG(String svg) {
        return svg.contains("@@VARSEL") || svg.contains("@@langtidsvarsel");
    }
}
