package org.streampipes.sensordemo.model;

public class BeaconEvent extends AbstractEvent {

    private Double distance;
    private String beaconId;

    @Override
    public String getTopic() {
        return "beacon";
    }

    public BeaconEvent(long timestamp, Double distance, String beaconId) {
        super(timestamp);
        this.distance = distance;
        this.beaconId = beaconId;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getBeaconId() {
        return beaconId;
    }

    public void setBeaconId(String beaconId) {
        this.beaconId = beaconId;
    }
}
