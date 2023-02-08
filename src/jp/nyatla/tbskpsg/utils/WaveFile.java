package jp.nyatla.tbskpsg.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import jp.nyatla.kokolink.compatibility.Functions;
import jp.nyatla.kokolink.types.Py_interface__.IPyIterator;
import jp.nyatla.kokolink.utils.wavefile.PcmData;
import processing.core.PApplet;

/**
 * 標準waveフォーマットファイルの格納クラス。
 * このクラスは、1chのwaveファイルのみに対応します。
 * <br></br>
 * Standard Wav File wrapper class.
 * 
 * @author nyatla
 *
 */
public class WaveFile
{
	final private PApplet _parent;
	private PcmData _pcmdata;
	public WaveFile load(InputStream s)
	{
		try {
			this._pcmdata=PcmData.load(s);
			return this;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	public WaveFile load(String path){
        try (InputStream input= this._parent.createInput(path)){
			this.load(input);
			return this;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	public WaveFile save(String path){
        try (OutputStream output= this._parent.createOutput(path)) {
        	this.save(output);
    		return this;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	public WaveFile save(OutputStream d)
	{
		try {
			PcmData.dump(this._pcmdata,d);
			return this;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	public WaveFile(PApplet parent,double[] frames,int sample_rate,int sample_bits)
	{		
		this(parent,Functions.toDoublePyIterator(frames),sample_rate,sample_bits);
	}
	public WaveFile(PApplet parent,float[] frames,int sample_rate)
	{		
		this(parent,Functions.toDoublePyIterator(frames),sample_rate,16);
	}

	public WaveFile(PApplet parent,float[] frames,int sample_rate,int sample_bits)
	{		
		this(parent,Functions.toDoublePyIterator(frames),sample_rate,sample_bits);
	}
	public WaveFile(PApplet parent,double[] frames,int sample_rate)
	{		
		this(parent,Functions.toDoublePyIterator(frames),sample_rate,16);
	}
	
	public WaveFile(PApplet parent,Iterable<Float> frames,int sample_rate,int sample_bits)
	{		
		this(parent,Functions.toDoublePyIterator(frames),sample_rate,sample_bits);
	}
	private WaveFile(PApplet parent,IPyIterator<Double> frames,int sample_rate,int sample_bits)
	{		
		this(parent,new PcmData(frames,sample_bits,sample_rate));
	}
	
	public WaveFile(PApplet parent)
	{		
		this(parent,null);
	}

	private WaveFile(PApplet parent,PcmData s) {
		this._parent=parent;
		this._pcmdata=s;	
	}
	public int getSampleRate() {
		if(this._pcmdata==null) {
			throw new RuntimeException("Empty data.");
		}
		return this._pcmdata.getFramerate();
	}
	public int getSampleBits() {
		if(this._pcmdata==null) {
			throw new RuntimeException("Empty data.");
		}
		return this._pcmdata.getSampleBits();
	}
	public float[] getFrame() {
		if(this._pcmdata==null) {
			throw new RuntimeException("Empty data.");
		}
		List<Double> d=this._pcmdata.getDataAsDouble();
		float[] r=new float[d.size()];
		for(int i=0;i<d.size();i++) {
			r[i]=d.get(i).floatValue();
		}
		return r;
	}
}
