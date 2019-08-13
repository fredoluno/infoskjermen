package no.infoskjermen.data.netatmo;



import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Body {

    private Integer begTime;
    private List<List<Double>> value = null;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Integer getBegTime() {
        return begTime;
    }

    public void setBegTime(Integer begTime) {
        this.begTime = begTime;
    }

    public List<List<Double>> getValue() {
        return value;
    }

    public void setValue(List<List<Double>> value) {
        this.value = value;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}