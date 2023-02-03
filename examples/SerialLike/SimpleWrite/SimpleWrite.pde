/**
 * Simple Write. 
 * 
 * Check if the mouse is over a rectangle and writes the status to the serial port. 
 * This example works with the Wiring / Arduino program that follows below.
 */
import ddf.minim.*;

import jp.nyatla.tbskpsg.*;
import jp.nyatla.tbskpsg.audioif.*;
import jp.nyatla.tbskpsg.result.*;
import jp.nyatla.tbskpsg.utils.*;

import processing.serial.*;

TbskSerial myPort;  // Create object from Serial class
int val;        // Data received from the serial port

void setup() 
{
  size(200, 200);
  Minim minim=new Minim(this);
  TbskTone tone=TbskTone.xpskSin();
  TbskPreamble preamble=TbskPreamble.coff(tone);
  myPort = new TbskSerial(this,tone,preamble,new MinimAudioInterface(minim,16000));

}

void draw() {
  background(255);
  if (mouseOverRect() == true) {  // If mouse is over square,
    fill(204);                    // change color and
    myPort.write('H');              // send an H to indicate mouse is over square
  } 
  else {                        // If mouse is not over square,
    fill(0);                      // change color and
    myPort.write('L');              // send an L otherwise
  }
  rect(50, 50, 100, 100);         // Draw a square
}

boolean mouseOverRect() { // Test if mouse is over square
  return ((mouseX >= 50) && (mouseX <= 150) && (mouseY >= 50) && (mouseY <= 150));
}
