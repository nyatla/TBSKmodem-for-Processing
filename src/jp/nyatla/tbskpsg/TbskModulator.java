package jp.nyatla.tbskpsg;


import jp.nyatla.kokolink.protocol.tbsk.preamble.CoffPreamble;
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
	 * @deprecated
	 * トーンとプリアンブルを指定してインスタンスを構築します。
	 * @param parent
	 * アプレットのインスタンスです。
	 * @param tone
	 * トーン信号のインスタンスです。
	 * @param preamble
	 * プリアンブルのインスタンスです。プリアンブルはtoneから作られたものを推奨します。
	 */
	public TbskModulator(PApplet parent,TbskTone tone,TbskPreamble preamble)
	{
		this._parent = parent;
		this._mod=new jp.nyatla.tbskmodem.TbskModulator(tone.getBase(),preamble.getBase());
	}
	/**
	 * @see {@link TbskModulator#TbskModulator(PApplet, TbskTone, TbskPreamble)}
	 * @param parent
	 * @param tone
	 */
	public TbskModulator(PApplet parent)
	{
		this(parent,TbskTone.xpskSin(),CoffPreamble.DEFAULT_CYCLE);
	}
	/**
	 * @see {@link TbskModulator#TbskModulator(PApplet, TbskTone, TbskPreamble)}
	 * @param parent
	 * @param tone
	 */
	public TbskModulator(PApplet parent,TbskTone tone)
	{
		this(parent,tone,CoffPreamble.DEFAULT_CYCLE);
	}
	/**
	 * TBSK変調クラスのインスタンスを生成します。
	 * @param parent
	 * processingアプレットのインスタンス
	 * @param tone
	 * トーン信号のインスタンス
	 * @param preamble_cycle
	 * プリアンブルのシンボルサイクル数
	 */
	public TbskModulator(PApplet parent,TbskTone tone,int preamble_cycle)
	{
		this._parent = parent;
		this._mod=new jp.nyatla.tbskmodem.TbskModulator(tone.getBase(),preamble_cycle);
	}


	
	

	

	

	
	/**
	 * Modulate the int iterable to a Waveform.
	 * @param s
	 * The source data.
	 * @return
	 * General purpose Iterable.
	 */
	public ModulateIterable modulate(Iterable<Integer> s,boolean stopsymbol){
		return new ModulateIterable(this._parent,this._mod.modulate(s,8,stopsymbol));
	}
	public ModulateIterable modulate(Iterable<Integer> s){
		return this.modulate(s,true);
	}
	public ModulateIterable modulate(Integer[] s,boolean stopsymbol){
		return new ModulateIterable(this._parent,this._mod.modulate(Functions.toIntegerPyIterator(s),8,stopsymbol));
	}
	public ModulateIterable modulate(Integer[] s){
		return this.modulate(s,true);
	}
	public ModulateIterable modulate(Short[] s,boolean stopsymbol){
		return new ModulateIterable(this._parent,this._mod.modulate(Functions.toIntegerPyIterator(s),8,stopsymbol));
	}
	public ModulateIterable modulate(Short[] s){
		return this.modulate(s,true);
	}
	public ModulateIterable modulate(Byte[] s,boolean stopsymbol){
		return new ModulateIterable(this._parent,this._mod.modulate(Functions.toIntegerPyIterator(s),8,stopsymbol));
	}
	public ModulateIterable modulate(Byte[] s){
		return this.modulate(s,true);
	}
	public ModulateIterable modulate(int[] s,boolean stopsymbol){
		return new ModulateIterable(this._parent,this._mod.modulate(Functions.toIntegerPyIterator(s),8,stopsymbol));
	}
	public ModulateIterable modulate(int[] s){
		return this.modulate(s,true);
	}
	public ModulateIterable modulate(short[] s,boolean stopsymbol){
		return new ModulateIterable(this._parent,this._mod.modulate(Functions.toIntegerPyIterator(s),8,stopsymbol));
	}
	public ModulateIterable modulate(short[] s){
		return this.modulate(s,true);
	}
	public ModulateIterable modulate(byte[] s,boolean stopsymbol){
		return new ModulateIterable(this._parent,this._mod.modulate(Functions.toIntegerPyIterator(s),8,stopsymbol));
	}
	public ModulateIterable modulate(byte[] s){
		return this.modulate(s,true);
	}
	public ModulateIterable modulate(String s,boolean stopsymbol){
		try {
			return new ModulateIterable(this._parent,this._mod.modulate(s,stopsymbol));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	public ModulateIterable modulate(String s){
		return this.modulate(s,true);
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

