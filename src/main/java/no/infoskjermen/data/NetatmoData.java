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

    // New fields
    public final Double rain; // mm
    public final Integer windStrength; // km/h
    public final Integer windAngle;
    public final Integer gustStrength;
    public final Integer gustAngle;

    public NetatmoData(NetatmoMeasure indoorMeasure, NetatmoMeasure outsideMeasure, NetatmoMeasure rainMeasure,
            NetatmoMeasure windMeasure) {
        // Existing
        if (indoorMeasure != null && indoorMeasure.getBody() != null && !indoorMeasure.getBody().isEmpty()) {
            var data = indoorMeasure.getBody().get(0).getValue().get(0);
            indoorTemperature = data.get(0).intValue();
            co2 = data.get(1).intValue();
            indoorHumidity = data.get(2).intValue();
            pressure = data.get(3).intValue();
            noise = data.get(4).intValue();
        } else {
            indoorTemperature = null;
            co2 = null;
            indoorHumidity = null;
            pressure = null;
            noise = null;
        }

        if (outsideMeasure != null && outsideMeasure.getBody() != null && !outsideMeasure.getBody().isEmpty()) {
            var data = outsideMeasure.getBody().get(0).getValue().get(0);
            outdoorTemperature = data.get(0).intValue();
            outdoorHumidity = data.get(1).intValue();
        } else {
            outdoorTemperature = null;
            outdoorHumidity = null;
        }

        // New Rain
        if (rainMeasure != null && rainMeasure.getBody() != null && !rainMeasure.getBody().isEmpty()) {
            rain = rainMeasure.getBody().get(0).getValue().get(0).get(0); // Rain
        } else {
            rain = null;
        }

        // New Wind
        if (windMeasure != null && windMeasure.getBody() != null && !windMeasure.getBody().isEmpty()) {
            var data = windMeasure.getBody().get(0).getValue().get(0);
            windStrength = data.get(0).intValue();
            windAngle = data.get(1).intValue();
            gustStrength = data.get(2).intValue();
            gustAngle = data.get(3).intValue();
        } else {
            windStrength = null;
            windAngle = null;
            gustStrength = null;
            gustAngle = null;
        }
    }
}
