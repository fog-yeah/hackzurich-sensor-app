package org.streampipes.sensordemo.model;

public class PictureEvent extends AbstractEvent {

    private String image;

    private static final String topic = "image";

    public PictureEvent(long timestamp, String image) {
        super(timestamp);
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String getTopic() {
        return topic;
    }
}
