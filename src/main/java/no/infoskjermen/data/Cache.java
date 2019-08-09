package no.infoskjermen.data;

import java.util.HashMap;

public class Cache {

    private final HashMap<String, CacheObject> cache = new HashMap<>() ;

    public void add(String key, Object value, long periodInMillis) {
        if (key == null) {
            return;
        }
        if (value == null) {
            cache.remove(key);
        } else {

            cache.put(key, new CacheObject(value, periodInMillis));
        }
    }


    public void remove(String key) {
        cache.remove(key);
    }

    public Object get(String key) {
        CacheObject co = cache.get(key);
        return  co == null? null: co.get();
    }



}
