package no.infoskjermen.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;


public class DateTimeUtils {

    public static String formatRFC3339(LocalDateTime date){
        DateTimeFormatter formatter = getRFC3339formatter();
        return formatter.format(ZonedDateTime.of(date,ZoneId.of("Europe/Oslo")));
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


    public static boolean erIdag(LocalDateTime dateToCheck) {
        LocalDateTime now = LocalDate.now().atStartOfDay();
        LocalDateTime tomorrow = now.plusDays(1);

        return dateToCheck.compareTo(now) > 0 && dateToCheck.compareTo(tomorrow) < 0;
    }


    public static boolean erIdag(LocalDateTime fra, LocalDateTime til) {


        LocalDateTime now = LocalDate.now().atStartOfDay();
        LocalDateTime tomorrow = now.plusDays(1);

        return fra.compareTo(tomorrow) < 0 && til.compareTo(now) > 0;
    }

    public static boolean erImorgen(LocalDateTime fra, LocalDateTime til) {
        LocalDateTime now = LocalDate.now().atStartOfDay();
        LocalDateTime tomorrow = now.plusDays(1);
        LocalDateTime tomorrowPlusOne = now.plusDays(2);

        return fra.compareTo(tomorrowPlusOne) < 0 && til.compareTo(tomorrow) > 0;
    }

    public static boolean erInnen7dager(LocalDateTime dato) {
        LocalDateTime now = LocalDate.now().atStartOfDay();
        LocalDateTime tomorrow = now.plusDays(7);

        return dato.compareTo(now) > 0 && dato.compareTo(tomorrow) <= 0;
    }

    public static boolean datoPassert(LocalDateTime dateToCheck){
        LocalDateTime today = LocalDate.now().atStartOfDay();
        return dateToCheck.compareTo(today) <= 0;

    }
    public static boolean erSammeDag(LocalDateTime dato, LocalDateTime dato2) {

        LocalDateTime now = dato.toLocalDate().atStartOfDay();
        LocalDateTime tomorrow = now.plusDays(1).plusMinutes(1);
        //            Logger.debug("Er samme dag");
        return dato2.compareTo(now) > 0 && dato2.compareTo(tomorrow) < 0;
    }

}
