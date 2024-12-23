package io.sahil.server.util;

/**
 * @author Sahil Saini
 */
public interface MqttConstants {
    String MQTT_HOST = "mqtt.broker.url";
    String MQTT_USERNAME = "mqtt.username";
    String MQTT_PASSWORD = "mqtt.password";
    String MQTT_TOPIC = "mqtt.topic";
    boolean MQTT_CLEAN_SESSION = true;
    int MQTT_QOS = 0;
    boolean MQTT_AUTO_RECONNECT = true;
}
