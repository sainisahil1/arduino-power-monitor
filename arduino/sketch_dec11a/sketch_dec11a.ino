
#include "secrets.h"
#include <ArduinoMqttClient.h>
#include <WiFi.h>
#include <NTPClient.h>
#include <WiFiUdp.h>
#include <Adafruit_INA219.h>
#include <Wire.h>

WiFiSSLClient espClient;
MqttClient mqttClient(espClient);
WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "pool.ntp.org", 0, 60000);
Adafruit_INA219 ina219_LED(0x40);
Adafruit_INA219 ina219_Motor(0x41);
Adafruit_INA219 ina219_Total(0x44);
float totalSum = 0;
float ledSum = 0;
float motorSum = 0;
float count = 0;
unsigned long lastPublishTime = 0;


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


void checkForWifi(){
  while (WiFi.status() != WL_CONNECTED) {
    WiFi.begin(ssid, password);
    delay(500);
    Serial.print(".");
  }
}

void setup() {
  Wire.begin();
  Serial.begin(9600);
  delay(10); 

  initializeSensors();
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
  lastPublishTime = millis();
}

void loop() {

  mqttClient.poll();
  timeClient.update();

  float power_LED = ina219_LED.getPower_mW();
  float power_Motor = ina219_Motor.getPower_mW();
  float power_Total = ina219_Total.getPower_mW();

  totalSum = totalSum + power_Total;
  ledSum = ledSum + power_LED;
  motorSum = motorSum + power_Motor;
  count++;
  

  if (millis() - lastPublishTime >= 5000) {
    checkForWifi();

  if(!mqttClient.connected()){
   connectToMQTT();
  }
    lastPublishTime = millis();
    long timestamp = timeClient.getEpochTime();
    

    float totalAvg = totalSum / count;
    float motorAvg = motorSum / count;
    float ledAvg = ledSum / count;
  

// Prepare JSON payload
  char payload[256];
  snprintf(payload, sizeof(payload),
           "{"
           "\"total_reading\":%.2f,"
           "\"led_reading\":%.2f,"
           "\"motor_reading\":%.2f,"
           "\"timestamp\":%lu"
           "}",
           totalAvg, ledAvg, motorAvg, timestamp);


  Serial.println(payload);

  totalSum = 0;
  ledSum = 0;
  motorSum = 0;
  count = 0;

    mqttClient.beginMessage(TOPIC);
    mqttClient.print(payload);
    mqttClient.endMessage();

  }

}
