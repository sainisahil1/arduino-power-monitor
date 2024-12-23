package io.sahil.server.infrastructure.config;

import io.sahil.server.util.MqttConstants;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This class provides a configuration for MQTT with MqttConnectOptions bean
 *
 * @author Sahil Saini
 */

@Configuration
public class MqttConfig implements MqttConstants {

    @Value("${"+MQTT_HOST+"}")
    private String host;

    @Value("${"+MQTT_USERNAME+"}")
    private String username;

    @Value("${"+MQTT_PASSWORD+"}")
    private String password;

    @Value("${"+MQTT_TOPIC+"}")
    private String topic;

    @Bean
    public MqttConnectOptions mqttConnectOptions() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(MQTT_CLEAN_SESSION);
        mqttConnectOptions.setServerURIs(new String[] {host});
        mqttConnectOptions.setUserName(username);
        mqttConnectOptions.setPassword(password.toCharArray());
        mqttConnectOptions.setAutomaticReconnect(MQTT_AUTO_RECONNECT);
        return mqttConnectOptions;
    }

    public String getHost() {
        return host;
    }

    public String getTopic() {
        return topic;
    }
}
