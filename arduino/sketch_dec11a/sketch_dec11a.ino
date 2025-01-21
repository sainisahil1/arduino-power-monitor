#include "secrets.h"
#include <ArduinoMqttClient.h>
#include <WiFi.h>
#include <NTPClient.h>
#include <WiFiUdp.h>
#include <Adafruit_INA219.h>

WiFiSSLClient espClient;
MqttClient mqttClient(espClient);
WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "pool.ntp.org", 7200, 60000);

unsigned long lastPublishTime = 0;
unsigned int samplesTaken = 0;

float sum_power_mW_LED = 0;
float sum_power_mW_MOTOR = 0;
float sum_power_mW_TOTAL = 0;

Adafruit_INA219 ina219_LED(0x41);
Adafruit_INA219 ina219_MOTOR(0x44);
Adafruit_INA219 ina219_TOTAL(0x40);

const int wifi_INDICATOR_LED = A6;
const int mqtt_INDICATOR_LED = A3;

const int led_RGB_RED_PIN = 5;
const int led_RGB_GREEN_PIN = 4;
const int motor_RGB_RED_PIN = 3;
const int motor_RGB_GREEN_PIN = 2;
const int total_RGB_RED_PIN = 0;
const int total_RGB_GREEN_PIN = 1;

const float led_MAXIMUM_THRESHOLD_MW = 700L;
const float motor_MAXIMUM_THRESHOLD_MW = 2000L;
const float total_MAXIMUM_THRESHOLD_MW = 3000L;

void connectToMQTT() {
  mqttClient.setUsernamePassword(USERNAME, PASS);
  mqttClient.setKeepAliveInterval(60000);

  while (!mqttClient.connected()) {
    digitalWrite(mqtt_INDICATOR_LED, HIGH);
    Serial.println("Connecting to MQTT...");
    if (mqttClient.connect(PUBSUB_ENDPOINT, PORT)) {
      Serial.println("Connected to MQTT broker");
      digitalWrite(mqtt_INDICATOR_LED, LOW);
    } else {
      Serial.print("Failed, rc=");
      Serial.print(mqttClient.connectError());
      Serial.println(" Trying again in 1 second...");
      for (int i = 0; i < 3; i++) {
        delay(250);
        digitalWrite(mqtt_INDICATOR_LED, LOW);
        delay(250);
        digitalWrite(mqtt_INDICATOR_LED, HIGH);
      }
    }
  }
  digitalWrite(mqtt_INDICATOR_LED, LOW);
}

void initializeSensors() {
  Serial.println("Initializing sensors");
  if (!ina219_LED.begin()) {
    Serial.println("Failed to find LED INA219");
    analogWrite(led_RGB_RED_PIN, 255);
    analogWrite(led_RGB_GREEN_PIN, 0);
    while (1)
      ;
  }

  analogWrite(led_RGB_RED_PIN, 0);
  analogWrite(led_RGB_GREEN_PIN, 255);

  Serial.println("INA219 LED sensor initialized! :)");
  if (!ina219_MOTOR.begin()) {
    Serial.println("Failed to find MOTOR INA219");
    analogWrite(motor_RGB_RED_PIN, 255);
    analogWrite(motor_RGB_GREEN_PIN, 0);
    while (1)
      ;
  }
  analogWrite(motor_RGB_RED_PIN, 0);
  analogWrite(motor_RGB_GREEN_PIN, 255);
  Serial.println("INA219 MOTOR sensor initialized! :)");

  if (!ina219_TOTAL.begin()) {
    Serial.println("Failed to find TOTAL INA219");
    analogWrite(total_RGB_RED_PIN, 255);
    analogWrite(total_RGB_GREEN_PIN, 0);
    while (1)
      ;
  }
  analogWrite(total_RGB_RED_PIN, 0);
  analogWrite(total_RGB_GREEN_PIN, 255);
  Serial.println("INA219 TOTAL sensor initialized! :)");

  Serial.println("ALL INA219 sensors initialized! :)");
}

void checkForWiFi(bool firstEverCheck) {
  while (WiFi.status() != WL_CONNECTED) {
    digitalWrite(wifi_INDICATOR_LED, HIGH);
    WiFi.begin(ssid, password);
    for (int i = 0; i < 3; i++) {
      delay(250);
      digitalWrite(wifi_INDICATOR_LED, LOW);
      delay(250);
      digitalWrite(wifi_INDICATOR_LED, HIGH);
    }
    Serial.println("Wi-Fi Not Connected. Trying to Connect...");
  }
  digitalWrite(wifi_INDICATOR_LED, LOW);
  if (firstEverCheck) {
    Serial.println("Connected to Wi-Fi!");
  }
}

void checkForMQTT() {
  if (!mqttClient.connected()) {
    connectToMQTT();
  }
}

void setup() {
  Serial.println("Start");

  Serial.begin(9600);
  // Below is commented to allow running without USB (by connecting the Arduino directly to DC battery)
  /*while (!Serial) {
    delay(1);
  }*/

  // Set LED pins as outputs
  pinMode(wifi_INDICATOR_LED, OUTPUT);
  pinMode(mqtt_INDICATOR_LED, OUTPUT);

  pinMode(led_RGB_RED_PIN, OUTPUT);
  pinMode(led_RGB_GREEN_PIN, OUTPUT);
  pinMode(motor_RGB_RED_PIN, OUTPUT);
  pinMode(motor_RGB_GREEN_PIN, OUTPUT);
  pinMode(total_RGB_RED_PIN, OUTPUT);
  pinMode(total_RGB_GREEN_PIN, OUTPUT);

  digitalWrite(wifi_INDICATOR_LED, HIGH);
  digitalWrite(mqtt_INDICATOR_LED, HIGH);

  digitalWrite(led_RGB_RED_PIN, 0);
  digitalWrite(led_RGB_GREEN_PIN, 0);

  digitalWrite(motor_RGB_RED_PIN, 0);
  digitalWrite(motor_RGB_GREEN_PIN, 0);

  digitalWrite(total_RGB_RED_PIN, 0);
  digitalWrite(total_RGB_GREEN_PIN, 0);

  initializeSensors();

  checkForWiFi(true);

  connectToMQTT();

  timeClient.begin();
  timeClient.update();

  lastPublishTime = millis();
}

void loop() {
  samplesTaken++;

  sum_power_mW_LED += ina219_LED.getPower_mW();
  float temp_power_mW_LED = sum_power_mW_LED / samplesTaken;

  sum_power_mW_MOTOR += ina219_MOTOR.getPower_mW();
  float temp_power_mW_MOTOR = sum_power_mW_MOTOR / samplesTaken;

  sum_power_mW_TOTAL += ina219_TOTAL.getPower_mW();
  float temp_power_mW_TOTAL = sum_power_mW_TOTAL / samplesTaken;

  int fadeValue = map((long)temp_power_mW_LED, 200, led_MAXIMUM_THRESHOLD_MW, 0, 255);
  fadeValue = constrain(fadeValue, 0, 255);
  analogWrite(led_RGB_RED_PIN, fadeValue);
  analogWrite(led_RGB_GREEN_PIN, 255 - fadeValue);

  fadeValue = map((long)temp_power_mW_MOTOR, 200, motor_MAXIMUM_THRESHOLD_MW, 0, 255);
  fadeValue = constrain(fadeValue, 0, 255);
  analogWrite(motor_RGB_RED_PIN, fadeValue);
  analogWrite(motor_RGB_GREEN_PIN, 255 - fadeValue);

  fadeValue = map((long)temp_power_mW_TOTAL, 200, total_MAXIMUM_THRESHOLD_MW, 0, 255);
  fadeValue = constrain(fadeValue, 0, 255);
  analogWrite(total_RGB_RED_PIN, fadeValue);
  analogWrite(total_RGB_GREEN_PIN, 255 - fadeValue);

  if (millis() - lastPublishTime >= 1000) {
    checkForWiFi(false);
    checkForMQTT();

    long timestamp = timeClient.getEpochTime();

    // Calculate the average power
    float power_mW_LED = sum_power_mW_LED / samplesTaken;
    float power_mW_MOTOR = sum_power_mW_MOTOR / samplesTaken;
    float power_mW_TOTAL = sum_power_mW_TOTAL / samplesTaken;

    float difference = (power_mW_MOTOR + power_mW_LED) - power_mW_TOTAL;

    char payload[256];
    snprintf(payload, sizeof(payload),
             "{"
             "\"total_reading\":%.2f,"
             "\"led_reading\":%.2f,"
             "\"motor_reading\":%.2f,"
             "\"difference\":%.2f,"
             "\"samples\":%lu,"
             "\"timestamp\":%lu"
             "}",
             power_mW_TOTAL, power_mW_LED, power_mW_MOTOR, difference, samplesTaken, timestamp);


    Serial.println(payload);

    mqttClient.beginMessage(TOPIC);
    mqttClient.print(payload);
    mqttClient.endMessage();

    lastPublishTime = millis();

    samplesTaken = 0;
    sum_power_mW_LED = 0;
    sum_power_mW_MOTOR = 0;
    sum_power_mW_TOTAL = 0;
  }
}