package no.infoskjermen;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import no.infoskjermen.utils.DivUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;

// [END fs_include_dependencies]
@Service
public class Settings {

    private Logger log = LoggerFactory.getLogger(Settings.class);

    private Firestore db;
    private HashMap<String, DocumentSnapshot> userProfiles;

  
    

    Settings(){
        userProfiles = new HashMap<> ();

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

    public void clearCache(String navn){
        userProfiles.remove(navn);

    }

    private DocumentSnapshot hentSettings(String navn) throws Exception{
        log.debug("HentPersonalSettings");
        DocumentSnapshot doc = userProfiles.get(navn);
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


    public HashMap getNetatmoSettings(String navn)throws Exception{
        log.debug("getNetatmoSettings : " + navn);
        DocumentSnapshot doc  = hentSettings(navn);
        HashMap map = (HashMap)doc.get("netatmo");
        log.debug(DivUtils.printHashMap(map));
        return  map;
    }


    public HashMap getDisplaySettings(String navn)throws Exception{
        log.debug("getDisplaySettings : " + navn);
        DocumentSnapshot doc  = hentSettings(navn);
        HashMap map = (HashMap)doc.get("display");
        //log.debug(DivUtils.printHashMap(map));
        return  map;
    }




    public HashMap getGoogleSettings(String navn)throws Exception{
        log.debug("getGoogleSettings: " + navn);
        DocumentSnapshot doc  = hentSettings(navn);
        HashMap map = (HashMap) doc.get("google");
        log.debug(DivUtils.printHashMap(map));
        return  map;
    }

    public HashMap getYrSettings(String navn)throws Exception{
        log.debug("getYrSettings: " + navn);
        DocumentSnapshot doc  = hentSettings(navn);
        HashMap map = (HashMap) doc.get("yr");
        log.debug(DivUtils.printHashMap(map));
        return  map;
    }

    public HashMap getEnturSettings(String navn)throws Exception{
        log.debug("getEnturSettings: " + navn);
        DocumentSnapshot doc  = hentSettings(navn);
        HashMap map = (HashMap) doc.get("entur");
        log.debug(DivUtils.printHashMap(map));
        return  map;
    }

    public boolean setNetatmoRefreshToken(String navn, String refresh_token)throws Exception{
        log.debug("setNetatmoRefreshToken:"+refresh_token);
        initiateDB();
        HashMap map = getNetatmoSettings(navn);
        map.put("refresh_token", refresh_token);
        

        db.collection("settings").document(navn).update("netatmo", map);
        return true;

    }



}
