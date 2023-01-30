package jp.nyatla.tbskpsg.result;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jp.nyatla.tbskpsg.utils.WaveFile;
import processing.core.PApplet;



public class ModulateIterable extends BaseResultIterable<Float>
{
	static class CastIterable implements Iterable<Float>{
		private Iterator<Double> _iter;
		CastIterable(Iterator<Double> s){
			this._iter=s;
		}
		@Override
		public Iterator<Float> iterator() {
			Iterator<Double> iter=this._iter;
			return new Iterator<Float>(){
				@Override
				public boolean hasNext() {
					return iter.hasNext();
				}
				@Override
				public Float next() {
					return iter.next().floatValue();
				}					
			};
		}
		
	}

	public ModulateIterable(PApplet parent,Iterable<Double> s){
		super(parent,new CastIterable(s.iterator()));
	}

	public float[] toArray()
	{
		List<Float> t=this.toList();//checkExpired
    	float[] r=new float[t.size()];
    	for(int i=0;i<r.length;i++) {
    		r[i]=t.get(i);
    	}
    	return r;			
	}
	/**
	 * 値をWavファイルに変換します。
	 * @param sample_bits
	 * サンプリングビット数(8 or 16)
	 * @param sample_rate
	 * サンプリングレート
	 * @param silence_time
	 * 前後に挿入する無音信号時間(sec)
	 * @return
	 */
	public WaveFile toWaveFile(int sample_rate,int sample_bits,float silence)
	{
		int num_of_silence_ticks=Math.round(silence*sample_rate);
		if(num_of_silence_ticks==0) {
			return new WaveFile(this._parent,this, sample_rate,sample_bits);		
		}else {
			List<Float> d=new ArrayList<Float>();
			for(int i=0;i<num_of_silence_ticks;i++) {
				d.add(0f);
			}
			for(Float i:this) {
				d.add(i);
			}
			for(int i=0;i<num_of_silence_ticks;i++) {
				d.add(0f);
			}
			return new WaveFile(this._parent,d, sample_rate,sample_bits);
		}
	}
	public WaveFile toWaveFile(int sample_rate,float silence) {
		return this.toWaveFile(sample_rate, 16,silence);
	}
	/**
	 * 0.5sの無音区間を持つ16bitWavファイルに変換します。
	 * @param　sample_rate
	 * サンプリング時間
	 * @return
	 */
	public WaveFile toWaveFile(int sample_rate) {
		return this.toWaveFile(sample_rate,.5f);
	}
	
}