/**
 * Modulated wave file.
 */
import jp.nyatla.tbskpsg.*;
import jp.nyatla.tbskpsg.audioif.*;
import jp.nyatla.tbskpsg.result.*;
import jp.nyatla.tbskpsg.utils.*;

TbskModem modem;
float[] wave;
String str;
void setup() {
  size(640, 200);
  noStroke();
  modem=new TbskModem(this);
  var waveresult=modem.modulate("Hello Processing");

  //save to file
  var wavefile1=waveresult.toWaveFile(16000,0.1);
  wavefile1.save("./test.wav");

  //load from file
  var wavefile2=(new WaveFile(this)).load("./test.wav");
  str=modem.demodulateAsStr(wavefile2).toString(); //<>//
  wave=wavefile2.getFrame();
}
int s=0;
void draw() {
  int W=this.width;
  int H=this.height;
  background(0);
  stroke(153);
  int n=(s%(wave.length-W-1));
  s=s+1;
  for (int i=0;i<W;i++) {
    line(i,H/2*(wave[i+n]+1),i+1,H/2*(wave[i+1+n]+1));
  }
  text(str,W/2,H/2);
}
