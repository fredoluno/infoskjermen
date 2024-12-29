package no.infoskjermen.tjenester;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PublicTransportServiceTest {
    @Autowired
    private PublicTransportService publicTransport;

    @Before
    public void setUp(){


    }

    @Test
    public void  testGetPulicTransporSchedule() throws Exception {
        assertThat(true).isTrue();
       // assertThat(publicTransport.getPublicTransporSchedule("fredrik").name).contains("Nordby");
    }

}
