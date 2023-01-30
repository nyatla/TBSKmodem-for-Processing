package jp.nyatla.tbskpsg.audioif;

import ddf.minim.Minim;
import jp.nyatla.tbskpsg.result.ModulateIterable;

public class MinimAudioInterface implements IAudioInterface {
	private Minim _minim;
	private int _sample_rate;

	public MinimAudioInterface(Minim minim,int sample_rate) {
		this._minim=minim;
		this._sample_rate=sample_rate;
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
		return new MinimAudioInputIterator(this._minim,this._sample_rate);
	}

	@Override
	public IAudioPlayer createPlayer(ModulateIterable s) {
		return new MinimAudioPlayer(this._minim, this._sample_rate, s);
	}

}