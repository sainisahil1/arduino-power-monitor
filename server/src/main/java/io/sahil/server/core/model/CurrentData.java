package io.sahil.server.core.model;

/**
 * Data class for individual sensor
 *
 * @author Sahil Saini
 */
public class CurrentData {

    private float value;
    private long timestamp;

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
