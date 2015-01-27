#include "SDPArduino.h"
#include <Wire.h>


#define DEBUG 1

//Globals
bool ON = false;

//Moving
bool motorsChanged = false;
byte motorMapping[2] = {0, 1};
int motorPower[2] = {0,0};
int motorDirs[2] = {-1, 1};
int motorMultiplier[2] = {-1, 1};

//Kicking
#define KICK_DELAY_MOVING_UP 250
#define KICK_DELAY_UP 200
#define KICK_DELAY_MOVING_DOWN 220

#define KICK_STATE_IDLE 0
#define KICK_STATE_START 1
#define KICK_STATE_MOVING_UP 2
#define KICK_STATE_UP 3
#define KICK_STATE_MOVING_DOWN 4

#define KICK_MOTOR 3
#define KICK_MOTOR_DIR -1

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
    motorAllStop();
  } 
}


void moveMotor(int motor, int power){
  /*
  debugPrint("\n***MM ");
  debugPrint(motor);
  debugPrint(", ");
  debugPrint(power);
  debugPrint(" **\n");
  */
  
  
  if(power >= 0){
    motorForward(motor, power);
  }
  else if(power < 0){
    motorBackward(motor, -power);
  }
  else {
    motorStop(motor);
  }
}



void doKick(){  
  if(kickState == KICK_STATE_START){
    kickStartTime = millis();
    
    kickState = KICK_STATE_MOVING_UP;
    
    moveMotor(KICK_MOTOR, kickPower * KICK_MOTOR_DIR);
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
      
      moveMotor(KICK_MOTOR, -kickPower * KICK_MOTOR_DIR);
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
    
    int i = 0;
    for( ; i < 2; i++)
    {
       if(motorMapping[i] > -1){
         moveMotor(motorMapping[i], motorPower[i] * motorDirs[i] * motorMultiplier[i]);
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
      
      motorsChanged = false;
      kickState = KICK_STATE_IDLE;
      
    }
    else if (incoming == 'M') // Motor
    {
      Serial.print('C');
      debugPrint("MOTORS ");
      
      int i = 0;
      for( ; i < 2; i++)
      {
        int nextByte = waitForByte();      
        motorPower[i] = nextByte;
        nextByte = waitForByte();
        
        if(nextByte == 0)
          motorDirs[i] = 1;
        else
          motorDirs[i] = -1;
        
        
        debugPrint(motorPower[i]);
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
      Serial.print('E');
      
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
