package io.sahil.server.infrastructure.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.sahil.server.core.model.MqttPayload;
import io.sahil.server.core.repository.InfluxDBRepository;
import io.sahil.server.util.MqttConstants;
import io.sahil.server.util.MqttDataProcessor;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.logging.Logger;

/**
 * This class is the implementation of MQTT Subscriber
 * It connects to MQTT service and subscribe to set topic.
 *
 * @author Sahil Saini
 */

@Component
public class MqttSubscriber implements MqttCallback, MqttConstants {

    private final MqttConnectOptions mqttConnectOptions;
    Environment environment;
    private MqttClient mqttClient;
    private MemoryPersistence memoryPersistence;
    private InfluxDBRepository influxDBRepository;
    private MqttDataProcessor mqttDataProcessor;

    private final Logger logger = Logger.getLogger(MqttSubscriber.class.getName());
    final private String clientId = UUID.randomUUID().toString();

    @Autowired
    public MqttSubscriber(
            MqttConnectOptions mqttConnectOptions,
            InfluxDBRepository influxDBRepository,
            MqttDataProcessor mqttDataProcessor,
            Environment environment
    ) {
        logger.info("Initializing MqttSubscriberImpl");
        this.mqttConnectOptions = mqttConnectOptions;
        this.influxDBRepository = influxDBRepository;
        this.mqttDataProcessor = mqttDataProcessor;
        this.environment = environment;
    }

    @PostConstruct
    private void init() {
        logger.info("Entry init method");
        memoryPersistence = new MemoryPersistence();
        String url = this.environment.getProperty(MQTT_HOST);
        try{
            connectMqtt(url);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void connectMqtt(String url) throws MqttException {
        mqttClient = new MqttClient(url, clientId, memoryPersistence);
        mqttClient.setCallback(this);
        mqttClient.connect(mqttConnectOptions);
        subscribe();
    }

    private void subscribe() throws MqttException {
        String topic = environment.getProperty(MQTT_TOPIC);
        mqttClient.subscribe(topic, MQTT_QOS);
    }

    @Override
    public void connectionLost(Throwable cause) {
        logger.info("Connection lost");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        MqttPayload payload = mqttDataProcessor.processMqttMessage(message.toString());
        influxDBRepository.save(payload);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}
