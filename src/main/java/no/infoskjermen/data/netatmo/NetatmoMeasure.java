package no.infoskjermen.data.netatmo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetatmoMeasure {

    private List<Body> body = null;
    private String status;
    private Double timeExec;
    private Integer timeServer;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public List<Body> getBody() {
        return body;
    }

    public void setBody(List<Body> body) {
        this.body = body;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getTimeExec() {
        return timeExec;
    }

    public void setTimeExec(Double timeExec) {
        this.timeExec = timeExec;
    }

    public Integer getTimeServer() {
        return timeServer;
    }

    public void setTimeServer(Integer timeServer) {
        this.timeServer = timeServer;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}