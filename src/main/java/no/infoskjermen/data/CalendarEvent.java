package no.infoskjermen.data;

import no.infoskjermen.utils.DateTimeUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


public class CalendarEvent implements Comparable {

    public LocalDateTime eventStart;
    public LocalDateTime eventEnd;
    public String title;

    @Override
    public int compareTo(Object o) {
        if (eventStart == null){
            return 1;
        }
        int compare = eventStart.compareTo(((CalendarEvent)o).eventStart) ;
        if(compare == 0)
            compare =  eventStart.compareTo(((CalendarEvent)o).eventEnd);
        if(compare == 0)
            return -1;
        else
            return compare;

    }

    public String printDato(){

        if(DateTimeUtils.erIdag(eventStart)&&DateTimeUtils.erIdag(eventEnd)){
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm").withLocale(new Locale("no","NO"));

            return fmt.format(eventStart) + "-" + fmt.format(eventEnd);
        }
        else if (DateTimeUtils.erInnen7dager(eventEnd)) {
            String dato;
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("EEEE").withLocale(new Locale("no","NO"));

            if (DateTimeUtils.datoPassert(eventStart)) {
                dato = "nå-" + fmt.format(eventEnd);
            } else if (DateTimeUtils.erSammeDag(eventStart, eventEnd)) {
                dato = fmt.format(eventStart);
            } else {
                dato = fmt.format(eventStart) + "-" + fmt.format(eventEnd);
            }
            return dato;

        } else if (DateTimeUtils.erSammeDag(eventStart, eventEnd)) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM");
            return fmt.format(eventStart);
        }

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM");


        return  fmt.format(eventStart) + "-" +fmt.format(eventEnd);
    }

    public String debug()
    {
        return "printDato="+ printDato() + " title=" +title + " eventStart=" + eventStart+ " eventEnd=" + eventEnd;
    }

    public String print(){
        return printDato() + " - " + this.title;
    }





}
