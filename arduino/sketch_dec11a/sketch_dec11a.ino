
#include "secrets.h"
#include <ArduinoMqttClient.h>
#include <WiFi.h>
#include <NTPClient.h>
#include <WiFiUdp.h>
#include <Adafruit_INA219.h>

WiFiSSLClient espClient;
MqttClient mqttClient(espClient);
WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "pool.ntp.org", 3600, 60000);
Adafruit_INA219 ina219_LED(0x40);
Adafruit_INA219 ina219_Motor(0x41);
Adafruit_INA219 ina219_Total(0x42);

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

void initializeSensors(){
  Serial.println("Initializing sensors");
  if (!ina219_LED.begin()) {
    Serial.println("Failed to find INA219 for LED");
    while (1);
  }
  if (!ina219_Motor.begin()) {
    Serial.println("Failed to find INA219 for Motor");
    while (1);
  }
  if (!ina219_Total.begin()) {
    Serial.println("Failed to find INA219 for Total");
    while (1);
  }
  Serial.println("INA219 sensors initialized!");
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
  initializeSensors();
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

  float power_LED = ina219_LED.getPower_mW() / 1000;
  float power_Motor = ina219_Motor.getPower_mW() / 1000;
  float power_Total = ina219_Total.getPower_mW() / 1000;
  long timestamp = timeClient.getEpochTime();

// Prepare JSON payload
  char payload[256];
  snprintf(payload, sizeof(payload),
           "{"
           "\"total_reading\":%.2f,"
           "\"led_reading\":%.2f,"
           "\"motor_reading\":%.2f,"
           "\"timestamp\":%lu"
           "}",
           power_Total, power_LED, power_Motor, timestamp);

  Serial.println(payload);

    mqttClient.beginMessage(TOPIC);
    mqttClient.print(payload);
    mqttClient.endMessage();

  }

}
