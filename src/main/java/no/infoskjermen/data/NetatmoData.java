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
        indoorTemperature = new Integer(indoorMeasure.getBody().get(0).getValue().get(0).get(0).intValue());
        indoorHumidity = new Integer(indoorMeasure.getBody().get(0).getValue().get(0).get(2).intValue());
        outdoorTemperature = new Integer(outsideMeasure.getBody().get(0).getValue().get(0).get(0).intValue());;
        outdoorHumidity = new Integer(outsideMeasure.getBody().get(0).getValue().get(0).get(1).intValue());;
        co2 = new Integer(indoorMeasure.getBody().get(0).getValue().get(0).get(1).intValue());;
        noise = new Integer(indoorMeasure.getBody().get(0).getValue().get(0).get(4).intValue());
        pressure = new Integer(indoorMeasure.getBody().get(0).getValue().get(0).get(3).intValue());;


    }
}
