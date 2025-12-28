package no.infoskjermen.data;


import no.infoskjermen.data.netatmo.NetatmoMeasure;

public class NetatmoData {

    public final Integer indoorTemperature;
    public final Integer indoorHumidity;
    public final Integer outdoorTemperature;
    public final Integer outdoorHumidity;
    public final Integer co2;
    public final Integer noise;
    public final Integer pressure;



    public NetatmoData(NetatmoMeasure indoorMeasure, NetatmoMeasure outsideMeasure) {
        indoorTemperature = Integer.valueOf(indoorMeasure.getBody().get(0).getValue().get(0).get(0).intValue());
        indoorHumidity = Integer.valueOf(indoorMeasure.getBody().get(0).getValue().get(0).get(2).intValue());
        outdoorTemperature = Integer.valueOf(outsideMeasure.getBody().get(0).getValue().get(0).get(0).intValue());;
        outdoorHumidity = Integer.valueOf(outsideMeasure.getBody().get(0).getValue().get(0).get(1).intValue());;
        co2 = Integer.valueOf(indoorMeasure.getBody().get(0).getValue().get(0).get(1).intValue());;
        noise = Integer.valueOf(indoorMeasure.getBody().get(0).getValue().get(0).get(4).intValue());
        pressure = Integer.valueOf(indoorMeasure.getBody().get(0).getValue().get(0).get(3).intValue());;


    }
}
