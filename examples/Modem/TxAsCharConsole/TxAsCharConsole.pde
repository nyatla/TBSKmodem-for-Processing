/**
 * Simplest charactor receive
 */
import ddf.minim.*;
import jp.nyatla.tbskpsg.*;
import jp.nyatla.tbskpsg.audioif.*;
import jp.nyatla.tbskpsg.result.*;
import jp.nyatla.tbskpsg.utils.*;

TbskModem modem;
long last_rxn;

void setup() {
  print(Version.STRING);

  //Initialize modem instance with Audio interface
  Minim minim=new Minim(this);
  TbskTone tone=TbskTone.xpskSin();
  modem=new TbskModem(this,tone,new MinimAudioInterface(minim,16000));

  // start Modem
  modem.start();
  last_rxn=modem.rxNumber();
}
void draw() {
  background(0);
  if(modem.rxReady()){
    modem.rxClear();
  }
}


void keyPressed() {
  if(key=='A'||key=='a'){
    if(modem.txReady()){
      modem.tx("Hello Processing");
    }
  }else if(key=='B'||key=='b'){
    modem.txBreak();
  }else{
  }
}
