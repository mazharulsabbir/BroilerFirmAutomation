const int FAN = 6;
const int LIGHT = 7;
const int MOTOR = 8;
const int PUMP = 9;

void setup() {
  // Open serial communications and wait for port to open:
  Serial.begin(115200);
  while (!Serial) {
    ; // wait for serial port to connect. Needed for native USB port only
  }

  pinMode(FAN, OUTPUT);
  pinMode(LIGHT, OUTPUT);
  pinMode(MOTOR, OUTPUT);
  pinMode(PUMP, OUTPUT);
  Serial.println("Output Pins: Fan 6, Light 7,Motor 8,Pump 9");
}

void loop() { // run over and over
  if (Serial.available()) {
    int response = Serial.read();
    Serial.println(response);

    switch (response) {
      case 48: digitalWrite(FAN, LOW);
        Serial.println("FAN, HIGH");
        break;
      case 49: digitalWrite(FAN, HIGH);
        Serial.println("FAN, LOW");
        break;

      case 51: digitalWrite(LIGHT, HIGH);
        Serial.println("LIGHT, HIGH");
        break;
      case 52: digitalWrite(LIGHT, LOW);
        Serial.println("LIGHT, LOW");
        break;

      case 53: digitalWrite(MOTOR, HIGH);
        Serial.println("MOTOR, HIGH");
        break;
      case 54: digitalWrite(MOTOR, LOW);
        Serial.println("MOTOR, LOW");
        break;

      case 55: digitalWrite(PUMP, HIGH);
        Serial.println("PUMP, HIGH");
        break;
      case 56: digitalWrite(PUMP, LOW);
        Serial.println("PUMP, LOW");
        break;
    }
  }
}