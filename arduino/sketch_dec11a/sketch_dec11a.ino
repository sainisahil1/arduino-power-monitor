
#include "secrets.h"
#include <ArduinoMqttClient.h>
#include <WiFi.h>
#include <NTPClient.h>
#include <WiFiUdp.h>

WiFiSSLClient espClient;
MqttClient mqttClient(espClient);
WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "pool.ntp.org", 3600, 60000);

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
  timeClient.begin();
  mqttClient.setUsernamePassword(USERNAME, PASS);
  mqttClient.setKeepAliveInterval(60000);
  connectToMQTT();
}

void loop() {

  mqttClient.poll();
  timeClient.update();

  if(!mqttClient.connected()){
    connectToMQTT();
  }

  static unsigned long lastPublishTime = 0;
  if (millis() - lastPublishTime >= 5000) {
    lastPublishTime = millis();

  float total_current = random(1000) / 100.0;
  float led_current = random(1000) / 100.0;
  float motor_current = random(1000) / 100.0;
  long timestamp = timeClient.getEpochTime();

// Prepare JSON payload
  char payload[256];
  snprintf(payload, sizeof(payload),
           "{"
           "\"total_current\":{\"value\":%.2f,\"timestamp\":%lu},"
           "\"led_current\":{\"value\":%.2f,\"timestamp\":%lu},"
           "\"motor_current\":{\"value\":%.2f,\"timestamp\":%lu}"
           "}",
           total_current, timestamp,
           led_current, timestamp,
           motor_current, timestamp);

  Serial.println(payload);

    mqttClient.beginMessage(TOPIC);
    mqttClient.print(payload);
    mqttClient.endMessage();

  }

}
