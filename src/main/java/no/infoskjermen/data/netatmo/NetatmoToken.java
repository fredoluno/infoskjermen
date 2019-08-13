package no.infoskjermen.data.netatmo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

public class NetatmoToken {
    private String access_token;
    private String refresh_token;
    private Integer expires_in;
    private Integer expire_in;
    private Calendar expire;

    private Logger log = LoggerFactory.getLogger(NetatmoToken.class);

    public String getAccess_token() {
        log.debug("Tokentid: "+ expire.compareTo(Calendar.getInstance()));

        if(expire.compareTo(Calendar.getInstance())>0){
            return access_token;
        }

        return null;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getRefresh_token() {

        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public Integer getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(Integer expires_in) {
        expire =  Calendar.getInstance();

        expire.add(Calendar.SECOND, expires_in);

        this.expires_in = expires_in;
    }

    public Integer getExpire_in() {
        return expire_in;
    }

    public void setExpire_in(Integer expire_in) {
        this.expire_in = expire_in;
    }


}


