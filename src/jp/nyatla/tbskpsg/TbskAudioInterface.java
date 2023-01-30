package jp.nyatla.tbskpsg;

import ddf.minim.Minim;
import jp.nyatla.tbskpsg.audioif.IAudioInputIterator;
import jp.nyatla.tbskpsg.audioif.IAudioInterface;
import jp.nyatla.tbskpsg.audioif.IAudioPlayer;
import jp.nyatla.tbskpsg.audioif.MinimAudioInputIterator;
import jp.nyatla.tbskpsg.audioif.MinimAudioPlayer;
import jp.nyatla.tbskpsg.result.ModulateIterable;

public class TbskAudioInterface {
	public static class DummyInterface implements IAudioInterface {
		private int _sample_rate;
		public DummyInterface(int sample_rate){
			this._sample_rate=sample_rate;
		}
		@Override
		public int getSampleRate() {
			// TODO Auto-generated method stub
			return this._sample_rate;
		}
		@Override
		public IAudioInputIterator createInputIterator() {
			return null;
		}
		@Override
		public IAudioPlayer createPlayer(ModulateIterable s) {
			return null;
		}

	}
	public static class MinimInterface implements IAudioInterface {
		private Minim _minim;
		private int _sample_rate;
		public MinimInterface(Minim minim,int sample_rate) {
			this._minim=minim;
			this._sample_rate=sample_rate;
		}
		@Override
		public IAudioInputIterator createInputIterator() {
			return new MinimAudioInputIterator(this._minim,this._sample_rate);
		}
		@Override
		public IAudioPlayer createPlayer(ModulateIterable s) {
			return new MinimAudioPlayer(this._minim,this._sample_rate,s);
		}
		@Override
		public int getSampleRate() {
			return this._sample_rate;
		}
	}	
	public IAudioInterface dummy(int sampleRate) {
		return new DummyInterface(sampleRate);
	}
	public IAudioInterface minim(Minim minim,int sampleRate) {
		return new MinimInterface(minim,sampleRate);
	}
}
