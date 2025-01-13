package io.sahil.server.util;

/**
 * @author Sahil Saini
 */
public interface InfluxConstants {
    String INFLUX_TOKEN = "influxdb.token";
    String INFLUX_HOST = "influxdb.host";
    String INFLUX_BUCKET = "influxdb.bucket";
    String INFLUX_ORG = "influxdb.org";
    String INFLUX_MEASUREMENT = "sensor_data";
    String INFLUX_SENSOR_ID = "sensor_id";
    String INFLUX_SENSOR_TOTAL = "total_reading";
    String INFLUX_SENSOR_LED = "led_reading";
    String INFLUX_SENSOR_MOTOR = "motor_reading";
    String INFLUX_VALUE = "_value";
    String INFLUX_TOTAL_THRESHOLD = "800";
    String INFLUX_LED_THRESHOLD = "300";
    String INFLUX_MOTOR_THRESHOLD = "500";
    int INFLUX_BATCH_SIZE = 12;
    int INFLUX_FLUSH_INTERVAL = 70000;
    String INFLUX_RANGE_DAILY = "-1d";
    String INFLUX_RANGE_WEEKLY = "-7d";
    String INFLUX_RANGE_MONTHLY = "-30d";
    String INFLUX_DOWN_SAMPLE_DAILY = "5m";
    String INFLUX_DOWN_SAMPLE_WEEKLY = "30m";
    String INFLUX_DOWN_SAMPLE_MONTHLY = "2h";
}
