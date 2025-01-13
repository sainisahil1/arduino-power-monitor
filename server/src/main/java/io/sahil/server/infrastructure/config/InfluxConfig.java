package io.sahil.server.infrastructure.config;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import io.sahil.server.util.HistoryType;
import io.sahil.server.util.InfluxConstants;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.logging.Logger;

/**
 * This class provides InfluxDBClient bean
 *
 * @author Sahil Saini
 */

@Configuration
public class InfluxConfig implements InfluxConstants {

    @Value("${" + INFLUX_HOST + "}")
    private String host;

    @Value("${" + INFLUX_BUCKET + "}")
    private String bucket;

    @Value("${" + INFLUX_TOKEN + "}")
    private String token;

    @Value("${" + INFLUX_ORG + "}")
    private String org;

    private InfluxDBClient influxDBClient;

    @PostConstruct
    private void init() {
        assert host != null;
        assert token != null;
        influxDBClient = InfluxDBClientFactory.create(host, token.toCharArray(), org);
    }

    public InfluxDBClient getInfluxDBClient() {
        return influxDBClient;
    }

    public String getFetchDataQuery(HistoryType historyType) {
        return String.format(
                "from(bucket: \"%s\") " +
                "|> range(start: %s) " +
                "|> filter(fn: (r) => r._measurement == \"%s\") " +
                "|> aggregateWindow(every: %s, fn: mean, createEmpty: false) " +
                "|> yield(name: \"all_data\")",
                bucket,
                getQueryRangeFilter(historyType),
                INFLUX_MEASUREMENT,
                getQueryDownSampleFilter(historyType)
        );
    }

    public String getOutlierDataQuery(HistoryType historyType) {
        return String.format(
                "from(bucket: \"%s\") " +
                        "|> range(start: %s) " +
                        "|> filter(fn: (r) => r._measurement == \"%s\" and " +
                        "  ( (r.sensor_id == \"motor_reading\" and r._value > %s) or " +
                        "    (r.sensor_id == \"total_reading\" and r._value > %s) or " +
                        "    (r.sensor_id == \"led_reading\" and r._value > %s) ) ) " +
                        "|> aggregateWindow(every: %s, fn: mean, createEmpty: false) " +
                        "|> yield(name: \"all_data\")",
                bucket,
                getQueryRangeFilter(historyType),
                INFLUX_MEASUREMENT,
                INFLUX_MOTOR_THRESHOLD,
                INFLUX_TOTAL_THRESHOLD,
                INFLUX_LED_THRESHOLD,
                getQueryDownSampleFilter(historyType)
        );
    }

    private String getQueryRangeFilter(HistoryType historyType) {
        return switch (historyType) {
            case DAILY -> INFLUX_RANGE_DAILY;
            case WEEKLY -> INFLUX_RANGE_WEEKLY;
            case MONTHLY -> INFLUX_RANGE_MONTHLY;
        };
    }

    private String getQueryDownSampleFilter(HistoryType historyType) {
        return switch (historyType) {
            case DAILY -> INFLUX_DOWN_SAMPLE_DAILY;
            case WEEKLY -> INFLUX_DOWN_SAMPLE_WEEKLY;
            case MONTHLY -> INFLUX_DOWN_SAMPLE_MONTHLY;
        };
    }

    public String getBucket() {
        return bucket;
    }

    public String getOrg() {
        return org;
    }
}
