/**** Semi-Autonomous Navigation Rover ****/

int ultraSonicPin = 13; //Pin for Ultra-sonic sensor
int sensorRead = 0; //Snesor data to check obstacle in threshold range defined.
int E1 = 6; //M1 Speed Control
int E2 = 5; //M2 Speed Control
int M1 = 8; //M1 Direction Control
int M2 = 7; //M2 Direction Control
boolean started = false;

/* function to initialize pin mode -- serial mode */
void setup()
{
  pinMode(ultraSonicPin, OUTPUT);
  int i;
  for(i=5;i<=8;i++)
  pinMode(i, OUTPUT);
  Serial.begin(9600);
}

/* this function does the function in a continous loop and controls the activity on arduino board */
void loop()
{
  int leftspeed = 255;
  int rightspeed = 255;
  while(started == false)
  {
    char cmdFromPhone = Serial.read();
    if(cmdFromPhone == 's') // condition to check the char received via phone s -- start rover forwards
    {
      started= true;      
    }
    else if(cmdFromPhone =='t') // condition to check the char received via phone t -- to stop the rover
    {
      started = false;
      stop();
    }
  }
  char cmdFromPhone = Serial.read();
  if(cmdFromPhone=='t') // condition to check the char received via phone 
  {
    started = false;
    stop();
  }
  if (sensorRead=!(digitalRead(2)))
  {
    right(leftspeed,rightspeed); // if any object is in the range of threshold 16cms defined, rover moves right
    delay(2000);
  }
  else{
    forward(leftspeed,rightspeed); //if no obstacle in the range of threshold 16cms, rover is set to move forward
  } 
}
/* function to stop */
void stop(void) 
{
  digitalWrite(E1,LOW);
  digitalWrite(E2,LOW);
}
/* function to move forward */
void forward(char a,char b)
{
  analogWrite (E1,a);
  digitalWrite(M1,HIGH);
  analogWrite (E2,b);
  digitalWrite(M2,HIGH);
}
/* function to move right when nearing to an obstacle */
void right(char a,char b)
{
  analogWrite (E1,a);
  digitalWrite(M1,HIGH);
  analogWrite (E2,b);
  digitalWrite(M2,LOW);
}


