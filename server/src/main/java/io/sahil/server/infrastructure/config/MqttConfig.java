package io.sahil.server.infrastructure.config;

import io.sahil.server.util.MqttConstants;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * This class provides a configuration for MQTT with MqttConnectOptions bean
 *
 * @author Sahil Saini
 */

@Configuration
class MqttConfig implements MqttConstants {

    private final Environment env;

    @Autowired
    public MqttConfig(Environment env) {
        this.env = env;
    }

    @Bean
    public MqttConnectOptions mqttConnectOptions() {
        String broker = env.getProperty(MQTT_HOST);
        String username = env.getProperty(MQTT_USERNAME);
        String password = env.getProperty(MQTT_PASSWORD);
        assert password != null;
        return getMqttConnectOptionsObject(broker, username, password);
    }

    private static MqttConnectOptions getMqttConnectOptionsObject(String broker, String username, String password) {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(MQTT_CLEAN_SESSION);
        mqttConnectOptions.setServerURIs(new String[] {broker});
        mqttConnectOptions.setUserName(username);
        mqttConnectOptions.setPassword(password.toCharArray());
        mqttConnectOptions.setAutomaticReconnect(MQTT_AUTO_RECONNECT);
        return mqttConnectOptions;
    }

}
