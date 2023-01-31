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
	class Listener extends ArrayBlockingQueue<Double> implements AudioListener 
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
			//キューがいっぱいの場合は先頭を削除しながら押し込む。
			for(double i : arg0) {
				if(!this.offer(i)) {
					this.remove();
					this.add(i);
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
				double d=(arg0[i]+arg1[1])*0.5;
				if(!this.offer(d)) {
					this.remove();
					this.add(d);
				}
			}
		}
		synchronized public void kill() {
			if(!this.offer(null)) {
				this.remove();
				this.add(null);
			}
			this._enable=false;
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
	/**
	 * ブロック中にstopすると
	 */
	@Override
	public Double next() throws PyStopIteration {
		try {
			synchronized(this._listener) {
				Double d=this._listener.take();//簡単にしたいのでブロックする。
				if(d==null) {//ターミネイトマーカー
					throw new PyStopIteration();
				}
				return d;
			}
		} catch (InterruptedException e) {
			throw new PyStopIteration(e);
		}
	}
	@Override
	public void close() throws IOException {
		this._listener.kill();
		this._in.removeListener(this._listener);
	}	
}