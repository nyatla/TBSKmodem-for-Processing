package jp.nyatla.tbskpsg.audioif;

import jp.nyatla.tbskpsg.result.ModulateIterable;

public class NullAudioInterface implements IAudioInterface {

	private int _sample_rate;
	public NullAudioInterface(int sample_rate){
		this._sample_rate=sample_rate;
	}
	@Override
	public IAudioInputIterator createInputIterator() {
		return null;
	}

	@Override
	public IAudioPlayer createPlayer(ModulateIterable s) {
		return null;
	}

	@Override
	public int getSampleRate() {
		// TODO Auto-generated method stub
		return this._sample_rate;
	}

}
