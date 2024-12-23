package io.sahil.server.infrastructure.config;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import io.sahil.server.util.InfluxConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This class provides InfluxDBClient bean
 *
 * @author Sahil Saini
 */

@Configuration
public class InfluxConfig implements InfluxConstants {

    @Value("${"+INFLUX_HOST+"}")
    private String host;

    @Value("${"+INFLUX_BUCKET+"}")
    private String bucket;

    @Value("${"+INFLUX_TOKEN+"}")
    private String token;

    @Value("${"+INFLUX_ORG+"}")
    private String org;

    @Bean
    public InfluxDBClient influxDBClient() {
        assert token != null;
        return InfluxDBClientFactory.create(host, token.toCharArray());
    }

    public String getBucket() {
        return bucket;
    }

    public String getOrg() {
        return org;
    }
}
