package io.sahil.server.core.repository;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApi;
import com.influxdb.client.WriteOptions;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import io.sahil.server.core.model.InfluxDTO;
import io.sahil.server.core.model.MqttPayload;
import io.sahil.server.core.model.SensorDataDTO;
import io.sahil.server.infrastructure.config.InfluxConfig;
import io.sahil.server.util.HistoryType;
import io.sahil.server.util.InfluxConstants;
import io.sahil.server.util.QueryType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Repository for InfluxDB APIs
 * @author Sahil Saini
 */

@Repository
public class InfluxDBRepository implements InfluxConstants {

    private final InfluxDBClient influxDBClient;
    WriteApi writeApi;
    private final InfluxConfig influxConfig;
    private final Logger logger = Logger.getLogger(InfluxDBRepository.class.getName());

    @Autowired
    public InfluxDBRepository(InfluxConfig config) {
        this.influxConfig = config;
        influxDBClient = config.getInfluxDBClient();
        makeInfluxWriteApi(influxDBClient);
    }

    /**
     * Initialize WriteApi object
     * @param influxDBClient client
     */
    private void makeInfluxWriteApi(InfluxDBClient influxDBClient) {
        WriteOptions writeOptions = WriteOptions.builder()
                .batchSize(INFLUX_BATCH_SIZE)
                .flushInterval(INFLUX_FLUSH_INTERVAL)
                .build();
        writeApi = influxDBClient.makeWriteApi(writeOptions);
    }

    /**
     * Write MQTT payload to InfluxDB
     * @param payload MQTT Payload
     */
    public void save(MqttPayload payload) {
        String bucket = influxConfig.getBucket();
        String org = influxConfig.getOrg();
        Point ledPoint = generatePoint(payload.getLedReading(), INFLUX_SENSOR_LED, payload.getTimestamp());
        Point motorPoint = generatePoint(payload.getMotorReading(), INFLUX_SENSOR_MOTOR, payload.getTimestamp());
        Point totalPoint = generatePoint(payload.getTotalReading(), INFLUX_SENSOR_TOTAL, payload.getTimestamp());
        writeApi.writePoint(bucket, org, ledPoint);
        writeApi.writePoint(bucket, org, motorPoint);
        writeApi.writePoint(bucket, org, totalPoint);
    }

    private Point generatePoint(float reading, String sensorId, long timestamp) {
        return Point.measurement(INFLUX_MEASUREMENT)
                .addTag(INFLUX_SENSOR_ID, sensorId)
                .addField(INFLUX_VALUE, reading)
                .time(timestamp, WritePrecision.S);
    }

    /**
     * Fetch data from InfluxDB using APIs
     * @param historyType daily, weekly or monthly
     * @param queryType historical or above threshold data
     * @return InfluxDTO object for API
     */
    public InfluxDTO fetchData(HistoryType historyType, QueryType queryType) {
        String query = (queryType == QueryType.HISTORICAL)
                ? influxConfig.getFetchDataQuery(historyType)
                : influxConfig.getOutlierDataQuery(historyType);
        QueryApi queryApi = influxDBClient.getQueryApi();
        return executeHistoricalDataFetchQuery(queryApi, query);
    }

    private InfluxDTO executeHistoricalDataFetchQuery(QueryApi queryApi, String query) {
        ArrayList<SensorDataDTO> totalReading = new ArrayList<>();
        ArrayList<SensorDataDTO> ledReading = new ArrayList<>();
        ArrayList<SensorDataDTO> motorReading = new ArrayList<>();
        logger.info(query);
        List<FluxTable> tables = queryApi.query(query, influxConfig.getOrg());
        logger.info(String.valueOf(tables.size()));
        for (FluxTable table : tables) {
            logger.info(table.toString());
            for (FluxRecord record : table.getRecords()) {
                SensorDataDTO sensorDataDTO = extractSensorData(record);
                logger.info(sensorDataDTO.toString());
                Object sensorId = record.getValueByKey(INFLUX_SENSOR_ID);
                assert sensorId != null;
                switch ((String) sensorId) {
                    case INFLUX_SENSOR_TOTAL -> totalReading.add(sensorDataDTO);
                    case INFLUX_SENSOR_LED -> ledReading.add(sensorDataDTO);
                    case INFLUX_SENSOR_MOTOR -> motorReading.add(sensorDataDTO);
                }
            }
        }
        InfluxDTO influxDTO = new InfluxDTO();
        influxDTO.setTotalReading(totalReading);
        influxDTO.setLedReading(ledReading);
        influxDTO.setMotorReading(motorReading);
        logger.info(influxDTO.toString());
        return influxDTO;
    }

    private SensorDataDTO extractSensorData(FluxRecord record) {
        Object power = record.getValueByKey(INFLUX_VALUE);
        Instant time = record.getTime();
        assert power != null;
        assert time != null;
        return getSensorDataDTO((Double) power, time);
    }

    private SensorDataDTO getSensorDataDTO(Double power, Instant time) {
        SensorDataDTO sensorDataDTO = new SensorDataDTO();
        sensorDataDTO.setPower(power);
        sensorDataDTO.setTimestamp(time.toEpochMilli());
        logger.info(sensorDataDTO.toString());
        return sensorDataDTO;
    }


}
