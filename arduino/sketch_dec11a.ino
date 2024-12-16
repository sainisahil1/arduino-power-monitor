#include "thingProperties.h"

// float total_current;

// const char WIFI_SSID[] = "FRITZ!Box 7520 QB";
// const char WIFI_PASSWORD[] = "02100898750741008823";

void setup() {
  // put your setup code here, to run once:

  Serial.begin(115200);
  delay(1500); 

  initProperties();

  ArduinoCloud.begin(ArduinoIoTPreferredConnection);

  // ArduinoCloud.addProperty(total_current, READ, ON_CHANGE, NULL);

  // WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  // while (WiFi.status() != WL_CONNECTED) {
  //   delay(3000);
  //   Serial.print(".");
  //   WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  // }
  // Serial.println("WiFi connected!");
  // Serial.println(WiFi.localIP());

  // ArduinoCloud.begin(ArduinoIoTPreferredConnection(WIFI_SSID, WIFI_PASSWORD));

  // total_current = 0.0;

  setDebugMessageLevel(2);
  ArduinoCloud.printDebugInfo();

}

void loop() {
  // put your main code here, to run repeatedly:
  ArduinoCloud.update();

  total_current = random(1000) / 100.0;

  Serial.print("Sending sensor value: ");
  Serial.println(total_current);

  

  delay(5000);

}
