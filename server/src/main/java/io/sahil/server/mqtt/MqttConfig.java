package io.sahil.server.mqtt;

import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @author Sahil Saini
 */

@Configuration
class MqttConfig implements MqttConstants{

    private Environment env;

    @Autowired
    public MqttConfig(Environment env) {
        this.env = env;
    }

    @Bean
    public MqttConnectOptions mqttConnectOptions() {
        String broker = env.getProperty(brokerHost);
        String username = env.getProperty(brokerUsername);
        String password = env.getProperty(brokerPassword);
        MqttConnectOptions mqttConnectOptions = getMqttConnectOptionsObject(broker, username, password);
        return mqttConnectOptions;
    }

    private static MqttConnectOptions getMqttConnectOptionsObject(String broker, String username, String password) {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(cleanSession);
        mqttConnectOptions.setServerURIs(new String[] {broker});
        mqttConnectOptions.setUserName(username);
        mqttConnectOptions.setPassword(password.toCharArray());
        return mqttConnectOptions;
    }

}
