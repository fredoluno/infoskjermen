package no.infoskjermen.tjenester;


import no.infoskjermen.Settings;
import no.infoskjermen.data.NetatmoMeasure;
import no.infoskjermen.data.NetatmoToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@Service
public class NetatmoService {

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

    private String atoken;

    private Settings settings;

    @Autowired
    public NetatmoService(Settings settings){
        this.settings = settings;

    }

    public NetatmoMeasure getIndoorTemperature(String navn) throws Exception {
        log.debug("getIndoorTemperature");
        MultiValueMap<String, String> parameters =  getIndoorPostParameters(navn);
        return getMeasures(navn, parameters);
    }

    public NetatmoMeasure getOutdoorTemperature(String navn) throws Exception {
        log.debug("getIndoorTemperature");
        MultiValueMap<String, String> parameters =  getOutdoorPostParameters(navn);
        return getMeasures(navn, parameters);
    }

    private NetatmoMeasure getMeasures(String navn, MultiValueMap<String, String> parameters) throws Exception {
        log.debug("getMeasure");
        RestTemplate restTemplate = new RestTemplate();
        log.debug("URL:" + MEASUREURL);

        NetatmoMeasure result = restTemplate.postForObject(MEASUREURL,parameters, NetatmoMeasure.class);
        assert result != null;
        log.debug("reultat:" + result.getBody().get(0).getValue().get(0));

        return result;
    }

    private MultiValueMap<String, String> getIndoorPostParameters(String navn) throws Exception {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
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
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
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




    public String getToken(String navn) throws Exception {
        if (token != null && token.getAccess_token()!= null){
            log.debug("token funnet");
            return token.getAccess_token();
        }
        MultiValueMap<String, String> parameters = getTokenParameters(navn);

        RestTemplate restTemplate = new RestTemplate();
        token = restTemplate.postForObject(TOKENURL,parameters, NetatmoToken.class);
        log.debug(token.getAccess_token());
        atoken = token.getAccess_token();
        return atoken;
    }

    private MultiValueMap<String, String> getTokenParameters(String navn) throws Exception {
        HashMap generalSettings = settings.getNetatmoSettings("general");
        HashMap personalSettings = settings.getNetatmoSettings(navn);

        log.debug("getToken");
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
        parameters.set("grant_type", "refresh_token");
        parameters.set("client_id",(String)generalSettings.get("client_id"));
        parameters.set("client_secret", (String)generalSettings.get("client_secret"));
        parameters.set("refresh_token", (String)personalSettings.get("refresh_token"));
        log.debug(""+parameters);
        return parameters;
    }


}