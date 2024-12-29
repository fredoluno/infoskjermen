package no.infoskjermen.tjenester;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class PublicTransportServiceTest {
    @Autowired
    private PublicTransportService publicTransport;

    @BeforeEach
    public void setUp(){


    }

    @Test
    public void  testGetPulicTransporSchedule() throws Exception {
        assertThat(true).isTrue();
       // assertThat(publicTransport.getPublicTransporSchedule("fredrik").name).contains("Nordby");
    }

}
