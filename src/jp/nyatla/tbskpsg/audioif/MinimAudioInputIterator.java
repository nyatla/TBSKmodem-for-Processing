package jp.nyatla.tbskpsg.audioif;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import ddf.minim.AudioInput;
import ddf.minim.AudioListener;
import ddf.minim.Minim;
import jp.nyatla.kokolink.types.Py__class__.PyStopIteration;
import jp.nyatla.kokolink.utils.recoverable.RecoverableStopIteration;


public class MinimAudioInputIterator implements IAudioInputIterator
{
	class Listener extends ArrayBlockingQueue<Float> implements AudioListener 
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private boolean _enable;

		public Listener(int capacity) {
			super(capacity);
		}

		@Override
		synchronized public void samples(float[] arg0) {
			if(!this._enable) {
				return;
			}
			for(float i : arg0) {
				if(!this.offer(i)) {
					this.remove();
				};
			}
		}

		@Override
		synchronized public void samples(float[] arg0, float[] arg1) {
			if(!this._enable) {
				return;
			}
			int l=Math.min(arg0.length,arg0.length);
			for(int i=0;i<l;i++) {
				if(!this.offer((arg0[i]+arg1[1])*0.5f)) {
					this.remove();
				}
			}
		}
		public void setEnable(boolean b) {
			this._enable=b;
		}
	}

	private AudioInput _in;
	private Listener _listener;
	public MinimAudioInputIterator(Minim minim,int sampleRate)
	{			
		AudioInput in=minim.getLineIn(Minim.MONO,1024, sampleRate);
		Listener listener=new Listener((int)(this._in.getFormat().getFrameRate()*2));
		this._in.addListener(listener);
		this._in=in;
		this._listener=listener;

	}



	@Override
	public Float next() throws PyStopIteration {
		try {
			synchronized(this._listener) {
				Float r=this._listener.poll(0, TimeUnit.MILLISECONDS);
				if(r==null) {
					throw new RecoverableStopIteration();
				}
				return r;					
			}
		} catch (InterruptedException e) {
			throw new PyStopIteration(e);
		}
	}

	@Override
	public void close() throws IOException {
		this.stop();
		this._in.removeListener(null);
	}

	@Override
	public void start() {
		this._listener.clear();
		this._listener.setEnable(true);
	}

	@Override
	public void stop() {
		synchronized(this._listener) {
			this._listener.setEnable(false);
		}
	}
	
}