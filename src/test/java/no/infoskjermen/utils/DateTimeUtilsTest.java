package no.infoskjermen.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class DateTimeUtilsTest {

    @Test
    public void testErIdag(){
        LocalDateTime idag = LocalDateTime.now();
        assertThat(DateTimeUtils.erIdag(idag)).isTrue();
        assertThat(DateTimeUtils.erIdag(idag.plusDays(1))).isFalse();
        assertThat(DateTimeUtils.erIdag(idag.minusDays(1))).isFalse();

    }

    @Test
    public void testErIdag2(){
        LocalDateTime idag = LocalDateTime.now();
        assertThat(DateTimeUtils.erIdag(idag.minusDays(1),idag)).isTrue();
        assertThat(DateTimeUtils.erIdag(idag,idag.plusDays(1))).isTrue();
        assertThat(DateTimeUtils.erIdag(idag.plusDays(1),idag.plusDays(1))).isFalse();

    }

    @Test
    public void testErImorgen(){
        LocalDateTime imorgen = LocalDateTime.now().plusDays(1);
        assertThat(DateTimeUtils.erImorgen(imorgen.minusDays(1),imorgen)).isTrue();
        assertThat(DateTimeUtils.erImorgen(imorgen,imorgen.plusDays(1))).isTrue();
        assertThat(DateTimeUtils.erImorgen(imorgen.plusDays(1),imorgen.plusDays(1))).isFalse();

    }



    @Test
    public void testErInnen7dager(){
        LocalDateTime idag = LocalDateTime.now();
        assertThat(DateTimeUtils.erInnen7dager(idag.minusDays(1))).isFalse();
        assertThat(DateTimeUtils.erInnen7dager(idag.plusDays(6))).isTrue();
        assertThat(DateTimeUtils.erInnen7dager(idag)).isTrue();
        assertThat(DateTimeUtils.erInnen7dager(idag.toLocalDate().atStartOfDay().plusDays(7).minusMinutes(1))).isTrue();
        assertThat(DateTimeUtils.erInnen7dager(idag.toLocalDate().atStartOfDay().plusMinutes(1).plusDays(7))).isFalse();
    }

    @Test
    public void testDatoPassert(){
        LocalDateTime idag = LocalDateTime.now();
        assertThat(DateTimeUtils.datoPassert(idag.minusDays(1))).isTrue();
        assertThat(DateTimeUtils.datoPassert(idag)).isFalse();


    }
    @Test
    public void testErSammeDag(){
        LocalDateTime idag = LocalDateTime.now();
        assertThat(DateTimeUtils.erSammeDag(idag,idag.minusDays(1))).isFalse();
        assertThat(DateTimeUtils.erSammeDag(idag,idag)).isTrue();
        assertThat(DateTimeUtils.erSammeDag(idag,idag.plusDays(1))).isFalse();

        assertThat(DateTimeUtils.erSammeDag(idag,idag.toLocalDate().atStartOfDay().plusDays(1).minusMinutes(1))).isTrue();





    }

    @Test
    public void testPublicTransportView(){
        LocalDateTime dateToTest = LocalDateTime.now();
        assertThat(Integer.parseInt(DateTimeUtils.getPublicTransportView(dateToTest.plusMinutes(5).plusSeconds(1)))).isEqualTo(5);
        assertThat(DateTimeUtils.getPublicTransportView(dateToTest.plusMinutes(121).plusSeconds(1))).contains(":");
        assertThat(DateTimeUtils.getPublicTransportView(dateToTest.plusHours(24).plusSeconds(1))).contains("timer");

    }

}


