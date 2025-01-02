package no.infoskjermen.tjenester;


import no.infoskjermen.Settings;
import no.infoskjermen.data.Cache;
import no.infoskjermen.data.NetatmoData;
import no.infoskjermen.data.netatmo.NetatmoMeasure;
import no.infoskjermen.data.netatmo.NetatmoToken;
import no.infoskjermen.utils.DateTimeUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;

@Service
public class NetatmoService implements PopulateInterface {

    private static  String TOKENURL ="https://api.netatmo.com/oauth2/token";
    private static  String MEASUREURL="https://api.netatmo.com/api/getmeasure";



    private Logger log = LoggerFactory.getLogger(NetatmoService.class);


    public String inneTemperatur ="NA";
    public String co2 = "";
    public String inneFuktighet= "";
    public String trykk= "";
    public String lyd= "";
    public String uteTemperatur= "NA";
    public String uteFuktighet= "";

    private NetatmoToken token;
    private HashMap token2;

    private String atoken;

    private Settings settings;
    private Cache data;
    private final long expireTime = 2 * 60 * 1000;

    @Autowired
    public NetatmoService(Settings settings){
        this.settings = settings;
        token2 = new HashMap();
        data = new Cache();
    }

    public NetatmoMeasure getIndoorTemperature(String navn) throws Exception {
        log.debug("getIndoorTemperature");
        MultiValueMap<String, String> parameters =  getIndoorPostParameters(navn);
        return getMeasures(navn, parameters);
    }

    public NetatmoMeasure getOutdoorTemperature(String navn) throws Exception {
        log.debug("getOutdoorTemperature");
        MultiValueMap<String, String> parameters =  getOutdoorPostParameters(navn);
        return getMeasures(navn, parameters);
    }

    private NetatmoMeasure getMeasures(String navn, MultiValueMap<String, String> parameters) {
        log.debug("getMeasure");
        RestTemplate restTemplate = new RestTemplate();
        log.debug("URL:" + MEASUREURL);
        log.debug("parameters:" + parameters);
        String cacheKey = parameters.getFirst("module_id");
        log.debug("cacheKey" + cacheKey);
        NetatmoMeasure result =(NetatmoMeasure)data.get(cacheKey);
        if(result !=null){
            log.debug("found in Cache");
            return result;
        }

        result = restTemplate.postForObject(MEASUREURL,parameters, NetatmoMeasure.class);
        assert result != null;
        log.debug("reultat:" + result.getBody().get(0).getValue().get(0));
        data.add(cacheKey,result,expireTime);
        return result;
    }



    private MultiValueMap<String, String> getIndoorPostParameters(String navn) throws Exception {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        HashMap personalSettings = settings.getNetatmoSettings(navn);
        parameters.set("device_id",(String)personalSettings.get("indoor_id"));
        parameters.set("module_id",(String)personalSettings.get("indoor_id"));
        parameters.set("type","Temperature,CO2,Humidity,Pressure,Noise");
        parameters.set("date_end","last");
        parameters.set("scale","max");
        parameters.set("access_token",getToken(navn));
        log.debug("parameters: " + parameters);
        return parameters;
    }

    private MultiValueMap<String, String> getOutdoorPostParameters(String navn) throws Exception {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        HashMap personalSettings = settings.getNetatmoSettings(navn);
        parameters.set("device_id",(String)personalSettings.get("indoor_id"));
        parameters.set("module_id",(String)personalSettings.get("outdoor_id"));
        parameters.set("type","Temperature,Humidity");
        parameters.set("date_end","last");
        parameters.set("scale","max");
        parameters.set("access_token",getToken(navn));
        log.debug("parameters: " + parameters);
        return parameters;
    }

    public NetatmoData getNetatmoData(String navn) throws Exception{
        return  new NetatmoData(getIndoorTemperature(navn), getOutdoorTemperature(navn));
    }



    public String getToken(String navn) throws Exception {
        NetatmoToken token = (NetatmoToken)token2.get(navn);

        if (token != null && token.getAccess_token()!= null){
            log.debug("token funnet: "+ navn );
            
            return token.getAccess_token();
        }
        MultiValueMap<String, String> parameters = getTokenParameters(navn);
        RestTemplate restTemplate = new RestTemplate();
        token = restTemplate.postForObject(TOKENURL,parameters, NetatmoToken.class);

        String access_token = token.getAccess_token();
        settings.setNetatmoRefreshToken(navn,token.getRefresh_token());
        log.debug("access_token" + token.getExpire_in() +  " " +token.getAccess_token());
        token2.put(navn, token);
        settings.clearCache(navn);
        return access_token;
    }

    private MultiValueMap<String, String> getTokenParameters(String navn) throws Exception {
        HashMap generalSettings = settings.getNetatmoSettings("general");
        HashMap personalSettings = settings.getNetatmoSettings(navn);

        log.debug("getToken");
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.set("grant_type", "refresh_token");
        parameters.set("client_id",(String)generalSettings.get("client_id"));
        parameters.set("client_secret", (String)generalSettings.get("client_secret"));
        parameters.set("refresh_token", (String)personalSettings.get("refresh_token"));
        log.debug(""+parameters);
        return parameters;
    }

    @Override
    public String populate(String svg, String navn){
        if(!isPresentInSVG(svg)){
            log.debug("svg inneholder ikke Netatmo");
            return svg;
        }
        try{
            NetatmoData myData = this.getNetatmoData(navn);

            svg = svg.replaceAll("@@INNETEMP@@",""+myData.indoorTemperature) ;
            svg = svg.replaceAll("@@UTETEMP@@", ""+myData.outdoorTemperature);
            svg = svg.replaceAll("@@INNEFUKTIGHET@@", ""+myData.indoorHumidity);
            svg = svg.replaceAll("@@UTEFUKTIGHETT@@", ""+myData.outdoorHumidity);
            svg = svg.replaceAll("@@LYD@@",         ""+myData.noise);
            svg = svg.replaceAll("@@TRYKK@@",       ""+myData.pressure);
            svg = svg.replaceAll("@@CO2@@",         ""+myData.co2);
        }catch(Exception e) {
            log.error("Fikk ikke hentet Netatmo " + e.getMessage());
            e.printStackTrace();
        }

        return svg;
    }

    @Override
    public boolean isPresentInSVG(String svg) {
        return svg.contains("TEMP@@") ||svg.contains("LYD@@")||svg.contains("CO2@@")||svg.contains("TRYKK@@")||svg.contains("FUKTIGHET@@");
    }



}
