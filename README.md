# BroilerFirmAutomation
```c
#if defined(ESP32)
#include <WiFi.h>
#include <FirebaseESP32.h>
#elif defined(ESP8266)
#include <ESP8266WiFi.h>
#include <FirebaseESP8266.h>
#endif

#include <NTPClient.h>
#include <WiFiUdp.h>

// Provide the token generation process info.
#include <addons/TokenHelper.h>

// Provide the RTDB payload printing info and other helper functions.
#include <addons/RTDBHelper.h>

/* 1. Define the WiFi credentials */
#define WIFI_SSID "Moin Khan"
#define WIFI_PASSWORD "01717511288"

// For the following credentials, see examples/Authentications/SignInAsUser/EmailPassword/EmailPassword.ino

/* 2. Define the API Key */
#define API_KEY "AIzaSyDoMnt1yF7tYB8PqX1xyksrsWRWqXz5dx8"

/* 3. Define the RTDB URL */
#define DATABASE_URL "https://imazharulsabbir.firebaseio.com" //<databaseName>.firebaseio.com or <databaseName>.<region>.firebasedatabase.app

/* 4. Define the user Email and password that alreadey registerd or added in your project */
#define USER_EMAIL "moinkhan4363@gmail.com"
#define USER_PASSWORD "01717511288"

// Define Firebase Data object
FirebaseData fbdo;
FirebaseData stream;

FirebaseAuth auth;
FirebaseConfig config;

// Define NTP Client to get time
WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "pool.ntp.org");

// Variable to save USER UID
String uid;

unsigned long sendDataPrevMillis = 0;

String childPath[4] = {"/device1", "/device2", "/device3", "/device4"};

int count = 0;

volatile bool dataChanged = false;

#include "DHT.h"
#include <Wire.h>

#define DHTPIN 2 // what pin we're connected to

// Uncomment whatever type you're using!
#define DHTTYPE DHT11 // DHT 11

// Initialize DHT sensor for normal 16mhz Arduino
DHT dht(DHTPIN, DHTTYPE);

unsigned long sendDhtDataPrevMillis = 0;
unsigned long sendWaterPumpPrevMillis = 0;

// relay module pin
int pin5 = D5;
int pin6 = D6;
int pin7 = D7;
int pin8 = D8;
// relay module pin ends

void setupRelayModulePins()
{
  pinMode(pin5, OUTPUT); // relay switch pin mode which connected to D5
  pinMode(pin6, OUTPUT); // relay switch pin mode which connected to D6
  pinMode(pin7, OUTPUT); // relay switch pin mode which connected to D7
  pinMode(pin8, OUTPUT); // relay switch pin mode which connected to D8

  digitalWrite(pin5, HIGH);
  digitalWrite(pin6, HIGH);
  digitalWrite(pin7, HIGH);
  digitalWrite(pin8, HIGH);
  // connect vcc to vin and gnd to g on nodemcu

//  resetDeviceStatus();
}

void readWaterLevelSensorData()
{
  int waterLevelValue = analogRead(A0); // Water Level Sensor output pin connected A0
  
  Serial.print("waterLevelValue: ");
  Serial.println(waterLevelValue);      // See the Value In Serial Monitor
  delay(100); // for timer

  String parentPath = "/user/" + uid + "/firm_data/water-level";
  FirebaseJson json;

  json.set("value", waterLevelValue);

  bool _write = false;

  if (waterLevelValue > 0 && waterLevelValue < 100)
  {
    // low
    json.set("level", "low");
    _write = true;
  }
  else if (waterLevelValue >= 100 && waterLevelValue < 250)
  {
    // medium
    json.set("level", "medium");
    _write = true;
  }
  else if (waterLevelValue >= 250)
  {
    // full
    json.set("level", "high");
    digitalWrite(pin8, HIGH); // turn off water pump
    _write = true;

//    if (Firebase.ready() && millis() - sendDhtDataPrevMillis > 6000)
//    {
//      sendWaterPumpPrevMillis = millis();
//      String parentPath2 = "/user/" + uid + "/firm_data/devices/device4";
//      FirebaseJson json;
//
//      int timestamp = getTime();
//
//      json.set("/id", "device4");
//      json.set("/name", "Water Pump");
//      json.set("/status", false);
//      json.set("/on_off_time",timestamp);
//
//      Serial.printf("Set device json... %s\n", Firebase.RTDB.setJSON(&fbdo, parentPath2.c_str(), &json) ? "ok" : fbdo.errorReason().c_str());
//    }
  }
  else
  {
    _write = false;
  }

  if (Firebase.ready() && _write)
  {
    Serial.printf("Set water level sensor json... %s\n", Firebase.RTDB.setJSON(&fbdo, parentPath.c_str(), &json) ? "ok" : fbdo.errorReason().c_str());
  }
}

// Function that gets current epoch time
unsigned long getTime()
{
  timeClient.update();
  unsigned long now = timeClient.getEpochTime();
  return now;
}

void readDhtSensorData()
{
  if (millis() - sendDhtDataPrevMillis > 3000)
  {
    sendDhtDataPrevMillis = millis();

    // Reading temperature or humidity takes about 250 milliseconds!
    // Sensor readings may also be up to 5 seconds 'old' (its a very slow sensor)
    float h = dht.readHumidity();
    // Read temperature as Celsius
    float t = dht.readTemperature();
    // Read temperature as Fahrenheit
    float f = dht.readTemperature(true);

    // Check if any reads failed and exit early (to try again).
    if (isnan(h) || isnan(t) || isnan(f))
    {
      Serial.println("Failed to read from DHT sensor!");
      return;
    }

    // Compute heat index
    // Must send in temp in Fahrenheit!
    float hi = dht.computeHeatIndex(f, h);

    if (Firebase.ready())
    {
      int timestamp = getTime();

      String parentPath = "/user/" + uid + "/firm_data/dht11/" + String(timestamp);
      FirebaseJson json;

      json.set("temp_c", t);
      json.set("temp_f", f);
      json.set("humidity", h);
      json.set("heat_index", hi);

      Serial.printf("Set dht11 sensor json... %s\n", Firebase.RTDB.setJSON(&fbdo, parentPath.c_str(), &json) ? "ok" : fbdo.errorReason().c_str());
    }
  }
}

void resetDeviceStatus()
{
  if (Firebase.ready())
  {
    String parentPath1 = "/user/" + uid + "/firm_data/devices/device1";
    String parentPath2 = "/user/" + uid + "/firm_data/devices/device2";
    String parentPath3 = "/user/" + uid + "/firm_data/devices/device3";
    String parentPath4 = "/user/" + uid + "/firm_data/devices/device4";

    FirebaseJson json;
    json.set("/status", false);

    Serial.printf("Set device json... %s\n", Firebase.RTDB.setJSON(&fbdo, parentPath1.c_str(), &json) ? "ok" : fbdo.errorReason().c_str());
    Serial.printf("Set device json... %s\n", Firebase.RTDB.setJSON(&fbdo, parentPath2.c_str(), &json) ? "ok" : fbdo.errorReason().c_str());
    Serial.printf("Set device json... %s\n", Firebase.RTDB.setJSON(&fbdo, parentPath3.c_str(), &json) ? "ok" : fbdo.errorReason().c_str());
    Serial.printf("Set device json... %s\n", Firebase.RTDB.setJSON(&fbdo, parentPath4.c_str(), &json) ? "ok" : fbdo.errorReason().c_str());
  }
}

void streamCallback(MultiPathStreamData result)
{
  size_t numChild = sizeof(childPath) / sizeof(childPath[0]);

  for (size_t i = 0; i < numChild; i++)
  {
    if (result.get(childPath[i]))
    {
      String _path = result.dataPath.c_str();

      FirebaseJson &json = stream.jsonObject();
      size_t len = json.iteratorBegin();

      String key, value = "";
      int type = 0;

      for (size_t i = 0; i < len; i++)
      {
        json.iteratorGet(i, type, key, value);
        String _result = String(value);
        
        if (key == "status")
        {
          if (_path == "/device1")
          {
            Serial.println(_path);

            if (_result == "true")
            {
              Serial.println("Enabled!");
              digitalWrite(pin5, LOW);
            }
            else
            {
              Serial.println("Disabled!");
              digitalWrite(pin5, HIGH);
            }
          }
          else if (_path == "/device2")
          {
            Serial.println(_path);

            if (_result == "true")
            {
              Serial.println("Enabled!");
              digitalWrite(pin6, LOW);
            }
            else
            {
              Serial.println("Disabled!");
              digitalWrite(pin6, HIGH);
            }
          }
          else if (_path == "/device3")
          {
            Serial.println(_path);

            if (_result == "true")
            {
              Serial.println("Enabled!");
              digitalWrite(pin7, LOW);
            }
            else
            {
              Serial.println("Disabled!");
              digitalWrite(pin7, HIGH);
            }
          }
          else if (_path == "/device4")
          {
            Serial.println(_path);

            if (_result == "true")
            {
              Serial.println("Enabled!");
              digitalWrite(pin8, LOW);
            }
            else
            {
              Serial.println("Disabled!");
              digitalWrite(pin8, HIGH);
            }
          }
          else
          {
            Serial.println(_path + _result);
          }
        }        
      }
      json.iteratorEnd();
    }
  }

  Serial.println();

  // This is the size of stream payload received (current and max value)
  // Max payload size is the payload size under the stream path since the stream connected
  // and read once and will not update until stream reconnection takes place.
  // This max value will be zero as no payload received in case of ESP8266 which
  // BearSSL reserved Rx buffer size is less than the actual stream payload.
  Serial.printf("Received stream payload size: %d (Max. %d)\n\n", stream.payloadLength(), stream.maxPayloadLength());

  // Due to limited of stack memory, do not perform any task that used large memory here especially starting connect to server.
  // Just set this flag and check it status later.
  dataChanged = true;
}

void streamTimeoutCallback(bool timeout)
{
  if (timeout)
    Serial.println("stream timed out, resuming...\n");

  if (!stream.httpConnected())
    Serial.printf("error code: %d, reason: %s\n\n", stream.httpCode(), stream.errorReason().c_str());
}

void setup()
{

  Serial.begin(115200);

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

  Serial.printf("Firebase Client v%s\n\n", FIREBASE_CLIENT_VERSION);

  /* Assign the api key (required) */
  config.api_key = API_KEY;

  /* Assign the user sign in credentials */
  auth.user.email = USER_EMAIL;
  auth.user.password = USER_PASSWORD;

  /* Assign the RTDB URL (required) */
  config.database_url = DATABASE_URL;

  /* Assign the callback function for the long running token generation task */
  config.token_status_callback = tokenStatusCallback; // see addons/TokenHelper.h

  // Or use legacy authenticate method
  // config.database_url = DATABASE_URL;
  // config.signer.tokens.legacy_token = "<database secret>";

  // To connect without auth in Test Mode, see Authentications/TestMode/TestMode.ino

  Firebase.begin(&config, &auth);

  // Getting the user UID might take a few seconds
  Serial.println("Getting User UID");
  while ((auth.token.uid) == "")
  {
    Serial.print('.');
    delay(1000);
  }
  // Print user UID
  uid = auth.token.uid.c_str();
  Serial.print("User UID: ");
  Serial.println(uid);

  Firebase.reconnectWiFi(true);

  // Recommend for ESP8266 stream, adjust the buffer size to match your stream data size
#if defined(ESP8266)
  stream.setBSSLBufferSize(2048 /* Rx in bytes, 512 - 16384 */, 512 /* Tx in bytes, 512 - 16384 */);
#endif

  // The data under the node being stream (parent path) should keep small
  // Large stream payload leads to the parsing error due to memory allocation.

  // The MultiPathStream works as normal stream with the payload parsing function.

  String parentPath = "/user/" + uid + "/firm_data/devices";

  if (!Firebase.beginMultiPathStream(stream, parentPath))
    Serial.printf("stream begin error, %s\n\n", stream.errorReason().c_str());

  Firebase.setMultiPathStreamCallback(stream, streamCallback, streamTimeoutCallback);
  dht.begin();

  setupRelayModulePins();
}

void loop()
{
  if (dataChanged)
  {
    dataChanged = false;
    Serial.println("Data Changed! \n");
    // When stream data is available, do anything here...
  }

  readWaterLevelSensorData();
  readDhtSensorData();
}
```
