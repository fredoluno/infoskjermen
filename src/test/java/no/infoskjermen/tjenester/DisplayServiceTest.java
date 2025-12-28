package no.infoskjermen.tjenester;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class DisplayServiceTest {

    @Autowired
    private DisplayService display;

    @Test
    public void test()throws Exception {

        assertThat(display.getPopulatedSVG("fredrik")).isNotEmpty();

    }
}
