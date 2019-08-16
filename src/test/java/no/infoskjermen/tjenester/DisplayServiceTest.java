package no.infoskjermen.tjenester;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DisplayServiceTest {

    @Autowired
    private DisplayService display;

    @Test
    public void test()throws Exception {

        assertThat(display.getDisplay("fredrik")).isNotEmpty();

    }
}
