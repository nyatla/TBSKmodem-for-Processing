/**
 * Serial Duplex 
 * by Tom Igoe. 
 * 
 * Sends a byte out the serial port when you type a key
 * listens for bytes received, and displays their value. 
 * This is just a quick application for testing serial data
 * in both directions. 
 */
import ddf.minim.*;

import jp.nyatla.tbskpsg.*;
import jp.nyatla.tbskpsg.audioif.*;
import jp.nyatla.tbskpsg.result.*;
import jp.nyatla.tbskpsg.utils.*;

import processing.serial.*;

TbskSerial myPort;  // Create object from Serial class
int val;        // Data received from the serial port
int whichKey = -1;  // Variable to hold keystoke values
int inByte = -1;    // Incoming serial data
void setup() 
{
  PFont myFont = createFont(PFont.list()[2], 14);
  textFont(myFont);
  
  size(400, 300);
  Minim minim=new Minim(this);
  TbskTone tone=TbskTone.xpskSin();
  TbskPreamble preamble=TbskPreamble.coff(tone);
  myPort = new TbskSerial(this,tone,preamble,new MinimAudioInterface(minim,16000));

}

void draw() {
  background(0);
  text("Last Received: " + inByte, 10, 130);
  text("Last Sent: " + whichKey, 10, 100);
  inByte = myPort.read();

}



void keyPressed() {
  // Send the keystroke out:
  myPort.write(key);
  whichKey = key;
}
