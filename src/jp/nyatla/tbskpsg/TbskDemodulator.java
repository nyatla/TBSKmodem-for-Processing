package jp.nyatla.tbskpsg;

import jp.nyatla.kokolink.compatibility;
import jp.nyatla.kokolink.utils.recoverable.RecoverableException;
import jp.nyatla.tbskpsg.utils.WaveFile;
import jp.nyatla.tbskpsg.result.DemodulateAsStrIterable;
import jp.nyatla.tbskpsg.result.DemodulateIterable;
import processing.core.*;

/**
 * TbskModem for JavaのProcessingバインドです。
 * TBSK信号を配列や文字列へ復調します。
 */

public class TbskDemodulator
{
	private jp.nyatla.tbskmodem.TbskDemodulator _demod;
	
	// myParent is a reference to the parent sketch
	private PApplet _parent;




	
	
	public final static TbskTone DEFAULT_TONE=TbskTone.xpskSin();
	

	public TbskDemodulator(PApplet parent,TbskTone tone,TbskPreamble preamble)
	{
		this._parent = parent;
		this._demod=new jp.nyatla.tbskmodem.TbskDemodulator(tone.getBase(),preamble.getBase());
		welcome();
	}
	public TbskDemodulator(PApplet parent)
	{
		this(parent,TbskTone.xpskSin(),TbskPreamble.coff(TbskTone.xpskSin()));
	}	
	
	private void welcome() {
		System.out.println(Version.STRING+" by Ryo Iizuka");
	}
	
	



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
			return new DemodulateIterable(this._parent,this._demod.demodulateAsInt(compatibility.toDoublePyIterator(s)));
		} catch (RecoverableException e) {
			return null;
		}
	}
	public DemodulateIterable demodulate(double[] s)
	{
		try {
			return new DemodulateIterable(this._parent,this._demod.demodulateAsInt(compatibility.toDoublePyIterator(s)));
		} catch (RecoverableException e) {
			return null;
		}
	}
	public DemodulateIterable demodulate(float[] s)
	{
		try {
			return new DemodulateIterable(this._parent,this._demod.demodulateAsInt(compatibility.toDoublePyIterator(s)));
		} catch (RecoverableException e) {
			return null;
		}
	}	
	public DemodulateIterable demodulate(WaveFile s)
	{
		return this.demodulate(s.getFrame());
	}
	


	
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
			return new DemodulateAsStrIterable(this._parent,this._demod.demodulateAsStr(compatibility.toDoublePyIterator(s),"utf-8"));
		} catch (RecoverableException e) {
			return null;
		}
	}
	public DemodulateAsStrIterable demodulateAsStr(double[] s)
	{
		try {
			return new DemodulateAsStrIterable(this._parent,this._demod.demodulateAsStr(compatibility.toDoublePyIterator(s),"utf-8"));
		} catch (RecoverableException e) {
			return null;
		}		
	}
	public DemodulateAsStrIterable demodulateAsStr(float[] s)
	{
		try {
			return new DemodulateAsStrIterable(this._parent,this._demod.demodulateAsStr(compatibility.toDoublePyIterator(s),"utf-8"));
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

