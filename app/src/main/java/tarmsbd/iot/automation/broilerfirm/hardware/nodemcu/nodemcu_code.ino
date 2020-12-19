#include <ESP8266WiFi.h>
#include <FirebaseESP8266.h>

// temp and humidity sensor
#include "DHT.h"
#include <Wire.h>

// thingspeak api
#include "ThingSpeak.h"

// Set these to run firebase.
#define FIREBASE_HOST "iot-abs.firebaseio.com"
#define FIREBASE_AUTH "m4dS7dKquNI4s9zrE7B36eUnxizaKA6Se8OYBG95"

// ThingSpeak api
#define CHANNEL_NUMBER 1125511
#define WRITE_API_KEY "GLRPZJZ768UMGPA8"
#define READ_API_KEY "H1X7OCPWB21LMY7U"

// Temperature and Humidity sensor
#define DHTPIN 0
#define DHTTYPE DHT11

DHT dht(DHTPIN, DHTTYPE);
WiFiClient  client;

// Wifi name and password
#define WIFI_SSID "MERCUSYS"
#define WIFI_PASSWORD "beniasohokola123"

//Define FirebaseESP8266 data object
FirebaseData fireFirm;

unsigned long sendTempDataPrevMillis = 0;
unsigned long sendHumidityDataPrevMillis = 0;

String parentPath = "/user/AoFjPjVl0DRyKrIwvqPZNBI6HZ32/firm_data/devices";
String childPath[4] = {"/device1", "/device2", "/device3", "/device4"};
size_t childPathSize = 4;

void streamCallback(MultiPathStreamData stream)
{
  Serial.println();
  Serial.println("Stream Data1 available...");

  size_t numChild = sizeof(childPath) / sizeof(childPath[0]);

  for (size_t i = 0; i < numChild; i++)
  {
    if (stream.get(childPath[i]))
    {
      String path = stream.dataPath;
      Serial.println("Path: " + path + ", Data Type:" + stream.type);

      FirebaseJson &json = fireFirm.jsonObject();
      size_t len = json.iteratorBegin();
      String key, value = "";
      int type = 0;
      for (size_t i = 0; i < len; i++)
      {
        json.iteratorGet(i, type, key, value);
        if (key == "status" && path == "/device1") {
          Serial.print("Fan Status: ");
          Serial.println(value);
          if(value){
           Serial1.print(101);
          }else{
            Serial1.print(102);
          }
        } else if (key == "status" && path == "/device2") {
          Serial.print("Light Status: ");
          Serial.println(value);
          if(value){
           Serial1.print(103);
          }else{
            Serial1.print(104);
          }
        } else if (key == "status" && path == "/device3") {
          Serial.print("Motor Status: ");
          Serial.println(value);
          if(value){
           Serial1.print(106);
          }else{
            Serial1.print(105);
          }
        } else if (key == "status" && path == "/device4") {
          Serial.print("Water Pump Status: ");
          Serial.println(value);
          if(value){
           Serial1.print(107);
          }else{
            Serial1.print(108);
          }
        }
      }
      json.iteratorEnd();
    }
  }
  Serial.println();
}

void printResult(FirebaseData &data)
{
  if (data.dataType() == "int")
    Serial.println(data.intData());
  else if (data.dataType() == "float")
    Serial.println(data.floatData(), 5);
  else if (data.dataType() == "double")
    printf("%.9lf\n", data.doubleData());
  else if (data.dataType() == "boolean")
    Serial.println(data.boolData() == 1 ? "true" : "false");
  else if (data.dataType() == "string")
    Serial.println(data.stringData());
  else if (data.dataType() == "json")
  {
    FirebaseJson &json = data.jsonObject();
    size_t len = json.iteratorBegin();
    String key, value = "";
    int type = 0;
    for (size_t i = 0; i < len; i++)
    {
      json.iteratorGet(i, type, key, value);
      Serial.print(i);
      Serial.print(", ");
      Serial.print("Type: ");
      Serial.print(type == FirebaseJson::JSON_OBJECT ? "object" : "array");
      if (type == FirebaseJson::JSON_OBJECT)
      {
        Serial.print(", Key: ");
        Serial.print(key);
      }
      Serial.print(", Value: ");
      Serial.println(value);
    }
    json.iteratorEnd();
  }
  else if (data.dataType() == "array")
  {
    Serial.println();
    //get array data from FirebaseData using FirebaseJsonArray object
    FirebaseJsonArray &arr = data.jsonArray();
    //Print all array values
    Serial.println("Pretty printed Array:");
    String arrStr;
    arr.toString(arrStr, true);
    Serial.println(arrStr);
    Serial.println();
    Serial.println("Iterate array values:");
    Serial.println();
    for (size_t i = 0; i < arr.size(); i++)
    {
      Serial.print(i);
      Serial.print(", Value: ");

      FirebaseJsonData &jsonData = data.jsonData();
      //Get the result data from FirebaseJsonArray object
      arr.get(jsonData, i);
      if (jsonData.typeNum == FirebaseJson::JSON_BOOL)
        Serial.println(jsonData.boolValue ? "true" : "false");
      else if (jsonData.typeNum == FirebaseJson::JSON_INT)
        Serial.println(jsonData.intValue);
      else if (jsonData.typeNum == FirebaseJson::JSON_FLOAT)
        Serial.println(jsonData.floatValue);
      else if (jsonData.typeNum == FirebaseJson::JSON_DOUBLE)
        printf("%.9lf\n", jsonData.doubleValue);
      else if (jsonData.typeNum == FirebaseJson::JSON_STRING ||
               jsonData.typeNum == FirebaseJson::JSON_NULL ||
               jsonData.typeNum == FirebaseJson::JSON_OBJECT ||
               jsonData.typeNum == FirebaseJson::JSON_ARRAY)
        Serial.println(jsonData.stringValue);
    }
  }
  else if (data.dataType() == "blob")
  {

    Serial.println();

    for (int i = 0; i < data.blobData().size(); i++)
    {
      if (i > 0 && i % 16 == 0)
        Serial.println();

      if (i < 16)
        Serial.print("0");

      Serial.print(data.blobData()[i], HEX);
      Serial.print(" ");
    }
    Serial.println();
  }
  else if (data.dataType() == "file")
  {

    Serial.println();

    File file = data.fileStream();
    int i = 0;

    while (file.available())
    {
      if (i > 0 && i % 16 == 0)
        Serial.println();

      int v = file.read();

      if (v < 16)
        Serial.print("0");

      Serial.print(v, HEX);
      Serial.print(" ");
      i++;
    }
    Serial.println();
    file.close();
  }
  else
  {
    Serial.println(data.payload());
  }
}

void streamTimeoutCallback(bool timeout)
{
  if (timeout)
  {
    Serial.println();
    Serial.println("Stream timeout, resume streaming...");
    Serial.println();
  }
}

void setup()
{
  Serial.begin(115200);
  Serial1.begin(115200);

  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to Wi-Fi");
  while (WiFi.status() != WL_CONNECTED)
  {
    Serial.print(".");
    delay(300);
  }
  Serial.println();
  Serial.print("Connected with IP: ");
  Serial.println(WiFi.localIP());
  Serial.println();

  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  Firebase.reconnectWiFi(true);

  //Set the size of WiFi rx/tx buffers in the case where we want to work with large data.
  fireFirm.setBSSLBufferSize(1024, 1024);

  //Set the size of HTTP response buffers in the case where we want to work with large data.
  fireFirm.setResponseSize(1024);

  if (!Firebase.beginMultiPathStream(fireFirm, parentPath, childPath, childPathSize))
  {
    Serial.println("------------------------------------");
    Serial.println("Can't begin stream connection...");
    Serial.println("REASON: " + fireFirm.errorReason());
    Serial.println("------------------------------------");
    Serial.println();
  }

  Firebase.setMultiPathStreamCallback(fireFirm, streamCallback, streamTimeoutCallback);

  dht.begin();
  ThingSpeak.begin(client);
}

void loop()
{
  // check wifi connectvity
  while (WiFi.status() != WL_CONNECTED)
  {
    Serial.print(".");
    delay(300);
  }

  // upload thinkspeak data
  if (millis() - sendTempDataPrevMillis > 5000)
  {
    sendTempDataPrevMillis = millis();
    writeTemperatureData();
  }

  if (millis() - sendHumidityDataPrevMillis > 10000)
  {
    sendHumidityDataPrevMillis = millis();
    writeHumidityData();
  }
}

void writeTemperatureData() {
  // Read temperature as Celsius (the default)
  float temp = dht.readTemperature();

  // Write value to Field 1 of a ThingSpeak Channel
  if (!isnan(temp)) {
    Serial.println("Temperature: ");
    Serial.print(temp);
    Serial.println();

    int httpCode = ThingSpeak.writeField(CHANNEL_NUMBER, 1, temp, WRITE_API_KEY);

    if (httpCode == 200) {
      Serial.println("Temperature Channel write successful.");
    }
    else {
      //      Serial.println("Problem writing to channel. HTTP error code " + String(httpCode));
    }
  } else {
    //    Serial.println("Failed to read temperature data");
  }
}

void writeHumidityData() {
  // read humidity data
  float humidity = dht.readHumidity();

  if (!isnan(humidity)) {
    Serial.println("Humidity: ");
    Serial.print(humidity);
    Serial.println();

    // Write value to Field 2 of a ThingSpeak Channel
    int result = ThingSpeak.writeField(CHANNEL_NUMBER, 2, humidity, WRITE_API_KEY);

    if (result == 200) {
      Serial.println("Humidity Channel write successful.");
    }
    else {
      //      Serial.println("Problem writing to channel. HTTP error code " + String(result));
    }
  } else {
    //    Serial.println("Failed to read humidity data");
  }
}