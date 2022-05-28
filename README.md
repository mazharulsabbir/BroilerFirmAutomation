# BroilerFirmAutomation
```c
#if defined(ESP32)
#include <WiFi.h>
#include <FirebaseESP32.h>
#elif defined(ESP8266)
#include <ESP8266WiFi.h>
#include <FirebaseESP8266.h>
#endif

// Provide the token generation process info.
#include <addons/TokenHelper.h>

// Provide the RTDB payload printing info and other helper functions.
#include <addons/RTDBHelper.h>

/* 1. Define the WiFi credentials */
#define WIFI_SSID "Software section"
#define WIFI_PASSWORD "dft@123456"

// For the following credentials, see examples/Authentications/SignInAsUser/EmailPassword/EmailPassword.ino

/* 2. Define the API Key */
#define API_KEY "AIzaSyDoMnt1yF7tYB8PqX1xyksrsWRWqXz5dx8"

/* 3. Define the RTDB URL */
#define DATABASE_URL "https://imazharulsabbir.firebaseio.com" //<databaseName>.firebaseio.com or <databaseName>.<region>.firebasedatabase.app

/* 4. Define the user Email and password that alreadey registerd or added in your project */
#define USER_EMAIL "admin@mazharulsabbir.com"
#define USER_PASSWORD "admin@123"

// Define Firebase Data object
FirebaseData fbdo;
FirebaseData stream;

FirebaseAuth auth;
FirebaseConfig config;

// Variable to save USER UID
String uid;

unsigned long sendDataPrevMillis = 0;

String childPath[4] = {"/device1", "/device2", "/device3", "/device4"};

int count = 0;

volatile bool dataChanged = false;

#include "DHT.h"
#include <Wire.h>

#define DHTPIN 2     // what pin we're connected to

// Uncomment whatever type you're using!
#define DHTTYPE DHT11   // DHT 11 

// Initialize DHT sensor for normal 16mhz Arduino
DHT dht(DHTPIN, DHTTYPE);

unsigned long sendDhtDataPrevMillis = 0;

// relay module pin
int pin5 = D5;
int pin6 = D6;
int pin7 = D7;
int pin8 = D8;
// relay module pin ends

void setupRelayModulePins() {
  pinMode(pin5, OUTPUT); // relay switch pin mode which connected to D5
  pinMode(pin6, OUTPUT); // relay switch pin mode which connected to D6
  pinMode(pin7, OUTPUT); // relay switch pin mode which connected to D7
  pinMode(pin8, OUTPUT); // relay switch pin mode which connected to D8

  digitalWrite(pin5, HIGH);
  digitalWrite(pin6, HIGH);
  digitalWrite(pin7, HIGH);
  digitalWrite(pin8, HIGH);
  // connect vcc to vin and gnd to g on nodemcu

  resetDeviceStatus();
}

void readWaterLevelSensorData() {
  int waterLevelValue = analogRead(A0); // Water Level Sensor output pin connected A0
  Serial.println(waterLevelValue);  // See the Value In Serial Monitor
  delay(100);      // for timer

  String parentPath = "/user/" + uid + "/firm_data/water-level";
  FirebaseJson json;

  json.set("value", waterLevelValue);

  bool _write = false;

  if (waterLevelValue > 400 && waterLevelValue < 500) {
    // low
    json.set("level", "low");
    _write = true;
  }  else if (waterLevelValue >= 500 && waterLevelValue < 550) {
    // medium
    json.set("level", "medium");
    _write = true;
  } else if (waterLevelValue >= 550) {
    // full
    json.set("level", "high");
    _write = true;
  } else {
    _write = false;
  }

  if (Firebase.ready() && _write)
  {
    Serial.printf("Set water level sensor json... %s\n", Firebase.RTDB.setJSON(&fbdo, parentPath.c_str(), &json) ? "ok" : fbdo.errorReason().c_str());
  }
}

void readDhtSensorData() {
  if (millis() - sendDhtDataPrevMillis > 2000)
  {
    sendDhtDataPrevMillis = millis();

    // Reading temperature or humidity takes about 250 milliseconds!
    // Sensor readings may also be up to 2 seconds 'old' (its a very slow sensor)
    float h = dht.readHumidity();
    // Read temperature as Celsius
    float t = dht.readTemperature();
    // Read temperature as Fahrenheit
    float f = dht.readTemperature(true);

    // Check if any reads failed and exit early (to try again).
    if (isnan(h) || isnan(t) || isnan(f)) {
      Serial.println("Failed to read from DHT sensor!");
      return;
    }

    // Compute heat index
    // Must send in temp in Fahrenheit!
    float hi = dht.computeHeatIndex(f, h);

    if (Firebase.ready())
    {
      String parentPath = "/user/" + uid + "/firm_data/dht11";
      FirebaseJson json;

      json.set("temp_c", t);
      json.set("temp_f", f);
      json.set("humidity", h);
      json.set("heat_index", hi);

      Serial.printf("Set dht11 sensor json... %s\n", Firebase.RTDB.setJSON(&fbdo, parentPath.c_str(), &json) ? "ok" : fbdo.errorReason().c_str());
    }
  }
}

void resetDeviceStatus() {
  if (Firebase.ready())
  {
    String parentPath = "/user/" + uid + "/firm_data/devices";
    FirebaseJson json;

    json.set("device1/status", false);
    json.set("device2/status", false);
    json.set("device3/status", false);
    json.set("device4/status", false);

    Serial.printf("Set device json... %s\n", Firebase.RTDB.setJSON(&fbdo, parentPath.c_str(), &json) ? "ok" : fbdo.errorReason().c_str());
  }
}

void streamCallback(MultiPathStreamData stream)
{
  size_t numChild = sizeof(childPath) / sizeof(childPath[0]);

  for (size_t i = 0; i < numChild; i++)
  {
    if (stream.get(childPath[i]))
    {
      String _path = stream.dataPath.c_str();
      String _result = stream.value.c_str();

      if (_path == "/device1/status") {
        Serial.println(_path);

        if (_result == "true") {
          Serial.println("Enabled!");
          digitalWrite(pin5, LOW);
        } else {
          Serial.println("Disabled!");
          digitalWrite(pin5, HIGH);
        }
      } else if (_path == "/device2/status") {
        Serial.println(_path);

        if (_result == "true") {
          Serial.println("Enabled!");
          digitalWrite(pin6, LOW);
        } else {
          Serial.println("Disabled!");
          digitalWrite(pin6, HIGH);
        }
      } else if (_path == "/device3/status") {
        Serial.println(_path);

        if (_result == "true") {
          Serial.println("Enabled!");
          digitalWrite(pin7, LOW);
        } else {
          Serial.println("Disabled!");
          digitalWrite(pin7, HIGH);
        }
      } else if (_path == "/device4/status") {
        Serial.println(_path);

        if (_result == "true") {
          Serial.println("Enabled!");
          digitalWrite(pin8, LOW);
        } else {
          Serial.println("Disabled!");
          digitalWrite(pin8, HIGH);
        }
      }
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
  while ((auth.token.uid) == "") {
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
