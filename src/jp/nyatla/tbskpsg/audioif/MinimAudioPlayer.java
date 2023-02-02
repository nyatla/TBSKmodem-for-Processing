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
	final static int DELAY=100;

	public MinimAudioPlayer(Minim minim,int sampleRate,ModulateIterable d){
		this._minim=minim;
		float[] s=d.toArray();
		this._src=this._minim.createSample(s,new AudioFormat(sampleRate,16,1,true,false));
		this._length_in_msec=(s.length+((sampleRate/1000)-1))*1000/sampleRate+DELAY;//ms単位の長さ
	} 

	@Override
	public void close() throws IOException {
		this.stop();
	}
	private boolean _is_finished=false;
	@Override
	public void play() throws InterruptedException
	{
		assert(this._src!=null && !this._is_finished);
		this._src.trigger();
		try {
			Thread.sleep(this._length_in_msec);
		}catch(InterruptedException e) {
			this.stop();
			throw e;
		}finally {
			this._is_finished=true;
		}
	}

	@Override
	public void stop() {
		if(this._src!=null) {
			this._src.stop();
			this._src.close();
			this._src=null;
		}
	}

	@Override
	public boolean isFinished() {
		// TODO Auto-generated method stub
		return this._is_finished;
	}

		
}