package no.infoskjermen;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;

// [END fs_include_dependencies]
@Service
public class Settings {

    private Logger log = LoggerFactory.getLogger(Settings.class);

    private Firestore db;
    private HashMap userProfiles;
    

    public Settings(){
        userProfiles = new HashMap();

    }
    
    private void initiateDB(){
        if(this.db==null) {
            log.debug("start firestore");
            FirestoreOptions fsOptions = FirestoreOptions.getDefaultInstance();
            log.debug("default" + fsOptions.getDatabaseId());
            Firestore db2 = FirestoreOptions.getDefaultInstance().getService();
            log.debug("firestore koblet opp");
            this.db = db2;
        }
        else{
            log.debug("db allerede satt opp");
        }

    }


    public DocumentSnapshot hentSettings(String navn) throws Exception{
        log.debug("HentPersonalSettings");
        DocumentSnapshot doc = (DocumentSnapshot)userProfiles.get(navn);
        if (doc ==null){
            log.debug("fant ikke i map, henter på fra Firestore");
            initiateDB();
            ApiFuture<DocumentSnapshot> qDoc = db.collection("settings").document(navn).get();

             doc = qDoc.get();
             this.userProfiles.put(navn, doc);
        }
        log.debug("doc:" + doc.getId());
        return doc;

    }

    @Cacheable(value="Settings", key="{ #root.methodName, #navn}")
    public HashMap getNetatmoSettings(String navn)throws Exception{
        log.debug("getNetatmoSettings");
        DocumentSnapshot doc  = hentSettings(navn);
        return  (HashMap) doc.get("netatmo");

    }


    @Cacheable(value="Settings", key="{ #root.methodName, #navn}")
    public HashMap getGmailSettings(String navn)throws Exception{
        log.debug("getNetatmoSettings");
        DocumentSnapshot doc  = hentSettings(navn);
        return  (HashMap) doc.get("gmail");
    }

    @CacheEvict(value = "Settings", allEntries = true)
    public void clearCache(){
        userProfiles.clear();
        log.debug("Cache slettet");}
}
