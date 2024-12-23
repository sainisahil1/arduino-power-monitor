package io.sahil.server.infrastructure.config;

import com.influxdb.v3.client.InfluxDBClient;
import com.influxdb.v3.client.config.ClientConfig;
import io.sahil.server.util.InfluxConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * This class provides InfluxDBClient bean
 *
 * @author Sahil Saini
 */

@Configuration
public class InfluxConfig implements InfluxConstants {

    private final Environment environment;

    @Autowired
    public InfluxConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public InfluxDBClient InfluxDBClient() {
        String token = environment.getProperty(INFLUX_TOKEN);
        String host = environment.getProperty(INFLUX_HOST);
        assert host != null;
        assert token != null;
        ClientConfig config = new ClientConfig.Builder()
                .host(host)
                .token(token.toCharArray())
                .build();
        return InfluxDBClient.getInstance(config);
    }

}
