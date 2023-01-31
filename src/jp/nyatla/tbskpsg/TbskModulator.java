package jp.nyatla.tbskpsg;



import java.io.UnsupportedEncodingException;
import jp.nyatla.kokolink.compatibility;
import jp.nyatla.tbskpsg.result.ModulateIterable;
import processing.core.*;

/**
 * TbskModem for JavaのProcessingバインドです。
 * 配列や文字列をTBSK信号に変調します。
 */

public class TbskModulator
{
	private jp.nyatla.tbskmodem.TbskModulator _mod;
	
	// myParent is a reference to the parent sketch
	private PApplet _parent;


	
	public final static TbskTone DEFAULT_TONE=TbskTone.xpskSin();
	

	public TbskModulator(PApplet parent,TbskTone tone,TbskPreamble preamble)
	{
		this._parent = parent;
		this._mod=new jp.nyatla.tbskmodem.TbskModulator(tone.getBase(),preamble.getBase());
		welcome();
	}
	public TbskModulator(PApplet parent)
	{
		this(parent,TbskTone.xpskSin(),TbskPreamble.coff(TbskTone.xpskSin()));
	}	
	
	private void welcome() {
		System.out.println(Version.STRING+" by Ryo Iizuka");
	}
	
	

	

	

	
	/**
	 * Modulate the sequence with TBSK.
	 * @param s
	 * 
	 * @return
	 */
	public ModulateIterable modulate(Iterable<Integer> s){
		return new ModulateIterable(this._parent,this._mod.modulate(s,8));
	}
	public ModulateIterable modulate(Integer[] s){
		return new ModulateIterable(this._parent,this._mod.modulate(compatibility.toIntegerPyIterator(s),8));
	}
	public ModulateIterable modulate(Short[] s){
		return new ModulateIterable(this._parent,this._mod.modulate(compatibility.toIntegerPyIterator(s),8));
	}
	public ModulateIterable modulate(Byte[] s){
		return new ModulateIterable(this._parent,this._mod.modulate(compatibility.toIntegerPyIterator(s),8));
	}
	public ModulateIterable modulate(int[] s){
		return new ModulateIterable(this._parent,this._mod.modulate(compatibility.toIntegerPyIterator(s),8));
	}
	public ModulateIterable modulate(short[] s){
		return new ModulateIterable(this._parent,this._mod.modulate(compatibility.toIntegerPyIterator(s),8));
	}
	public ModulateIterable modulate(byte[] s){
		return new ModulateIterable(this._parent,this._mod.modulate(compatibility.toIntegerPyIterator(s),8));
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

