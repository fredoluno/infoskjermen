package no.infoskjermen.data;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class CalendarEventTest {

    private Logger log = LoggerFactory.getLogger(CalendarEventTest.class);

    @Test
    public void testPrintToday(){
        CalendarEvent event = new CalendarEvent();
        event.eventStart = LocalDate.now().atStartOfDay().plusHours(10);
        event.eventEnd = event.eventStart.plusHours(1);
        log.debug(event.printDato());
        assertThat(event.printDato()).isEqualTo("10:00-11:00");
    }

    @Test
    public void testPrintTodayLater(){
        CalendarEvent event = new CalendarEvent();
        event.eventStart = LocalDate.now().atStartOfDay().plusHours(16);
        event.eventEnd = event.eventStart.plusHours(1);
        log.debug(event.printDato());
        assertThat(event.printDato()).isEqualTo("16:00-17:00");
    }

    @Test
    public void testPrintThisWeek(){
        CalendarEvent event = new CalendarEvent();
        event.eventStart = LocalDateTime.now().plusDays(1);
        event.eventEnd = event.eventStart.plusHours(1);
        log.debug(event.printDato());
        assertThat(event.printDato()).doesNotContain("-");
    }
    @Test
    public void testPrintThisWeek2(){
        CalendarEvent event = new CalendarEvent();
        event.eventStart = LocalDateTime.now().plusDays(1);
        event.eventEnd = event.eventStart.plusDays(1);
        log.debug(event.printDato());
        assertThat(event.printDato()).contains("-");
    }

    @Test
    public void testPrintLongerAhead(){
        CalendarEvent event = new CalendarEvent();
        event.eventStart = LocalDateTime.now().plusDays(7);
        event.eventEnd = event.eventStart.plusDays(1);
        log.debug(event.printDato());
        assertThat(event.printDato()).contains("/");
    }


}
