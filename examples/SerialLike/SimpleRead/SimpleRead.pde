/**
 * Simple Read
 * 
 * Read data from the serial port and change the color of a rectangle
 * when a switch connected to a Wiring or Arduino board is pressed and released.
 * This example works with the Wiring / Arduino program that follows below.
 */
import ddf.minim.*;

import jp.nyatla.tbskpsg.*;
import jp.nyatla.tbskpsg.audioif.*;
import jp.nyatla.tbskpsg.result.*;
import jp.nyatla.tbskpsg.utils.*;


TbskSerial myPort;  // Create object from Serial class
int val;      // Data received from the serial port

void setup() 
{
  size(200, 200);
  Minim minim=new Minim(this);
  TbskTone tone=TbskTone.xpskSin();
  TbskPreamble preamble=TbskPreamble.coff(tone);
  myPort = new TbskSerial(this,tone,preamble,new MinimAudioInterface(minim,16000));
}

void draw()
{
  if ( myPort.available() > 0) {  // If data is available,
    val = myPort.read();         // read it and store it in val
    background(val);             // Set background to white
  }else{
    background(255);             // Set background to white
  }
}
