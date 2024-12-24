package io.sahil.server.core.model;

/**
 * Data class for individual sensor
 *
 * @author Sahil Saini
 */
public class MqttPayload {

    private String sensorId;
    private float power;
    private long timestamp;

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public float getPower() {
        return power;
    }

    public void setPower(float power) {
        this.power = power;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "MqttPayload{" +
                "sensorId='" + sensorId + '\'' +
                ", power=" + power +
                ", timestamp=" + timestamp +
                '}';
    }
}
