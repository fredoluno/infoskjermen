package no.infoskjermen;



import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest
public class SettingsTest {
    private Logger log = LoggerFactory.getLogger(SettingsTest.class);

    @Autowired
    private Settings settings;

    @Test
    public void testCaching() throws Exception {
        log.debug("startNetatmoSettings");
        HashMap map = settings.getNetatmoSettings("testSetting");
        log.debug(map.toString());
        log.debug("Hentet settings engang. la oss sjekke om det caches");
         map = settings.getNetatmoSettings("testSetting");
        log.debug(map.toString());
        log.debug("Hentet andre gang");
        HashMap map2 = settings.getGmailSettings("testSetting");
        log.debug(map2.toString());
        log.debug("hentet gmialsettings");
         map2 = settings.getGmailSettings("testSetting");
        log.debug(map2.toString());
        log.debug("hentet gmialsettings");
        settings.clearCache();
        log.debug("------------nå skal ting skje");
        map = settings.getNetatmoSettings("testSetting");
        log.debug("----------hentet netatmo");
        map2 = settings.getGmailSettings("testSetting");

        assertThat(map.get("indoor_id")).isEqualTo("indoor_idVal");

    }
    @Test
    public void testNetatmoSettings() throws Exception {
        log.debug("startNetatmoSettings");
        HashMap map = settings.getNetatmoSettings("testSetting");

        assertThat(map.get("indoor_id")).isEqualTo("indoor_idVal");
    }

    @Test
    public void testGmailSettings() throws Exception {
        log.debug("testGmailSettings");
        HashMap map = settings.getGmailSettings("testSetting");
        assertThat(map.get("something")).isEqualTo("somethingVal");

    }
}
