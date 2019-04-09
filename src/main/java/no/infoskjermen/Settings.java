package no.infoskjermen;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

// [END fs_include_dependencies]

public class Settings {

    private Logger log = LoggerFactory.getLogger(Settings.class);

    private Firestore db;
    

    public Settings(){
        initiateDB();

    }
    
    private void initiateDB(){
        FirestoreOptions fsOptions = FirestoreOptions.getDefaultInstance();
        log.debug("default" + fsOptions.getDatabaseId());
        Firestore db =  FirestoreOptions.getDefaultInstance().getService();
        log.debug("firestore koblet opp");
        this.db = db;
    }

    public String hentSettings() throws Exception{
        // [START fs_add_query]
        // asynchronously query for all users born before 1900
        ApiFuture<QuerySnapshot> query =
                db.collection("settings").limit(1).get();
        String setting  ="";
        // ...
        // query.get() blocks on response
        QuerySnapshot querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
        for (QueryDocumentSnapshot document : documents) {
            
            log.debug("User: " + document.getId());

            System.out.println("test: " + document.getString("test"));
            setting = document.getString("test");
        }
        // [END fs_add_query]
        return  setting;
    }

}
