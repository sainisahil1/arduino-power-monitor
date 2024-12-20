package io.sahil.server.mqtt;

/**
 * @author Sahil Saini
 */
public interface MqttConstants {
    String brokerHost = "mqtt.broker.url";
    String brokerUsername = "mqtt.username";
    String brokerPassword = "mqtt.password";
    String brokerTopic = "mqtt.topic";
    boolean cleanSession = true;
}
