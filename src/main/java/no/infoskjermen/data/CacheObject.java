package no.infoskjermen.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheObject {
    private Logger log = LoggerFactory.getLogger(CacheObject.class);

    private Object store;
    private long expiryTime;

    public CacheObject(Object store, long expireIn){
        log.debug("Cache="+ store.toString() + " expireIn=" + expireIn);
        this.expiryTime = System.currentTimeMillis() + expireIn ;
        this.store = store;

    }


    public Object get(){
        if(isExpired()){
            log.debug("Expired Cache="+ store.toString() );
            return null;
        }

        return store;
    }

    private boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }




}
