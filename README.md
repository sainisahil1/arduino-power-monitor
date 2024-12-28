### Arduino Power Monitor

## About

- This repository is part of a univeristy IOT project.
- The IOT project consist of Arduino circuit, MQTT broker, Spring Boot app, React.js app and a Flutter app.
- This particular repository consist of C++ code to be run on arduino, the Spring Boot app and MQTT broker implementation.
- The objective of the project is to monitor power usage of various devices and provide realtime data along with daily, weekly, and monthly analysis.
- For MQTT broker, EMQX is used to send data from Arduino.
- Spring Boot app, web app and mobile app subscribes to MQTT topic for realtime updates.
- For storing historical data, InfluxDB is used mainly for the reason that it is best suitable for IOT focused time-series data. The inverse-indexing makes it a better option for quering historical data based on time scale along with downsampling.

## Workflow

![](https://github.com/sainisahil1/arduino-power-monitor/blob/main/arduinoflow2.png)
