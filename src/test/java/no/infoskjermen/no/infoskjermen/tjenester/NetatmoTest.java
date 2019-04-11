package no.infoskjermen.no.infoskjermen.tjenester;


import no.infoskjermen.Settings;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NetatmoTest {


    private Netatmo netatmo;

    @Autowired
    private Settings settings;

    private HashMap personalSettings;
    private HashMap generalSettings;

    @Before
    public void setUp(){
        try {
            personalSettings= this.settings.getNetatmoSettings("fredrik");
            generalSettings = this.settings.getNetatmoSettings("general");
            netatmo = new Netatmo(generalSettings,personalSettings);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testToken(){
        assertThat(netatmo.getToken()).isNotEmpty();
    }
}
