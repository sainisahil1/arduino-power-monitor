package io.sahil.server.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.sahil.server.core.model.CurrentData;
import io.sahil.server.core.model.InfluxDTO;
import io.sahil.server.core.model.MqttPayload;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Deserialize JSON data received from MQTT listener
 *
 * @author Sahil Saini
 */
@Component
public class MqttDataProcessor {

    private final ObjectMapper objectMapper;

    @Autowired
    public MqttDataProcessor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public MqttPayload processMqttMessage(String json) {
        try{
            return objectMapper.readValue(json, MqttPayload.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public InfluxDTO processInfluxDTO(CurrentData currentData, String tagValue) {
        InfluxDTO influxDTO = new InfluxDTO();
        influxDTO.setTagValue(tagValue);
        influxDTO.setTimestamp(currentData.getTimestamp());
        influxDTO.setFieldValue(currentData.getValue());
        return influxDTO;
    }

}
