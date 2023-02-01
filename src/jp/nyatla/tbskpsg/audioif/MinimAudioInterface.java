package jp.nyatla.tbskpsg.audioif;

import ddf.minim.Minim;
import jp.nyatla.tbskpsg.result.ModulateIterable;

public class MinimAudioInterface implements IAudioInterface {
	private Minim _minim;
	private int _sample_rate;
	private int _buffer_size;

	public MinimAudioInterface(Minim minim,int sample_rate) {
		this(minim,sample_rate,-1);
	}

	public MinimAudioInterface(Minim minim,int sample_rate,int input_buffer_size) {
		this._minim=minim;
		this._sample_rate=sample_rate;
		this._buffer_size=input_buffer_size;
	}
	@Override
	public int getSampleRate() {		
		return this._sample_rate;
	}
	public Minim getMinim() {
		return this._minim;
	}

	@Override
	public IAudioInputIterator createInputIterator() {
		if(this._buffer_size==-1) {
			return new MinimAudioInputIterator(this._minim,this._sample_rate);
		}else {
			return new MinimAudioInputIterator(this._minim,this._sample_rate,this._buffer_size);
		}
	}

	@Override
	public IAudioPlayer createPlayer(ModulateIterable s) {
		return new MinimAudioPlayer(this._minim, this._sample_rate, s);
	}

}