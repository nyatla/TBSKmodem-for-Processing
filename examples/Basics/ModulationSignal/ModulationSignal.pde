/**
 * Modulated waveform array.
 */
import jp.nyatla.tbskpsg.*;
import jp.nyatla.tbskpsg.audioif.*;
import jp.nyatla.tbskpsg.result.*;

TbskTone tone=TbskTone.xpskSin();
TbskPreamble preamble=TbskPreamble.coff(tone);
TbskModem modem=new TbskModem(this,tone,preamble);
float[] wave;
String str;
void setup() {
  size(640, 200);
  noStroke();
  wave=modem.modulate("Hello Processing").toArray();
  str=modem.demodulateAsStr(wave).toString();
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
