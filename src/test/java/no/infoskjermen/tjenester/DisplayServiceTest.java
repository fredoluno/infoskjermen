package no.infoskjermen.tjenester;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class DisplayServiceTest {

    @Autowired
    private DisplayService display;

    @Test
    public void test()throws Exception {

        assertThat(display.getPopulatedSVG("fredrik")).isNotEmpty();

    }
}
