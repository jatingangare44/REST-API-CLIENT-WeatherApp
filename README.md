# REST-API-CLIENT-WeatherApp
# ☀️ Weather App (Internship Task - 2)

This is a **Java Swing-based GUI application** that consumes a **public REST API** to fetch real-time weather data (temperature, humidity, weather condition) for any city in **India**, based on the state and city input provided by the user.

## 🚀 Features

- ✅ Dropdown menu for selecting **Indian States**
- ✅ Text field for entering **City**
- ✅ Displays:
  - 🌡️ Temperature
  - 💧 Humidity
  - 🌤️ Weather condition with a descriptive label
  - 🕒 Timestamp of the request
- ✅ Saves each request and report to a text file (`weather_report.txt`) with city and state info
- ✅ Handles **HTTP GET** requests and **parses JSON** responses using the OpenWeather and Nominatim API

---

## 📌 Task Description

**Task Name:** Internship Task - 2  
**Objective:** Build a Java Application that consumes a REST API and presents data in a structured format.  
**Requirement:** Handle HTTP requests and parse JSON responses.

---

## 📦 Tech Stack

- Java (JDK 8+)
- Swing (GUI)
- `java.net.HttpURLConnection`
- `org.json` (JSON parsing)
- Open-Meteo API (https://open-meteo.com)
- Nominatim API for geolocation (https://nominatim.org/)

---

## 🛠️ How to Run

1. **Clone or download** the repository.
2. Make sure your `CLASSPATH` includes `org.json` library. You can add this jar from:
   [https://mvnrepository.com/artifact/org.json/json](https://mvnrepository.com/artifact/org.json/json)
3. Compile and run:

```bash
javac -cp .;json-20240303.jar task_2/WeatherGUIApp.java
java -cp .;json-20240303.jar task_2.WeatherGUIApp
