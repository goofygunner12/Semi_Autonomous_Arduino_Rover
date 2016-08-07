// # Editor    :Holiday from DFRobot
// # Data      :30.05.2013
 
// # Product name:ultrasonic scanner 
// # Product SKU:SEN0001
// # Version :  3.2
 
// # Description:
// # This sample shows how to use the Autonomous trigger mode by writing its EEPROM 
 
// # Connection:
// #       Pin 1 VCC (URM V3.2) -> VCC (Arduino)
// #       Pin 2 GND (URM V3.2) -> GND (Arduino)
// #       Pin 6 COMP/TRIG (URM V3.2) -> Pin 2 (Arduino)
// #
 
int cmmd1[]={
  0x44,0x00,0x10,0x54};//low byte stored in the sensor for the distance threshold.
int cmmd2[]={
  0x44,0x01,0x00,0x45};//high byte, write 0x0010 into address 0x01 and 0x00,so the threshold is set to 16cm
int cmmd3[]={
  0x44,0x02,0xaa,0xf0};// Autonomous mode. write 0xaa into address 0x02
//int cmmd3[]={
//  0x44,0x02,0xbb,0x01};  // PWM mode. write 0xbb into address 0x02
int i;
 
void setup(){                                 
  Serial.begin(9600);                         // Sets the baud rate to 9600
  A_Mode_Setup();                             //PWM mode setup function
}
 
void loop()
{
}                      
 
void A_Mode_Setup(){ 
  //write the data into the URM37 EEPROM
  for (i=0;i<4;i++)
    Serial.write(cmmd3[i]);                             
  delay(200);                                           
 
  for (i=0;i<4;i++)
    Serial.write(cmmd1[i]);
  delay(200);
 
  for (i=0;i<4;i++)
    Serial.write(cmmd2[i]);
  delay(200);
 
}
