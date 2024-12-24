package io.sahil.server.core.model;

/**
 *
 * @author Sahil Saini
 */
public class SensorDataDTO {

    private double power;
    private long timestamp;

    public double getPower() {
        return power;
    }

    public void setPower(double power) {
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
        return "SensorDataDTO{" +
                "power=" + power +
                ", timestamp=" + timestamp +
                '}';
    }
}
