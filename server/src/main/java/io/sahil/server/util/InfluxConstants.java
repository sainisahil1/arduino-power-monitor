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
    String INFLUX_CURRENT = "current";
    String INFLUX_VOLTAGE = "voltage";
    int INFLUX_BATCH_SIZE = 12;
    int INFLUX_FLUSH_INTERVAL = 70000;
}
