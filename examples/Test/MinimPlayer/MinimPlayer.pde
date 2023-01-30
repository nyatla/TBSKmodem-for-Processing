import ddf.minim.*;
import ddf.minim.analysis.*;
import ddf.minim.effects.*;
import ddf.minim.signals.*;
import ddf.minim.spi.*;
import ddf.minim.ugens.*;
import javax.sound.sampled.*;

import jp.nyatla.tbskpsg.*;
import jp.nyatla.tbskpsg.audioif.*;
import jp.nyatla.tbskpsg.result.*;
import jp.nyatla.tbskpsg.utils.*;

TbskTone tone=TbskTone.xpskSin();
TbskPreamble preamble=TbskPreamble.coff(tone);
TbskModem modem=new TbskModem(this);


var s=modem.modulate("aaaa");
var al=s.toArray();
var sampleRate=16000;
var minim=new Minim(this);
var a=new AudioFormat(sampleRate,16,1,true,false);
var m=minim.createSample(al,a);
var len=(al.length+((sampleRate/1000)-1))*1000/sampleRate;  
print(len);
