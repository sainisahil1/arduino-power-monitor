package io.sahil.server.mqtt;

import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.logging.Logger;

/**
 * @author Sahil Saini
 */

@Component
public class MqttSubscriberImpl implements MqttCallback, MqttConstants {

    private MqttConnectOptions mqttConnectOptions;
    Environment environment;
    private MqttClient mqttClient;
    private MemoryPersistence memoryPersistence;

    private final Logger logger = Logger.getLogger(MqttSubscriberImpl.class.getName());
    final private String clientId = UUID.randomUUID().toString();

    @Autowired
    public MqttSubscriberImpl(MqttConnectOptions mqttConnectOptions, Environment environment) {
        logger.info("Initializing MqttSubscriberImpl");
        this.mqttConnectOptions = mqttConnectOptions;
        this.environment = environment;
    }

    @PostConstruct
    private void config() {
        logger.info("Entry config method");
        memoryPersistence = new MemoryPersistence();
        String url = this.environment.getProperty(brokerHost);
        logger.info("Broker URL: " + url);
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
        String topic = environment.getProperty(brokerTopic);
        int qos = 0;
        mqttClient.subscribe(topic, qos);
    }

    @Override
    public void connectionLost(Throwable cause) {
        logger.info("Connection lost");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        logger.info("Message arrived");
        logger.info(new String(message.getPayload()));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}
