package io.sahil.server.infrastructure.mqtt;

import io.sahil.server.core.model.MqttPayload;
import io.sahil.server.core.repository.InfluxDBRepository;
import io.sahil.server.infrastructure.config.MqttConfig;
import io.sahil.server.util.MqttConstants;
import io.sahil.server.util.MqttDataProcessor;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
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
    private MqttClient mqttClient;
    private MemoryPersistence memoryPersistence;
    private final InfluxDBRepository influxDBRepository;
    private final MqttDataProcessor mqttDataProcessor;
    private final MqttConfig mqttConfig;

    private final Logger logger = Logger.getLogger(MqttSubscriber.class.getName());
    final private String clientId = UUID.randomUUID().toString();

    @Autowired
    public MqttSubscriber(
            MqttConnectOptions mqttConnectOptions,
            InfluxDBRepository influxDBRepository,
            MqttDataProcessor mqttDataProcessor,
            MqttConfig mqttConfig
    ) {
        logger.info("Initializing MqttSubscriberImpl");
        this.mqttConnectOptions = mqttConnectOptions;
        this.influxDBRepository = influxDBRepository;
        this.mqttDataProcessor = mqttDataProcessor;
        this.mqttConfig = mqttConfig;
    }

    @PostConstruct
    private void init() {
        logger.info("Entry init method");
        memoryPersistence = new MemoryPersistence();
        String url = mqttConfig.getHost();
        logger.info("Connecting to " + url);
        try {
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
        String topic = mqttConfig.getTopic();
        mqttClient.subscribe(topic, MQTT_QOS);
    }

    @Override
    public void connectionLost(Throwable cause) {
        logger.info("Connection lost");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        logger.info("Message arrived: " + message);
        MqttPayload payload = mqttDataProcessor.processMqttMessage(message.toString());
        influxDBRepository.save(payload);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}
