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
  TbskPreamble preamble=TbskPreamble.coff(tone);
  modem=new TbskModem(this,tone,preamble,new MinimAudioInterface(minim,16000));

  // start Modem
  modem.start();
  last_rxn=modem.rxNumber();
}
void draw() {
  background(0);
  var n=modem.rxNumber();
  if(last_rxn!=n){
    print("\nNo."+str(n)+":");
    last_rxn=n;
  }
  if(modem.rxAsCharReady()){
    print(str(modem.rxAsChar())+" ");
  }
}
