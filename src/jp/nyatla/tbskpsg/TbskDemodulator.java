package jp.nyatla.tbskpsg;

import jp.nyatla.kokolink.compatibility.Functions;
import jp.nyatla.kokolink.utils.recoverable.RecoverableException;
import jp.nyatla.tbskpsg.utils.WaveFile;
import jp.nyatla.tbskpsg.result.DemodulateAsStrIterable;
import jp.nyatla.tbskpsg.result.DemodulateIterable;
import processing.core.*;

/**
 * Demodulator of the waveform to data.
 * <br/><br>
 * TbskModem for JavaのProcessingバインドです。
 * TBSK信号を配列や文字列へ復調します。
 */
public class TbskDemodulator
{
	private jp.nyatla.tbskmodem.TbskDemodulator _demod;
	
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
	public TbskDemodulator(PApplet parent,TbskTone tone,TbskPreamble preamble)
	{
		this._parent = parent;
		this._demod=new jp.nyatla.tbskmodem.TbskDemodulator(tone.getBase(),preamble.getBase());
	}
	/**
	 * Same as TbskDemodulator(parent,TbskTone.xpskSin(),TbskPreamble.coff(TbskTone.xpskSin());
	 * @param parent
	 * PApplet instance.
	 */	
	public TbskDemodulator(PApplet parent)
	{
		this(parent,TbskTone.xpskSin(),TbskPreamble.coff(TbskTone.xpskSin()));
	}	
	
	
	


	/**
	 * Demodulate the waveform to an Iterable object of TBSK signals.
	 * This demodulator only returns the first detected signal.
	 * @param s
	 * The waveform data.
	 * @return
	 * General purpose Iterable.
	 */
	public DemodulateIterable demodulate(Iterable<Double> s)
	{	
		try {
			return new DemodulateIterable(this._parent,this._demod.demodulateAsInt(s));
		} catch (RecoverableException e) {
			return null;
		}
	}
	public DemodulateIterable demodulate(Double[] s)
	{
		try {
			return new DemodulateIterable(this._parent,this._demod.demodulateAsInt(Functions.toDoublePyIterator(s)));
		} catch (RecoverableException e) {
			return null;
		}
	}
	public DemodulateIterable demodulate(double[] s)
	{
		try {
			return new DemodulateIterable(this._parent,this._demod.demodulateAsInt(Functions.toDoublePyIterator(s)));
		} catch (RecoverableException e) {
			return null;
		}
	}
	public DemodulateIterable demodulate(float[] s)
	{
		try {
			return new DemodulateIterable(this._parent,this._demod.demodulateAsInt(Functions.toDoublePyIterator(s)));
		} catch (RecoverableException e) {
			return null;
		}
	}	
	public DemodulateIterable demodulate(WaveFile s)
	{
		return this.demodulate(s.getFrame());
	}
	


	/**
	 * Demodulate the waveform to a string Iterable object of TBSK signals.
	 * @param s
	 * The waveform data.
	 * @return
	 * General purpose string Iterable.
	 */	
	public DemodulateAsStrIterable demodulateAsStr(Iterable<Double> s)
	{
		try {
			return new DemodulateAsStrIterable(this._parent,this._demod.demodulateAsStr(s));
		} catch (RecoverableException e) {
			return null;
		}
	}
	public DemodulateAsStrIterable demodulateAsStr(Double[] s)
	{
		try {
			return new DemodulateAsStrIterable(this._parent,this._demod.demodulateAsStr(Functions.toDoublePyIterator(s),"utf-8"));
		} catch (RecoverableException e) {
			return null;
		}
	}
	public DemodulateAsStrIterable demodulateAsStr(double[] s)
	{
		try {
			return new DemodulateAsStrIterable(this._parent,this._demod.demodulateAsStr(Functions.toDoublePyIterator(s),"utf-8"));
		} catch (RecoverableException e) {
			return null;
		}		
	}
	public DemodulateAsStrIterable demodulateAsStr(float[] s)
	{
		try {
			return new DemodulateAsStrIterable(this._parent,this._demod.demodulateAsStr(Functions.toDoublePyIterator(s),"utf-8"));
		} catch (RecoverableException e) {
			return null;
		}		
	}

	public DemodulateAsStrIterable demodulateAsStr(WaveFile s)
	{
		return this.demodulateAsStr(s.getFrame());
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

