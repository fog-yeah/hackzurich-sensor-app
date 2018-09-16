package org.streampipes.sensordemo.model;

public abstract class AbstractEvent {

    private long timestamp;

    public AbstractEvent() {
    }

    public AbstractEvent(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public abstract String getTopic();
}
