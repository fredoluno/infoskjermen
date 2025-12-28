package no.infoskjermen;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import no.infoskjermen.utils.DivUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;

// [END fs_include_dependencies]
@Service
public class Settings {

    private Logger log = LoggerFactory.getLogger(Settings.class);

    private Firestore db;
    private HashMap<String, DocumentSnapshot> userProfiles;

    @Value("${google.cloud.firestore.project-id:infoskjermen}")
    private String projectId;
    

    Settings(){
        userProfiles = new HashMap<> ();

    }
    
    private void initiateDB(){
        if(this.db==null) {
            log.debug("start firestore");
            
            try {
                FirestoreOptions.Builder builder = FirestoreOptions.newBuilder()
                    .setProjectId(projectId);
                
                // Use default credential chain which will pick up environment variables
                
                FirestoreOptions fsOptions = builder.build();
                log.debug("project: " + fsOptions.getProjectId() + ", database: " + fsOptions.getDatabaseId());
                Firestore db2 = fsOptions.getService();
                log.debug("firestore koblet opp");
                this.db = db2;
            } catch (Exception e) {
                log.error("Failed to initialize Firestore: " + e.getMessage());
                throw new RuntimeException("Could not initialize Firestore", e);
            }
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
        try {
            DocumentSnapshot doc  = hentSettings(navn);
            HashMap map = (HashMap)doc.get("netatmo");
            log.debug(DivUtils.printHashMap(map));
            return  map;
        } catch (Exception e) {
            log.warn("Failed to get Netatmo settings from Firestore, using default settings: " + e.getMessage());
            // Return default settings for development
            HashMap defaultSettings = new HashMap();
            defaultSettings.put("client_id", "default");
            defaultSettings.put("client_secret", "default");
            return defaultSettings;
        }
    }


    public HashMap getDisplaySettings(String navn)throws Exception{
        log.debug("getDisplaySettings : " + navn);
        try {
            DocumentSnapshot doc  = hentSettings(navn);
            HashMap map = (HashMap)doc.get("display");
            //log.debug(DivUtils.printHashMap(map));
            return  map;
        } catch (Exception e) {
            log.warn("Failed to get Display settings from Firestore, using default settings: " + e.getMessage());
            // Return default settings for development
            HashMap defaultSettings = new HashMap();
            defaultSettings.put("theme", "default");
            return defaultSettings;
        }
    }




    public HashMap getGoogleSettings(String navn)throws Exception{
        log.debug("getGoogleSettings: " + navn);
        try {
            DocumentSnapshot doc  = hentSettings(navn);
            HashMap map = (HashMap) doc.get("google");
            log.debug(DivUtils.printHashMap(map));
            return  map;
        } catch (Exception e) {
            log.warn("Failed to get Google settings from Firestore, using default settings: " + e.getMessage());
            // Return default settings for development
            HashMap defaultSettings = new HashMap();
            defaultSettings.put("display_calendar", "primary");
            defaultSettings.put("calendar", "primary");
            return defaultSettings;
        }
    }

    public HashMap getYrSettings(String navn)throws Exception{
        log.debug("getYrSettings: " + navn);
        try {
            DocumentSnapshot doc  = hentSettings(navn);
            HashMap map = (HashMap) doc.get("yr");
            log.debug(DivUtils.printHashMap(map));
            return  map;
        } catch (Exception e) {
            log.warn("Failed to get Yr settings from Firestore, using default settings: " + e.getMessage());
            // Return default settings for development
            HashMap defaultSettings = new HashMap();
            defaultSettings.put("latitude", "59.9139");
            defaultSettings.put("longitude", "10.7522");
            return defaultSettings;
        }
    }

    public HashMap getEnturSettings(String navn)throws Exception{
        log.debug("getEnturSettings: " + navn);
        try {
            DocumentSnapshot doc  = hentSettings(navn);
            HashMap map = (HashMap) doc.get("entur");
            log.debug(DivUtils.printHashMap(map));
            return  map;
        } catch (Exception e) {
            log.warn("Failed to get Entur settings from Firestore, using default settings: " + e.getMessage());
            // Return default settings for development
            HashMap defaultSettings = new HashMap();
            defaultSettings.put("stopplace_id", "NSR:StopPlace:58366");
            return defaultSettings;
        }
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
