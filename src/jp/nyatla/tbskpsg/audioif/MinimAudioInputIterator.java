package jp.nyatla.tbskpsg.audioif;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import ddf.minim.AudioInput;
import ddf.minim.AudioListener;
import ddf.minim.Minim;
import jp.nyatla.kokolink.types.Py__class__.PyStopIteration;
import jp.nyatla.kokolink.utils.math.Rms;
import jp.nyatla.tbskpsg.TbskModem;


public class MinimAudioInputIterator implements IAudioInputIterator
{
	private class Listener implements AudioListener 
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private boolean _enable;
		private Rms _rms;
		private long _sample_counter;
		private BlockingQueue<Double> _bq;


		public Listener(int capacity,int rms_length) {
			this._bq=new ArrayBlockingQueue<Double>(capacity);
			this._rms=new Rms(rms_length);
			this._sample_counter=0;
			this._enable=true;
		}

		@Override
		synchronized public void samples(float[] arg0) {
			//TbskModem.debug(String.format("IN1:%d",arg0.length));			
			this._sample_counter=this._sample_counter+arg0.length;
			if(!this._enable) {
				int l=arg0.length;
				for(int i=0;i<l;i++) {
					if(!this._bq.offer(0.)) {
						this._bq.remove();
						this._bq.add(0.);
					}
					this._rms.update(i);					
				}
			}else {
				//キューがいっぱいの場合は先頭を削除しながら押し込む。
				for(double i : arg0) {
					if(!this._bq.offer(i)) {
						this._bq.remove();
						this._bq.add(i);
					};
					this._rms.update(i);
				}
			}
		}

		@Override
		synchronized public void samples(float[] arg0, float[] arg1) {
			//TbskModem.debug(String.format("IN2:%d,%d",arg0.length,arg1.length));
			this._sample_counter=this._sample_counter+arg0.length;
			int l=Math.min(arg0.length,arg1.length);
			if(!this._enable) {
				for(int i=0;i<l;i++) {
					if(!this._bq.offer(0.)) {
						this._bq.remove();
						this._bq.add(0.);
					}
					this._rms.update(i);					
				}
			}else {
				for(int i=0;i<l;i++) {
					double d=(arg0[i]+arg1[1])*0.5;
					if(!this._bq.offer(d)) {
						this._bq.remove();
						this._bq.add(d);
					}
					this._rms.update(i);
				}				
			}
		}
		public double take() throws InterruptedException {
			return this._bq.take();
		}
		synchronized public void mute(boolean v) {
			this._enable=!v;
		}

		/**
		 * 平均RMS値
		 * @return
		 */
		public double rms() {
			return this._rms.getLastRms();
		}
		/**
		 * 受信したサンプル数
		 * @return
		 */
		public long sampleCount() {
			return this._sample_counter;
		}
		
	}
	


	private AudioInput _in;
	private Listener _listener;
	public MinimAudioInputIterator(Minim minim,int sampleRate)
	{
		this(minim, sampleRate, 512);
	}
	/**
	 * 
	 * @param minim
	 * @param sampleRate
	 * @param bufferSize
	 */
	public MinimAudioInputIterator(Minim minim,int sampleRate,int bufferSize)
	{	
		TbskModem.debug(String.format("sampleRate:%d,bufferSize:%d", sampleRate,bufferSize));
		AudioInput in=minim.getLineIn(Minim.MONO,bufferSize, sampleRate);
		Listener listener=new Listener((sampleRate*2),Math.max(sampleRate/100,10));
		in.addListener(listener);
		this._in=in;
		this._listener=listener;
		TbskModem.debug("MinimAudioInputIterator/");

	}
	@Override
	public void mute(boolean v) {
		this._listener.mute(v);
	}
	/**
	 * ブロック中にstopすると
	 */
	@Override
	public Double next() throws PyStopIteration {
		try {
			return this._listener.take();//簡単にしたいのでブロックする。
		} catch (InterruptedException e) {
			TbskModem.debug("receive interrupt in MinimAudioInput");
			throw new PyStopIteration(e);
		}
	}
	/**
	 * この関数を呼び出す前に、nextを呼び出すスレッドにinterruptで割り込みを入れて止めておくこと。
	 */
	@Override
	public void close() throws IOException {
		this._listener.mute(true);
		this._in.removeListener(this._listener);
		this._in.close();
	}
	@Override
	public long acceptedSamples() {
		// TODO Auto-generated method stub
		return this._listener.sampleCount();
	}
	@Override
	public double getRMS() {
		return this._listener.rms();
	}	
}