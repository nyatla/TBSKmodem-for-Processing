/**
 * Debug application.
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
  TbskModem._DEBUG=true;

  // start Modem
  modem.start();
  last_rxn=modem.rxNumber();
}
long last_rxn;
String recvd="";
int dec_mode=0;
void draw() {
  background(0);
  text("Hit 'A':tx,'B':break tx,'C':clear,'D':switch rx mode",10,10);
  //Show status
  text(int(modem.acceptedSampleCount()),10,20);  
  var vol=max(0,(log(modem.rms())+5)*3);
  rect(10,30,10+vol*10,10);
  

  var rxnum=modem.rxNumber();
  text("byteReady " + (modem.rxReady()?"YES":"NO"),10,50);
  text("charReady " +(modem.rxAsCharReady()?"YES":"NO"),10,60);
  text("Signal#   " +rxnum,10,70);
  switch(dec_mode){
    case 0:break;
    case 1:
      if(modem.rxAsCharReady()){
        if(last_rxn!=rxnum){
          recvd="";
          last_rxn=rxnum;
        }
        recvd=recvd+modem.rxAsChar();
      }
      break;
    case 2:
      if(modem.rxReady()){
        if(last_rxn!=rxnum){
          recvd="";
          last_rxn=rxnum;
        }
        recvd=recvd+str(modem.rx())+" ";
      }
      break;
  }

  text(recvd,10,80);
}
void keyPressed() {
  if(key=='A'||key=='a'){
    if(modem.txReady()){
      println("OK1");
      modem.tx("Hello Processing");
      println("OK2");
    }
  }else if(key=='B'||key=='b'){
    modem.txBreak();
  }else if(key=='C'||key=='c'){
    modem.rxClear();
  }else if(key=='D'||key=='d'){
    dec_mode=(dec_mode+1)%3;
    println("rxmode "+str(dec_mode));
  }else if(key=='E'||key=='e'){
    modem.rxBreak();
  }else{
  }
}
