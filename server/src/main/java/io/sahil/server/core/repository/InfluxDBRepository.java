package io.sahil.server.core.repository;

import com.influxdb.v3.client.InfluxDBClient;
import io.sahil.server.core.model.MqttPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

/**
 * @author Sahil Saini
 */

@Repository
public class InfluxDBRepository {

    private InfluxDBClient influxDBClient;
    private Environment environment;

    @Autowired
    public InfluxDBRepository(InfluxDBClient influxDBClient, Environment environment) {
        this.influxDBClient = influxDBClient;
        this.environment = environment;
    }

    public void save(MqttPayload payload) {
        //TODO: save messages in batch
    }



}
