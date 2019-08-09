package no.infoskjermen.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.*;
import java.time.format.DateTimeFormatter;


public class DateTimeUtils {

    private static Logger log = LoggerFactory.getLogger(DateTimeUtils.class);

    public static String formatRFC3339(LocalDateTime date){
        DateTimeFormatter formatter = getRFC3339formatter();
        return formatter.format(ZonedDateTime.of(date,ZoneId.systemDefault()));
    }

    private static DateTimeFormatter getRFC3339formatter() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("UTC"));
    }


    public static LocalDateTime getLocalDateTimefromRFC3339(String value) {
        DateTimeFormatter formatter = getRFC3339formatter();

        return LocalDateTime.parse(value,formatter);
    }

    public static LocalDateTime getLocalDateTimefromLong(long l) {
        return Instant.ofEpochMilli(l).atZone(ZoneId.of("Europe/Oslo")).toLocalDateTime();
    }
    public static LocalDateTime getLocalDateTimefromLongDate(long l) {
        return Instant.ofEpochMilli(l).atZone(ZoneId.of("UTC")).toLocalDateTime();
    }


    public static boolean erIdag(LocalDateTime dateToCheck) {
        LocalDateTime now = LocalDate.now().atStartOfDay();
        LocalDateTime tomorrow = now.plusDays(1);

        return dateToCheck.compareTo(now) > 0 && dateToCheck.compareTo(tomorrow) < 0;
    }


    public static boolean erIdag(LocalDateTime fra, LocalDateTime til) {


        LocalDateTime now = LocalDate.now().atStartOfDay();
        LocalDateTime tomorrow = now.plusDays(1);

        return fra.isBefore(tomorrow)  && til.isAfter(now);
    }

    public static boolean erImorgen(LocalDateTime fra, LocalDateTime til) {
        LocalDateTime now = LocalDate.now().atStartOfDay();
        LocalDateTime tomorrow = now.plusDays(1);
        LocalDateTime tomorrowPlusOne = now.plusDays(2);

        return fra.isBefore(tomorrowPlusOne) && til.isAfter(tomorrow);
    }

    public static boolean erInnen7dager(LocalDateTime dato) {
        LocalDateTime now = LocalDate.now().atStartOfDay();
        LocalDateTime tomorrow = now.plusDays(7);

        return dato.isAfter(now) && dato.isBefore(tomorrow);
    }

    public static boolean datoPassert(LocalDateTime dateToCheck){
        LocalDateTime today = LocalDate.now().atStartOfDay();
        log.debug("date: "+ dateToCheck + " now: " + today);
        return dateToCheck.isBefore(today);

    }
    public static boolean erSammeDag(LocalDateTime dato, LocalDateTime dato2) {

        LocalDateTime now = dato.toLocalDate().atStartOfDay();
        LocalDateTime tomorrow = now.plusDays(1).plusMinutes(1);
        //            Logger.debug("Er samme dag");
        return dato2.compareTo(now) > 0 && dato2.compareTo(tomorrow) < 0;
    }

}
