package jp.nyatla.tbskpsg.audioif;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;

import ddf.minim.AudioSample;
import ddf.minim.Minim;
import jp.nyatla.tbskpsg.result.ModulateIterable;

public class MinimAudioPlayer implements IAudioPlayer{
	private Minim _minim;
	private AudioSample _src;
	private int _length_in_msec;
	private long _start_time;

	public MinimAudioPlayer(Minim minim,int sampleRate,ModulateIterable d){
		this._minim=minim;
		float[] s=d.toArray();
		this._src=this._minim.createSample(s,new AudioFormat(sampleRate,16,1,true,false));
		this._length_in_msec=(s.length+((sampleRate/1000)-1))*1000/sampleRate;//ms単位の長さ
	} 

	@Override
	public void close() throws IOException {
		if(this._src!=null) {
			this._src.stop();
			this._src.close();
			this._src=null;
		}
	}

	@Override
	public void play()
	{
		assert(this._src!=null);
		this._src.trigger();
		this._start_time=System.currentTimeMillis();
		
	}

	@Override
	public void stop() {
		this._src.stop();
	}
	@Override
	public boolean waitForFinished(long timeout_in_msec) throws InterruptedException {
		long rem=this._length_in_msec-(System.currentTimeMillis()-this._start_time);//残り時間
		if(rem<1) {
			return true;//既に完了
		}else if(timeout_in_msec<0) {
			Thread.sleep(rem);//残存時間ブロックして完了
			return true;//完了
		}else if(rem<=timeout_in_msec) {
			Thread.sleep(rem);
			return true;//残存時間ブロックして完了
		}else {
			Thread.sleep(timeout_in_msec);
			return false;//ブロックして未完了
		}
	}		
}