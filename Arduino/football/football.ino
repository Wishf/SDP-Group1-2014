#include "SDPArduino.h"
#include <Wire.h>


#define DEBUG false

//Globals
bool ON = false;

//Moving
bool motorsChanged = false;
int motors[4] = {0,0,0,0};

//Kicking
#define KICK_DELAY_MOVING_UP 500
#define KICK_DELAY_UP 100
#define KICK_DELAY_MOVING_DOWN 500

#define KICK_STATE_IDLE 0
#define KICK_STATE_START 1
#define KICK_STATE_MOVING_UP 2
#define KICK_STATE_UP 3
#define KICK_STATE_MOVING_DOWN 4

#define KICK_MOTOR 3

long kickStartTime;
int kickState = KICK_STATE_IDLE;
int kickPower = 0;



void setup() {
  
  SDPsetup(); 
  motorAllStop();
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
    doMotors();
    doKick();
  }
  else
  {    
  } 
}


void doKick(){
  if(kickState == KICK_STATE_START){
    kickStartTime = millis();
    
    kickState = KICK_STATE_MOVING_UP;
    
    motorForward(KICK_MOTOR, kickPower);
  }
  else if(kickState == KICK_STATE_MOVING_UP){
    if(millis() - kickStartTime > KICK_DELAY_MOVING_UP){
      kickStartTime = millis();
      
      kickState = KICK_STATE_UP;
    
      motorStop(KICK_MOTOR); 
    }
  }
  else if(kickState == KICK_STATE_UP){
    if(millis() - kickStartTime > KICK_DELAY_UP){
      kickStartTime = millis();
      
      kickState = KICK_STATE_MOVING_DOWN;
      
      motorBackward(KICK_MOTOR, 255);
    }
  } 
  else if(kickState == KICK_STATE_MOVING_DOWN)
    if(millis() - kickStartTime > KICK_DELAY_MOVING_DOWN){
      kickState = KICK_STATE_IDLE;
      
      motorStop(KICK_MOTOR); 
  } 
}



void doMotors(){
  if(motorsChanged){
    motorsChanged = false;
    
    int i = 1;
    for( ; i < 3; i++)
    {
      
      
      if(motors[i] > 0){
        int power = motors[i] * 2;
        
        motorBackward(i, power);
      }
      else if(motors[i] < 0){
        
        int power = -motors[i] * 2;
        
        motorBackward(i, power);
      }
      else{
       motorStop(i);  
      }
    }    
  } 
}


void readComms(){
  if (Serial.available()) // have we got enough characters for a message?
  {
    byte incoming = Serial.read();
    if (incoming == 'D') //Deactivate
    {
      Serial.print('C');
      debugPrint("DEACTIVATED ");
      ON = false;
    }
    else if (incoming == 'A') // Activate
    {
      Serial.print('C');
      debugPrint("ACTIVATED ");
      ON = true;
      
    }
    else if (incoming == 'M') // Motor
    {
      Serial.print('C');
      debugPrint("MOTORS ");
      
      int i = 0;
      for( ; i < 4; i++)
      {
        int nextByte = waitForByte();      
        motors[i] = (int)nextByte - 128;
        debugPrint(motors[i]);
        debugPrint(" ");
      }
      
      motorsChanged = true;
      
    }
    else if (incoming == 'K') // Kick
    {
      Serial.print('C');
      debugPrint("KICK");
      
      int nextByte = waitForByte();   
      
      if(kickState == KICK_STATE_IDLE){
        kickState = KICK_STATE_START;
        kickPower = nextByte;
      }
      
      
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
      
      
      Serial.print('S');
      Serial.print("1234");
    }
    else{
      debugPrint(" GET OFF MY LAWN ");
      
      Serial.print('E');
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
