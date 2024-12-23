package io.sahil.server.util;

/**
 * @author Sahil Saini
 */
public interface InfluxConstants {
    String INFLUX_TOKEN = "influxdb.token";
    String INFLUX_HOST = "influxdb.host";
    String INFLUX_BUCKET = "influxdb.bucket";
    String INFLUX_MEASUREMENT = "sensor_data";
    String INFLUX_SENSOR_ID = "sensor_id";
    String INFLUX_TOTAL_CURRENT = "total_current";
    String INFLUX_MOTOR_CURRENT = "motor_current";
    String INFLUX_LED_CURRENT = "led_current";
    String INFLUX_VALUE = "value";
}
