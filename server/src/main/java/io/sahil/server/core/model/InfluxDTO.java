package io.sahil.server.core.model;

import java.util.ArrayList;

/**
 * @author Sahil Saini
 */
public class InfluxDTO {

    private ArrayList<SensorDataDTO> totalReading;
    private ArrayList<SensorDataDTO> ledReading;
    private ArrayList<SensorDataDTO> motorReading;

    public ArrayList<SensorDataDTO> getTotalReading() {
        return totalReading;
    }

    public void setTotalReading(ArrayList<SensorDataDTO> totalReading) {
        this.totalReading = totalReading;
    }

    public ArrayList<SensorDataDTO> getLedReading() {
        return ledReading;
    }

    public void setLedReading(ArrayList<SensorDataDTO> ledReading) {
        this.ledReading = ledReading;
    }

    public ArrayList<SensorDataDTO> getMotorReading() {
        return motorReading;
    }

    public void setMotorReading(ArrayList<SensorDataDTO> motorReading) {
        this.motorReading = motorReading;
    }

    @Override
    public String toString() {
        return "InfluxDTO{" +
                "totalReading=" + totalReading +
                ", ledReading=" + ledReading +
                ", motorReading=" + motorReading +
                '}';
    }
}
