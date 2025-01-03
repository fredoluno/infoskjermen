package no.infoskjermen.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class DateTimeUtils {

    private static Logger log = LoggerFactory.getLogger(DateTimeUtils.class);

    public static String formatRFC3339(LocalDateTime date){
        DateTimeFormatter formatter = getRFC3339formatter();
        return formatter.format(ZonedDateTime.of(date,ZoneId.systemDefault()));
    }

    private static DateTimeFormatter getRFC3339formatter() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("UTC"));
    }

    public  static DateTimeFormatter getEnturFormatter(){
        return new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                .appendOffset("+HHmm","Z")
                .toFormatter();

    }

   public static String formatCalendar(Calendar calendar) {
        // Henter datoen som et Date-objekt fra Calendar
        Date date = calendar.getTime();

        // Oppretter en formatter for dato og tid
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // Formaterer datoen til en streng
        return dateFormat.format(date);
    }


    public static LocalDateTime getYrDateTime(String value){
        return LocalDateTime.parse(value, DateTimeUtils.getEnturFormatter());

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

    public static LocalDate getDateFromYr(String date){
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

        //convert String to LocalDate
        return LocalDate.parse(date, formatter);
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

    public static String getPublicTransportView(LocalDateTime date){
        LocalDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Oslo")).toLocalDateTime();

        Duration duration = Duration.between(now, date);
        log.debug("duration: " +  duration.toMinutes());
        if(duration.toMinutes()<120){
            return ""+ duration.toMinutes();
        }else if (duration.toHours() < 24){
            return getTime(date);
        }else{
            return duration.toHours() + " timer";
        }



    }

    public static String getTime(LocalDateTime date){
        return DateTimeFormatter.ofPattern("HH:mm").format(date);
    }
    public static String getDay(LocalDate date){ return DateTimeFormatter.ofPattern("EEEE").withLocale(new Locale("no","NO")).format(date);}

}
