package org.streampipes.sensordemo.model;

public class PressureEvent extends AbstractEvent {

    private Float pressure;

    public PressureEvent(long timestamp, Float pressure) {
        super(timestamp);
        this.pressure = pressure;
    }

    public Float getPressure() {
        return pressure;
    }

    public void setPressure(Float pressure) {
        this.pressure = pressure;
    }

    @Override
    public String getTopic() {
        return "pressure";
    }
}
