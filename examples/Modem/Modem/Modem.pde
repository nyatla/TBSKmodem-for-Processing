import ddf.minim.*;

/**
 * Modulated wave file.
 */
import jp.nyatla.tbskpsg.*;
import jp.nyatla.tbskpsg.audioif.*;
import jp.nyatla.tbskpsg.result.*;
import jp.nyatla.tbskpsg.utils.*;
float[] wave;
String str;
TbskTone tone=TbskTone.xpskSin();
TbskPreamble preamble=TbskPreamble.coff(tone);

TbskModem modem;


void setup() {
  Minim minim=new Minim(this);
  modem=new TbskModem(this,tone,preamble,new MinimAudioInterface(minim,16000));
  TbskModem._DEBUG=true;
  modem.start();
  size(320, 200);
  noStroke();
}
int s=0;
void draw() {
//print(modem.rxReady());
  background(0);
  text("Hit 'A' key to send string!",10,10);
  text(modem.acceptedSampleCount(),10,20);
  text(modem.rms(),10,30);
  text(modem.rxReady()?"YES":"NO",10,40);
  if(modem.rxReadyAsChar()){
    print(modem.rx());
  }
}
void keyPressed() {
  if(key=='A'||key=='a'){
    if(modem.txReady()){
      modem.tx("Hello Processing",true);
    }
  }else{
    modem.txBreak();
  }
}
