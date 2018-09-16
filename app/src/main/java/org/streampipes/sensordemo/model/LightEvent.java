package org.streampipes.sensordemo.model;

public class LightEvent extends AbstractEvent {

    private Float ambientLight;

    public LightEvent(long timestamp, Float ambientLight) {
        super(timestamp);
        this.ambientLight = ambientLight;
    }

    public Float getAmbientLight() {
        return ambientLight;
    }

    public void setAmbientLight(Float ambientLight) {
        this.ambientLight = ambientLight;
    }

    @Override
    public String getTopic() {
        return "light";
    }
}
