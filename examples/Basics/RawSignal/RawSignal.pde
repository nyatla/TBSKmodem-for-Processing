/**
 * Modulated waveform array.
 */
import jp.nyatla.tbskpsg.*;
import jp.nyatla.tbskpsg.audioif.*;
import jp.nyatla.tbskpsg.result.*;

TbskTone tone=TbskTone.xpskSin();
TbskModulator mod=new TbskModulator(this,tone);
TbskDemodulator demod=new TbskDemodulator(this,tone);
float[] wave;
String str;
void setup() {
  print(Version.STRING);
  size(640, 200);
  noStroke();
  //make waveform
  wave=mod.modulate("Hello Processing").toArray();
  //demodulate waveform
  str=demod.demodulateAsStr(wave).toString();
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
