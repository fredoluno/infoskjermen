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
public class CalendarServiceTest {

    @Autowired
    private CalendarService calendar;


    @Before
    public void setUp(){


    }

    @Test
    public void testGetCalendarEvents()throws Exception {

        assertThat(calendar.getCalendarEvents("fredrik")).isNotEmpty();

    }

}
