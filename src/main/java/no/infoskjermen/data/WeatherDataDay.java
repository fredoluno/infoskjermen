package no.infoskjermen.data;


import java.time.LocalDate;

public class WeatherDataDay implements Comparable{

    public LocalDate date;
    public WeatherDataPeriod night;
    public WeatherDataPeriod morning;
    public WeatherDataPeriod day;
    public WeatherDataPeriod evening;

    public void addMagic(WeatherDataPeriod data){

        this.date = data.date;
        switch (data.period) {
            case "0":
                this.night = data;
                break;
            case "1":
                this.morning = data;
                break;
            case "2":
                this.day = data;
                break;
            case "3":
                this.evening = data;
                break;
        }
    }

    public String debug(){

        String text = "NewDay date: " + date + "\n";
        text = night!=null?     text + " night   - "   + night.debug() + "\n":text;
        text = morning!=null?   text + " morning - " +  morning.debug() + "\n":text;
        text = day!=null?       text + " day     - "     + day.debug() + "\n":text;
        text = evening!=null?   text + " evening - " + evening.debug() + "\n":text;

        return text;
    }

    @Override
    public int compareTo(Object o) {
        if (date == null){
            return 1;
        }
        int compare = date.compareTo(((WeatherDataDay)o).date) ;
        if(compare == 0)
            compare =  date.compareTo(((WeatherDataDay)o).date);
        if(compare == 0)
            return -1;
        else
            return compare;

    }


}
