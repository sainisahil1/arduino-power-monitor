
#include "secrets.h"
#include <ArduinoMqttClient.h>
#include <WiFi.h>

WiFiSSLClient espClient;
MqttClient mqttClient(espClient);

void connectToMQTT() {
  while (!mqttClient.connected()) {
    Serial.print("Connecting to MQTT...");
    if (mqttClient.connect(PUBSUB_ENDPOINT, PORT)){
      Serial.println("Connected to MQTT broker");
    } else {
      Serial.print("Failed, rc=");
      Serial.print(mqttClient.connectError());
      Serial.println(" Trying again in 5 seconds...");
      delay(5000);
    }
  }
}

void setup() {
  
  Serial.begin(115200);
  delay(1500); 
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("Connected to Wi-Fi!");
  mqttClient.setUsernamePassword(USERNAME, PASS);
  mqttClient.setKeepAliveInterval(300000);
  connectToMQTT();
}

void loop() {

  mqttClient.poll();

  float total_current = random(1000) / 100.0;
  float led_current = random(1000) / 100.0;
  float motor_current = random(1000) / 100.0;

  Serial.print("Sending sensor value: ");

  char payload[100];
  snprintf(payload, sizeof(payload), "{\"total_current\": %.2f, \"led_current\": %.2f, \"motor_current\": %.2f}", total_current, led_current, motor_current);

  Serial.println(payload);
  bool retained = false;
    int qos = 1;
    bool dup = false;

    mqttClient.beginMessage(TOPIC, sizeof(payload), retained, qos, dup);
    mqttClient.print(payload);
    mqttClient.endMessage();

  delay(5000);

}
