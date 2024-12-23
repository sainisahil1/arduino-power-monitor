package io.sahil.server.core.repository;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApi;
import com.influxdb.client.WriteOptions;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import io.sahil.server.core.model.MqttPayload;
import io.sahil.server.infrastructure.config.InfluxConfig;
import io.sahil.server.util.HistoryType;
import io.sahil.server.util.InfluxConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Sahil Saini
 */

@Repository
public class InfluxDBRepository implements InfluxConstants {

    private InfluxDBClient influxDBClient;
    WriteApi writeApi;
    private InfluxConfig influxConfig;

    @Autowired
    public InfluxDBRepository(InfluxDBClient influxDBClient, InfluxConfig config) {
        this.influxDBClient = influxDBClient;
        this.influxConfig = config;
        makeInfluxWriteApi(influxDBClient);
    }

    private void makeInfluxWriteApi(InfluxDBClient influxDBClient) {
        WriteOptions writeOptions = WriteOptions.builder()
                .batchSize(INFLUX_BATCH_SIZE)
                .flushInterval(INFLUX_FLUSH_INTERVAL)
                .build();
        writeApi = influxDBClient.makeWriteApi(writeOptions);
    }

    public void save(List<MqttPayload> payload) {
        for (MqttPayload payloadObj : payload) {
            writeToInflux(payloadObj);
        }
    }

    private void writeToInflux(MqttPayload payloadObj) {
        String bucket = influxConfig.getBucket();
        String org = influxConfig.getOrg();
        Point point = Point.measurement(INFLUX_MEASUREMENT)
                .addTag(INFLUX_SENSOR_ID, payloadObj.getSensorId())
                .addField(INFLUX_CURRENT, payloadObj.getCurrent())
                .addField(INFLUX_VOLTAGE, payloadObj.getVoltage())
                .time(payloadObj.getTimestamp(), WritePrecision.MS);
        writeApi.writePoint(bucket, org, point);
    }

    public void fetchHistoricalData(HistoryType historyType) {
        //TODO: fetch down-sampled data from query
    }

    public void fetchOutliers(HistoryType historyType) {
        //TODO: fetch data points above threshold
    }


}
