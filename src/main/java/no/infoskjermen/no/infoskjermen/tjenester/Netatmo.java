package no.infoskjermen.no.infoskjermen.tjenester;


import no.infoskjermen.no.infoskjermen.data.NetatmoToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@Service
public class Netatmo {

    private static String TOKENURL ="https://api.netatmo.com/oauth2/token";

    private  HashMap generalSettings;
    private  HashMap personalSettings;


    private Logger log = LoggerFactory.getLogger(Netatmo.class);


    public String inneTemperatur ="NA";
    public String co2 = "";
    public String inneFuktighet= "";
    public String trykk= "";
    public String lyd= "";
    public String uteTemperatur= "NA";
    public String uteFuktighet= "";


    public  Netatmo(HashMap generalSettings,  HashMap personalSettings){
            this.generalSettings = generalSettings;
            this.personalSettings = personalSettings;
            log.debug(generalSettings.toString());
            log.debug(personalSettings.toString());
    }






    public String getToken(){
        log.debug("getToken");
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
        parameters.set("grant_type", "refresh_token");
        parameters.set("client_id",(String)generalSettings.get("client_id"));
        parameters.set("client_secret", (String)generalSettings.get("client_secret"));
        parameters.set("refresh_token", (String)personalSettings.get("refresh_token"));
        log.debug(""+parameters);

        RestTemplate restTemplate = new RestTemplate();
        NetatmoToken token = restTemplate.postForObject(TOKENURL,parameters, NetatmoToken.class);
        //log.debug(token.getAccess_token());

        log.debug(token.getAccess_token());
        return token.getAccess_token();
    }




}
