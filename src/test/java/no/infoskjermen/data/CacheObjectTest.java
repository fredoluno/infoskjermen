package no.infoskjermen.data;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CacheObjectTest {
    @Test
    public void testCache()throws Exception{
        String str = "String";
        CacheObject object = new CacheObject(str, 100);
        assertThat((String)object.get()).isEqualTo(str);
        Thread.sleep(101);
        assertThat((String)object.get()).isNull();
    }
}
