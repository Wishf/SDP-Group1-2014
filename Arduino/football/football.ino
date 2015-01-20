
#define DEBUG true

//Globals
bool ON = false;

int motors[4] = {0,0,0,0};

bool kick = false;
int kickPower = 0;



void setup() {
  pinMode(13, OUTPUT);    // initialize pin 13 as digital output (LED)
  pinMode(8, OUTPUT);     // initialize pin 8 to control the radio
  digitalWrite(8, HIGH);  // select the radio
  Serial.begin(115200);     // start the serial port at 115200 baud (correct for XinoRF and RFu, if using XRF + Arduino you might need 9600)
  debugPrint("STARTED");// transmit started packet

}


//3 kick 2byte
//4 motor 4byte
//5 read sensor 2byte
void loop() {
  
  //Coms
  readComms();
 
 
 
  //Control 
  if(ON)
  {
    digitalWrite(13, HIGH); 
  }
  else
  {
    digitalWrite(13, LOW); 
  } 
}



void readComms(){
  if (Serial.available()) // have we got enough characters for a message?
  {
    byte incoming = Serial.read();
    if (incoming == 'D') //Deactivate
    {
      debugPrint("DEACTIVATED ");
      ON = false;
    }
    else if (incoming == 'A') // Activate
    {
      debugPrint("ACTIVATED ");
      ON = true;
    }
    else if (incoming == 'M') // Motor
    {
      debugPrint("MOTORS ");
      
      int nextByte = waitForByte();      
      motors[0] = nextByte - 128;
      debugPrint(motors[0]);
      debugPrint(" ");
      
      
      nextByte = waitForByte();      
      motors[1] = nextByte - 128;
      debugPrint(motors[1]);
      debugPrint(" ");
      
      nextByte = waitForByte();      
      motors[2] = nextByte - 128;
      debugPrint(motors[2]);
      debugPrint(" ");
      
      nextByte = waitForByte();      
      motors[3] = nextByte - 128;     
      debugPrint(motors[3]);
      debugPrint(" ");
    }
    else if (incoming == 'K') // Kick
    {
      debugPrint("KICK");
      
      int nextByte = waitForByte();   
      kick = true;
      kickPower = nextByte;
      
      debugPrint(" ");
      debugPrint(kickPower);
      
    }
    else if (incoming == 'R') // Sensor read
    {
      debugPrint("READ");
      int nextByte = waitForByte(); 
      //Read sensor
      //Reply
      debugPrint(" ");
      debugPrint(nextByte);
    }
    else{
      debugPrint(" GET OFF MY LAWN ");
    }
  }   
}


byte waitForByte()
{
  while(!Serial.available());
  return Serial.read();
}


void debugPrint(char msg[]){
  if(DEBUG){
    Serial.print(msg);
  }
}

void debugPrint(int msg){
  if(DEBUG){
    Serial.print(msg, DEC);
  }
}
