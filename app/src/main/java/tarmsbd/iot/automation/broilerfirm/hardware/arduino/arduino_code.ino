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
    switch (response) {
      case 49: digitalWrite(LIGHT, HIGH);
        Serial.println("Light, On");
        break;
      case 50: digitalWrite(LIGHT, LOW);
        Serial.println("Light, Off");
        break;

      case 51: digitalWrite(MOTOR, HIGH);
        Serial.println("Motor, On");
        break;
      case 52: digitalWrite(MOTOR, LOW);
        Serial.println("Motor, Off");
        break;

      case 53: digitalWrite(PUMP, HIGH);
        Serial.println("Water Pump, On");
        break;
      case 54: digitalWrite(PUMP, LOW);
        Serial.println("Water Pump, Off");
        break;

      case 55: digitalWrite(FAN, HIGH);
        Serial.println("FAN, ON");
        break;
      case 56: digitalWrite(FAN, LOW);
        Serial.println("FAN, OFF");
        break;
    }
  }
}