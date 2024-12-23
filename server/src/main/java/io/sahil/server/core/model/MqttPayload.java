package io.sahil.server.core.model;

/**
 * Data class for data received from mqtt
 *
 * @author Sahil Saini
 */
public class MqttPayload {

    private CurrentData totalCurrent;
    private CurrentData motorCurrent;
    private CurrentData ledCurrent;

    public CurrentData getTotalCurrent() {
        return totalCurrent;
    }

    public void setTotalCurrent(CurrentData totalCurrent) {
        this.totalCurrent = totalCurrent;
    }

    public CurrentData getMotorCurrent() {
        return motorCurrent;
    }

    public void setMotorCurrent(CurrentData motorCurrent) {
        this.motorCurrent = motorCurrent;
    }

    public CurrentData getLedCurrent() {
        return ledCurrent;
    }

    public void setLedCurrent(CurrentData ledCurrent) {
        this.ledCurrent = ledCurrent;
    }

}
