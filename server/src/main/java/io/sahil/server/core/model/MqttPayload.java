package io.sahil.server.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sahil.server.util.InfluxConstants;

/**
 * Data class for individual sensor
 *
 * @author Sahil Saini
 */
public class MqttPayload implements InfluxConstants {

    @JsonProperty(INFLUX_SENSOR_LED)
    private float ledReading;

    @JsonProperty(INFLUX_SENSOR_TOTAL)
    private float totalReading;

    @JsonProperty(INFLUX_SENSOR_MOTOR)
    private float motorReading;
    private long timestamp;

    public float getTotalReading() {
        return totalReading;
    }

    public void setTotalReading(float totalReading) {
        this.totalReading = totalReading;
    }

    public float getMotorReading() {
        return motorReading;
    }

    public void setMotorReading(float motorReading) {
        this.motorReading = motorReading;
    }

    public float getLedReading() {
        return ledReading;
    }

    public void setLedReading(float ledReading) {
        this.ledReading = ledReading;
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
                "ledReading=" + ledReading +
                ", totalReading=" + totalReading +
                ", motorReading=" + motorReading +
                ", timestamp=" + timestamp +
                '}';
    }
}
