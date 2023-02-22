/**
 * Modulated wave file.
 */
import jp.nyatla.tbskpsg.*;
import jp.nyatla.tbskpsg.audioif.*;
import jp.nyatla.tbskpsg.result.*;
import jp.nyatla.tbskpsg.utils.*;

TbskTone tone=TbskTone.xpskSin();
TbskPreamble preamble=TbskPreamble.coff(tone);
TbskModulator mod=new TbskModulator(this,tone);
TbskDemodulator demod=new TbskDemodulator(this,tone);
float[] wave;
String str;
void setup() {
  print(Version.STRING);
  size(640, 200);
  noStroke();

  //Modulation
  var waveresult=mod.modulate("Hello Processing");

  //Save to file
  var wavefile1=waveresult.toWaveFile(16000,.1f);
  wavefile1.save("./test.wav");

  //Load from file
  var wavefile2=(new WaveFile(this)).load("./test.wav");
  str=demod.demodulateAsStr(wavefile2).toString();
  wave=wavefile2.getFrame();
}
int s=0;
void draw() {
  int W=this.width;
  int H=this.height;
  background(0);
  stroke(153);
  int n=(s%(wave.length-W-1));
  s=s+10;
  for (int i=0;i<W;i++) {
    line(i,H/2*(wave[i+n]+1),i+1,H/2*(wave[i+1+n]+1));
  }
  text(str,W/2,H/2);
}
