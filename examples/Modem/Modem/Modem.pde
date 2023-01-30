import ddf.minim.*;
import ddf.minim.analysis.*;
import ddf.minim.effects.*;
import ddf.minim.signals.*;
import ddf.minim.spi.*;
import ddf.minim.ugens.*;

/**
 * Modulated wave file.
 */
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
}
int s=0;
void draw() {
}
void keyPressed() {
  print(modem.txReady());
  
  modem.tx("123456789qwertyuissssssssssssssssss",true);
}
