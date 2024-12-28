package no.infoskjermen;



import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest
public class SettingsTest {
    private Logger log = LoggerFactory.getLogger(SettingsTest.class);

    @Autowired
    private Settings settings;


    @Test
    public void testNetatmoSettings() throws Exception {
        log.debug("startNetatmoSettings");
        HashMap map = settings.getNetatmoSettings("testSetting");

        assertThat(map.get("indoor_id")).isEqualTo("indoor_idVal");
    }

    @Test
    public void testGmailSettings() throws Exception {
        log.debug("testGmailSettings");
        HashMap map = settings.getGoogleSettings("testSetting");
        assertThat(map.get("calendar")).isEqualTo("minkalender");

    }

    @Test
    public void testEnturSettings() throws Exception {
        log.debug("testGmailSettings");
        HashMap map = settings.getEnturSettings("testSetting");
        assertThat(map.get("endpoint")).isEqualTo("something");

    }
    @Test
    public void testSetNetatmoRefreshToken() throws Exception {
        log.debug("testGmailSettings");
        UUID uuid = UUID.randomUUID();
        String uuidAsString = uuid.toString();
        boolean ret = settings.setNetatmoRefreshToken("testSetting", uuidAsString);
        settings.clearCache("testSetting");
        HashMap map = settings.getNetatmoSettings("testSetting");
        assertThat(map.get("refresh_token")).isEqualTo(uuidAsString);
        
        


    }
}
