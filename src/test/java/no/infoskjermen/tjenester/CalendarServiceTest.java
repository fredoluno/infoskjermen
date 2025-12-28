package no.infoskjermen.tjenester;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CalendarServiceTest {

    @Autowired
    private CalendarService calendar;


    @BeforeEach
    public void setUp(){


    }

    @Test
    public void testGetCalendarEvents()throws Exception {

        assertThat(calendar.getCalendarEvents("fredrik")).isNotEmpty();

    }
    @Test
    public  void testPopulate(){
        String test = "dette er en test @@EVENT1@@ og @@EVENT2@@";
        assertThat(calendar.populate(test,"fredrik")).doesNotContain("@@");
    }


}
