package no.infoskjermen.data;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CacheTest {

    @Test
    public void testCache() throws Exception {
        String str = "String";
        Cache cache = new Cache();
        cache.add("key", str, 100);
        assertThat((String) cache.get("key")).isEqualTo(str);
        Thread.sleep(101);
        assertThat((String) cache.get("key")).isNull();
    }
}