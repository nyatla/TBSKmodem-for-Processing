/**
 * BYTE receiving on GUI.
 */
import ddf.minim.*;
import jp.nyatla.tbskpsg.*;
import jp.nyatla.tbskpsg.audioif.*;
import jp.nyatla.tbskpsg.result.*;
import jp.nyatla.tbskpsg.utils.*;

TbskTone tone=TbskTone.xpskSin();
TbskPreamble preamble=TbskPreamble.coff(tone);

TbskModem modem;


void setup() {
  size(320, 200);
  noStroke();

  print(Version.STRING);
  PFont font = createFont("Osaka", 10);
  textFont(font);

  //Initialize modem instance with Audio interface
  Minim minim=new Minim(this);
  modem=new TbskModem(this,tone,preamble,new MinimAudioInterface(minim,16000));
  //TbskModem._DEBUG=true;

  // start Modem
  modem.start();
  last_rxn=modem.rxNumber();
}
long last_rxn;
String recvd="";
int dec_mode=0;
void draw() {
  background(0);

  //Show status
  text(int(modem.acceptedSampleCount()),10,20);  
  var vol=max(0,(log(modem.rms())+6.5)*3);
  rect(10,30,10+vol*10,10);  
  var rxnum=modem.rxNumber();
  text("byteReady " + (modem.rxReady()?"YES":"NO"),10,50);

  //Show received
  text("Signal#   " +rxnum,10,60);
  if(modem.rxReady()){
    if(last_rxn!=rxnum){
      recvd="";
      last_rxn=rxnum;
    }
    recvd=recvd+str(modem.rx())+" ";
  }
  text(recvd,10,70);
}
