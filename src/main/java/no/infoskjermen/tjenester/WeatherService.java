package no.infoskjermen.tjenester;


import no.infoskjermen.Settings;
import no.infoskjermen.data.Cache;
import no.infoskjermen.data.WeatherData;
import no.infoskjermen.data.WeatherDataDay;
import no.infoskjermen.data.WeatherDataPeriod;
import no.infoskjermen.utils.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.HashMap;
import java.util.TreeSet;

@Service
public class WeatherService implements PopulateInterface {
    private Logger log = LoggerFactory.getLogger(WeatherService.class);


    private Settings settings;
    private final long expireTime = 60 * 60 * 1000;



    private Cache weatherReports;




    @Autowired
    public WeatherService(Settings settings) {
        this.settings = settings;
        weatherReports = new Cache();

    }


    public WeatherData getWeatherReport(String navn) throws  Exception {


        boolean varselFraIdag = true;
        HashMap personalSettings = settings.getYrSettings(navn);
        String url = personalSettings!=null && personalSettings.get("url")!=null?(String) personalSettings.get("url"):"";

        WeatherData returnData = (WeatherData)weatherReports.get(url);
        if(returnData != null) {
            log.debug("found in Cache");
            return returnData;
        }
        log.debug("didn't find it in cache. Going to get it");
        WeatherData weatherData = getWeatherData(url);
        weatherReports.add(url,weatherData,expireTime);
        return weatherData;
    }

    private WeatherData getWeatherData(String url) throws Exception {
        WeatherData weatherData = new WeatherData();
        Document doc = getWeatherFromYr(url);
        if(doc==null) return weatherData;

        Node tabular = doc.getElementsByTagName("tabular").item(0);

        boolean foersteElement = true;
        boolean hovedFerdig = false;
        final int dagPeriode = 2;

        NodeList nodeList = tabular.getChildNodes();
        log.debug("nodeList.getLength()=" + nodeList.getLength());
        WeatherDataDay weatherDay = new WeatherDataDay();
        for (int i = 0; i < nodeList.getLength(); i++){
            Node node = nodeList.item(i)  ;
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                WeatherDataPeriod data = getWeatherDataPeriod(element);
                weatherDay.date = data.date;
                if (foersteElement && Integer.parseInt(data.period) > dagPeriode) {
                    //det er da kveld og vi henter heller morgendagens varsel.
                    foersteElement = false;
                    weatherData.mainFromToday = false;
                    continue;

                }
                foersteElement = false;

                weatherDay.addMagic(data);

                if (data.period.equals("3")) {
                    weatherDay.evening = data;
                    if (!hovedFerdig){
                        log.debug("hovedvarsel");
                        weatherData.main = weatherDay;
                        hovedFerdig = true;
                    }else{
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
        data.temperature = Integer.parseInt(((Element) element.getElementsByTagName("temperature").item(0)).getAttribute("value"));
        data.date = DateTimeUtils.getDateFromYr(element.getAttribute("from"));
        data.period = element.getAttribute("period");
        log.debug( data.debug());
        return data;
    }


    private Document getWeatherFromYr(String url) throws Exception{
        if(url== null || url.equals("")) return null;
        log.debug("getWeatherFromYr for " + url );
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(url);

        doc.getDocumentElement().normalize();
        log.debug(doc.toString());
        return doc;
    }

    @Override
    public String populate(String svg, String navn) {
        if(!isPresentInSVG(svg)){
            log.debug("svg inneholder ikke vær");
            return svg;
        }
        try{
            WeatherData myWeatherData = this.getWeatherReport(navn);
            String tittel = myWeatherData.mainFromToday? "Været i dag": "I morgen";
            svg = svg.replaceAll("@@VARSETITTEL@@", tittel);
            if(myWeatherData.main != null){

                svg = svg.replaceAll("@@VARSELDAG@@",   getTemperature(myWeatherData.main.day));
                svg = svg.replaceAll("@@VARSELNATT@@",  getTemperature(myWeatherData.main.night));
                svg = svg.replaceAll("@@VARSELMORGEN@@",getTemperature(myWeatherData.main.morning));
                svg = svg.replaceAll("@@VARSELKVELD@@", getTemperature(myWeatherData.main.evening));



                svg = svg.replaceAll("@@SYMBOL@@","v" + getSymbol(myWeatherData.main.day));
                svg = svg.replaceAll("@@VARSELNATTSYMBOL@@", "v" +  getSymbol(myWeatherData.main.night));
                svg = svg.replaceAll("@@VARSELMORGENSYMBOL@@","v" + getSymbol(myWeatherData.main.morning));
                svg = svg.replaceAll("@@VARSELKVELDSYMBOL@@","v" +  getSymbol(myWeatherData.main.evening));

            }

            svg = populateLongTime(myWeatherData.longtimeForecast, svg);



        }
        catch(Exception e) {
            log.error("Fikk ikke hentet Været " + e.getMessage());
            e.printStackTrace();
        }

        return svg;
    }

    private String populateLongTime(TreeSet<WeatherDataDay> longtimeForecast, String svg) {
        if (longtimeForecast == null){
            return svg;
        }
        int i = 0;
        for (WeatherDataDay myPeriod: longtimeForecast){
            svg = svg.replaceAll("@@langtidsvarsel."+i+".dag@@",   DateTimeUtils.getDay(myPeriod.date));
            svg = svg.replaceAll("@@langtidsvarsel."+i+".temperatur@@",   getTemperature(myPeriod.night));
            svg = svg.replaceAll("@@langtidsvarsel."+i+".symbol@@",   getSymbol(myPeriod.night));
            i++;
            svg = svg.replaceAll("@@langtidsvarsel."+i+".dag@@",   DateTimeUtils.getDay(myPeriod.date));
            svg = svg.replaceAll("@@langtidsvarsel."+i+".temperatur@@",   getTemperature(myPeriod.morning));
            svg = svg.replaceAll("@@langtidsvarsel."+i+".symbol@@",   getSymbol(myPeriod.morning));
            i++;
            svg = svg.replaceAll("@@langtidsvarsel."+i+".dag@@",   DateTimeUtils.getDay(myPeriod.date));
            svg = svg.replaceAll("@@langtidsvarsel."+i+".temperatur@@",   getTemperature(myPeriod.day));
            svg = svg.replaceAll("@@langtidsvarsel."+i+".symbol@@",   getSymbol(myPeriod.day));
            i++;
            svg = svg.replaceAll("@@langtidsvarsel."+i+".dag@@",   DateTimeUtils.getDay(myPeriod.date));
            svg = svg.replaceAll("@@langtidsvarsel."+i+".temperatur@@",   getTemperature(myPeriod.evening));
            svg = svg.replaceAll("@@langtidsvarsel."+i+".symbol@@",   getSymbol(myPeriod.evening));
            i++;

        }
        return svg;
    }

    private String getTemperature(WeatherDataPeriod period) {
        return period == null?"":""+period.temperature;
    }

    private String getSymbol(WeatherDataPeriod period) {
        return period == null?"":period.symbol;
    }


    @Override
    public boolean isPresentInSVG(String svg) {
        return svg.contains("@@VARSEL")  || svg.contains("@@langtidsvarsel") ;
    }
}









