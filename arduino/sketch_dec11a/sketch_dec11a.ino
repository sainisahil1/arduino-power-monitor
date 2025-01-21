#include "secrets.h"
#include <ArduinoMqttClient.h>
#include <WiFi.h>
#include <NTPClient.h>
#include <WiFiUdp.h>
#include <Adafruit_INA219.h>

// ======================= Global objects & variables =======================

WiFiSSLClient        espClient;
MqttClient           mqttClient(espClient);
WiFiUDP              ntpUDP;
NTPClient            timeClient(ntpUDP, "pool.ntp.org", 7200, 60000);

unsigned long lastPublishTime  = 0;
unsigned long samplesTaken     = 0;

float sumPowerLed   = 0.0f;
float sumPowerMotor = 0.0f;
float sumPowerTotal = 0.0f;

Adafruit_INA219 ina219Led(0x41);
Adafruit_INA219 ina219Motor(0x44);
Adafruit_INA219 ina219Total(0x40);

// Indicator and RGB pins
const int WIFI_INDICATOR_LED     = A6;
const int MQTT_INDICATOR_LED     = A3;
const int LED_RGB_RED_PIN        = 5;
const int LED_RGB_GREEN_PIN      = 4;
const int MOTOR_RGB_RED_PIN      = 3;
const int MOTOR_RGB_GREEN_PIN    = 2;
const int TOTAL_RGB_RED_PIN      = 0;
const int TOTAL_RGB_GREEN_PIN    = 1;

// Thresholds
const float LED_MAX_THRESHOLD    = 700.0f;
const float MOTOR_MAX_THRESHOLD  = 2000.0f;
const float TOTAL_MAX_THRESHOLD  = 3000.0f;

// Submission Frequency / How often to send to MQTT (ms)
const uint MQTT_SUBMISSION_FREQUENCY = 5000;

// ======================= Helper functions =======================

/**
 * Turns an indicator LED on or off.
 */
void setIndicatorLed(int pin, bool isOn) {
  digitalWrite(pin, (isOn ? HIGH : LOW));
}

/**
 * Sets an RGB pair to show a single colour: RED or GREEN.
 * If `redOn = true` and `greenOn = false`, the pins show RED.
 * If `redOn = false` and `greenOn = true`, the pins show GREEN.
 * If both are false, the pins are turned off.
 */
void setSimpleRgb(int redPin, int greenPin, bool redOn, bool greenOn) {
  analogWrite(redPin,   (redOn   ? 255 : 0));
  analogWrite(greenPin, (greenOn ? 255 : 0));
}

/**
 * Maps a (power) reading to a fade value on red/green pins:
 *   - “readingMin” (lower input bound)
 *   - “readingMax” (upper input bound)
 */
void fadeRgbByReading(float reading,
                      float readingMin,
                      float readingMax,
                      int   redPin,
                      int   greenPin) {
  // Convert reading to int for map
  int fadeValue = map((long)reading, (long)readingMin, (long)readingMax, 0, 255);
  fadeValue     = constrain(fadeValue, 0, 255);

  // Red goes from 0 to fadeValue, green from 255 down to some remainder
  analogWrite(redPin, fadeValue);
  analogWrite(greenPin, 255 - fadeValue);
}

/**
 * Connects to the Wi-Fi network. If not successful immediately, it keeps trying
 * until a connection is established.
 */
void connectWiFi(bool isFirstCheck) {
  while (WiFi.status() != WL_CONNECTED) {
    setIndicatorLed(WIFI_INDICATOR_LED, true);
    WiFi.begin(ssid, password);

    // Blink the Wi-Fi indicator LED a few times to show we are trying
    for (int i = 0; i < 3; i++) {
      delay(250);
      setIndicatorLed(WIFI_INDICATOR_LED, false);
      delay(250);
      setIndicatorLed(WIFI_INDICATOR_LED, true);
    }
    Serial.println("Wi-Fi not connected. Retrying...");
  }

  setIndicatorLed(WIFI_INDICATOR_LED, false);
  if (isFirstCheck) {
    Serial.println("Connected to Wi-Fi!");
  }
}

/**
 * Connects to the MQTT broker. If the connection fails, it tries repeatedly.
 */
void connectMQTT() {
  mqttClient.setUsernamePassword(USERNAME, PASS);
  mqttClient.setKeepAliveInterval(60000);

  while (!mqttClient.connected()) {
    setIndicatorLed(MQTT_INDICATOR_LED, true);
    Serial.println("Connecting to MQTT...");

    if (mqttClient.connect(PUBSUB_ENDPOINT, PORT)) {
      Serial.println("Connected to MQTT broker!");
      setIndicatorLed(MQTT_INDICATOR_LED, false);
    } else {
      Serial.print("Failed, rc=");
      Serial.print(mqttClient.connectError());
      Serial.println(". Trying again in 1 second...");

      // Blink the MQTT indicator a few times
      for (int i = 0; i < 3; i++) {
        delay(250);
        setIndicatorLed(MQTT_INDICATOR_LED, false);
        delay(250);
        setIndicatorLed(MQTT_INDICATOR_LED, true);
      }
    }
  }
  setIndicatorLed(MQTT_INDICATOR_LED, false);
}

/**
 * Initializes one INA219 sensor. If it fails, sets the corresponding RGB pins
 * to red and stops execution.
 */
void initializeSensor(Adafruit_INA219 &sensor,
                      const char      *name,
                      int              redPin,
                      int              greenPin) {
  Serial.print("Initializing INA219: ");
  Serial.println(name);

  if (!sensor.begin()) {
    Serial.print("Failed to find INA219 for ");
    Serial.println(name);
    setSimpleRgb(redPin, greenPin, true, false);  // red on
    while (true) { /* Halt here */ }
  }
  // If success, switch the sensor's LED to green
  setSimpleRgb(redPin, greenPin, false, true);
  Serial.print("INA219 ");
  Serial.print(name);
  Serial.println(" sensor initialized! :)");
}

/**
 * Initializes all sensors (LED, MOTOR, TOTAL).
 */
void initializeAllSensors() {
  initializeSensor(ina219Led,   "LED",   LED_RGB_RED_PIN,    LED_RGB_GREEN_PIN);
  initializeSensor(ina219Motor, "MOTOR", MOTOR_RGB_RED_PIN,  MOTOR_RGB_GREEN_PIN);
  initializeSensor(ina219Total, "TOTAL", TOTAL_RGB_RED_PIN,  TOTAL_RGB_GREEN_PIN);

  Serial.println("All INA219 sensors initialized successfully! :)");
}

/**
 * Helper to check Wi-Fi connection in loop and reconnect if needed.
 */
void checkForWiFi() {
  if (WiFi.status() != WL_CONNECTED) {
    connectWiFi(false);
  }
}

/**
 * Helper to check MQTT connection in loop and reconnect if needed.
 */
void checkForMQTT() {
  if (!mqttClient.connected()) {
    connectMQTT();
  }
}

// ======================= Arduino setup & loop =======================

void setup() {
  Serial.begin(9600);
  // Uncomment if you want the code to wait for the serial monitor
  /*
  while (!Serial) {
    delay(1);
  }
  */

  // Set LED pins as outputs
  pinMode(WIFI_INDICATOR_LED, OUTPUT);
  pinMode(MQTT_INDICATOR_LED, OUTPUT);

  pinMode(LED_RGB_RED_PIN,    OUTPUT);
  pinMode(LED_RGB_GREEN_PIN,  OUTPUT);
  pinMode(MOTOR_RGB_RED_PIN,  OUTPUT);
  pinMode(MOTOR_RGB_GREEN_PIN,OUTPUT);
  pinMode(TOTAL_RGB_RED_PIN,  OUTPUT);
  pinMode(TOTAL_RGB_GREEN_PIN,OUTPUT);

  // Initially turn on the Wi-Fi and MQTT indicator LEDs
  setIndicatorLed(WIFI_INDICATOR_LED, true);
  setIndicatorLed(MQTT_INDICATOR_LED, true);

  // Clear RGB LED outputs
  setSimpleRgb(LED_RGB_RED_PIN,    LED_RGB_GREEN_PIN,    false, false);
  setSimpleRgb(MOTOR_RGB_RED_PIN,  MOTOR_RGB_GREEN_PIN,  false, false);
  setSimpleRgb(TOTAL_RGB_RED_PIN,  TOTAL_RGB_GREEN_PIN,  false, false);

  initializeAllSensors();
  connectWiFi(true);  // First time: show a log
  connectMQTT();

  timeClient.begin();
  timeClient.update();

  lastPublishTime = millis();

  Serial.println("Setup completed.\n");
}

void loop() {
  // Take one “sample” from each sensor
  samplesTaken++;

  sumPowerLed   += ina219Led.getPower_mW();
  sumPowerMotor += ina219Motor.getPower_mW();
  sumPowerTotal += ina219Total.getPower_mW();

  // Compute the rolling average so far
  float avgPowerLed   = sumPowerLed   / samplesTaken;
  float avgPowerMotor = sumPowerMotor / samplesTaken;
  float avgPowerTotal = sumPowerTotal / samplesTaken;

  // Update each sensor’s RGB LED to reflect average usage
  fadeRgbByReading(avgPowerLed,   200.0f, LED_MAX_THRESHOLD,   LED_RGB_RED_PIN,    LED_RGB_GREEN_PIN);
  fadeRgbByReading(avgPowerMotor, 200.0f, MOTOR_MAX_THRESHOLD, MOTOR_RGB_RED_PIN,  MOTOR_RGB_GREEN_PIN);
  fadeRgbByReading(avgPowerTotal, 200.0f, TOTAL_MAX_THRESHOLD, TOTAL_RGB_RED_PIN,  TOTAL_RGB_GREEN_PIN);

  // Publish data roughly every second
  if (millis() - lastPublishTime >= MQTT_SUBMISSION_FREQUENCY) {
    checkForWiFi();
    checkForMQTT();

    timeClient.update();
    long timestamp = timeClient.getEpochTime();

    float difference = (avgPowerMotor + avgPowerLed) - avgPowerTotal;

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
      avgPowerTotal, avgPowerLed, avgPowerMotor, difference, samplesTaken, timestamp
    );

    Serial.println(payload);

    mqttClient.beginMessage(TOPIC);
    mqttClient.print(payload);
    mqttClient.endMessage();

    // Reset counters and sums
    lastPublishTime = millis();
    samplesTaken    = 0;
    sumPowerLed     = 0.0f;
    sumPowerMotor   = 0.0f;
    sumPowerTotal   = 0.0f;
  }
}
