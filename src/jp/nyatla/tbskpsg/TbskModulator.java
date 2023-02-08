package jp.nyatla.tbskpsg;



import java.io.UnsupportedEncodingException;
import jp.nyatla.kokolink.compatibility.Functions;
import jp.nyatla.tbskpsg.result.ModulateIterable;
import processing.core.*;

/**
 * Modulator of the waveform to data.
 * <br/><br>
 * TbskModem for JavaのProcessingバインドです。
 * 配列や文字列をTBSK信号へ復調します。
 */
public class TbskModulator
{
	private jp.nyatla.tbskmodem.TbskModulator _mod;
	
	// myParent is a reference to the parent sketch
	private PApplet _parent;


	/**
	 * Create instance with specified tone and preamble.
	 * @param parent
	 * PApplet instance.
	 * @param tone
	 * Tone symbol for TBSK modulation.
	 * @param preamble
	 * Preamble format for TBSK modulation.
	 */
	public TbskModulator(PApplet parent,TbskTone tone,TbskPreamble preamble)
	{
		this._parent = parent;
		this._mod=new jp.nyatla.tbskmodem.TbskModulator(tone.getBase(),preamble.getBase());
	}
	/**
	 * Same as TbskModulator(parent,TbskTone.xpskSin(),TbskPreamble.coff(TbskTone.xpskSin());
	 * @param parent
	 * PApplet instance.
	 */		
	public TbskModulator(PApplet parent)
	{
		this(parent,TbskTone.xpskSin(),TbskPreamble.coff(TbskTone.xpskSin()));
	}	


	
	

	

	

	
	/**
	 * Modulate the int iterable to a Waveform.
	 * @param s
	 * The source data.
	 * @return
	 * General purpose Iterable.
	 */
	public ModulateIterable modulate(Iterable<Integer> s){
		return new ModulateIterable(this._parent,this._mod.modulate(s,8));
	}
	public ModulateIterable modulate(Integer[] s){
		return new ModulateIterable(this._parent,this._mod.modulate(Functions.toIntegerPyIterator(s),8));
	}
	public ModulateIterable modulate(Short[] s){
		return new ModulateIterable(this._parent,this._mod.modulate(Functions.toIntegerPyIterator(s),8));
	}
	public ModulateIterable modulate(Byte[] s){
		return new ModulateIterable(this._parent,this._mod.modulate(Functions.toIntegerPyIterator(s),8));
	}
	public ModulateIterable modulate(int[] s){
		return new ModulateIterable(this._parent,this._mod.modulate(Functions.toIntegerPyIterator(s),8));
	}
	public ModulateIterable modulate(short[] s){
		return new ModulateIterable(this._parent,this._mod.modulate(Functions.toIntegerPyIterator(s),8));
	}
	public ModulateIterable modulate(byte[] s){
		return new ModulateIterable(this._parent,this._mod.modulate(Functions.toIntegerPyIterator(s),8));
	}
	public ModulateIterable modulate(String s){
		try {
			return new ModulateIterable(this._parent,this._mod.modulate(s));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	

	/**
	 * return the version of the Library.
	 * 
	 * @return String
	 */
	public static String version() {
		return Version.STRING;
	}	
	class Rxtask{
		
	}


}

