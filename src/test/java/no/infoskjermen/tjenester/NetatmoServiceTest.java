package no.infoskjermen.tjenester;


import no.infoskjermen.data.NetatmoToken;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NetatmoServiceTest {

    @Autowired
    private NetatmoService netatmo;



    @Before
    public void setUp(){


    }

    @Test
    public void testToken() throws Exception {
        netatmo.getToken("fredrik");

        assertThat(netatmo.getToken("fredrik")).isNotEmpty();
    }

    @Test
    public void testGetIndoorTemperature() throws Exception {

        assertThat(netatmo.getIndoorTemperature("fredrik").getStatus()).isNotEmpty();
    }
    @Test
    public void testGetOutdoorTemperature() throws Exception {

        assertThat(netatmo.getOutdoorTemperature("fredrik").getStatus()).isNotEmpty();
    }


    @Test
    public void testNetatmoToken1()  {

        NetatmoToken token = new NetatmoToken();
        token.setAccess_token("adad");
        token.setExpires_in(-1);
        assertThat(token.getAccess_token()).isNull();

    }
    @Test
    public void testNetatmoToken2() {

        NetatmoToken token = new NetatmoToken();
        token.setAccess_token("adad");
        token.setExpires_in(1);
        assertThat(token.getAccess_token()).isEqualTo("adad");

    }

}
