/**
 * Send transaction to audio device.
 */

import ddf.minim.*;

import jp.nyatla.tbskpsg.*;
import jp.nyatla.tbskpsg.audioif.*;
import jp.nyatla.tbskpsg.result.*;
import jp.nyatla.tbskpsg.utils.*;

Minim minim=new Minim(this);
TbskTone tone=TbskTone.xpskSin();
TbskPreamble preamble=TbskPreamble.coff(tone);
TbskModem modem=new TbskModem(this,tone,preamble,new MinimAudioInterface(minim,16000));

float[] wave;
String str;
void setup() {
  size(320, 200);
  noStroke();
  background(0);
  noLoop();
}
int s=0;
void draw() {
  
  text("Hit 'A' key to send string!",10,10);
}
void keyPressed() {
  if(key=='A'||key=='a'){
    if(modem.txReady()){
      modem.tx("Hello Processing",true);
    }
  }else{
    modem.txStop();
  }
}
