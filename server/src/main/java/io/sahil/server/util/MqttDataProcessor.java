package io.sahil.server.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.sahil.server.core.model.MqttPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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

    public MqttPayload processMqttMessage(String payload) {
        try{
            return objectMapper.readValue(payload, MqttPayload.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
