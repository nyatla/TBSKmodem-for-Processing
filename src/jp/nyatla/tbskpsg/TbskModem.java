package jp.nyatla.tbskpsg;




import java.io.IOException;
import java.io.UnsupportedEncodingException;

import jp.nyatla.tbskpsg.audioif.IAudioInterface;
import jp.nyatla.tbskpsg.audioif.IAudioPlayer;

import jp.nyatla.kokolink.compatibility;
import jp.nyatla.kokolink.utils.recoverable.RecoverableException;
import jp.nyatla.tbskmodem.TbskDemodulator;
import jp.nyatla.tbskmodem.TbskModulator;
import jp.nyatla.tbskpsg.result.ModulateIterable;
import jp.nyatla.tbskpsg.utils.WaveFile;
import jp.nyatla.tbskpsg.result.DemodulateAsStrIterable;
import jp.nyatla.tbskpsg.result.DemodulateIterable;
import processing.core.*;

/**
 * TbskModem for JavaのProcessingバインドです。
 * TbskModemを簡単に使うための関数を定義します。
 *
 * @example Hello 
 */

public class TbskModem
{
	private TbskModulator _mod;
	private TbskDemodulator _demod;
	
	// myParent is a reference to the parent sketch
	private PApplet _parent;

	int myVariable = 0;
	private IAudioInterface _aif;
	
	
	public final static TbskTone DEFAULT_TONE=TbskTone.xpskSin();
	

	public TbskModem(PApplet parent,TbskTone tone,TbskPreamble preamble,IAudioInterface aif)
	{
		this._parent = parent;
		this._aif=aif;
		this._mod=new TbskModulator(tone.getBase(),preamble.getBase());
		this._demod=new TbskDemodulator(tone.getBase(),preamble.getBase());
		welcome();
	}
	public TbskModem(PApplet parent,TbskTone tone,TbskPreamble preamble)
	{
		this(parent,tone,preamble,null);
	}
	public TbskModem(PApplet parent)
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
	


	
	public DemodulateAsStrIterable demodulateAsStr(Iterable<Double> s){
		try {
			return new DemodulateAsStrIterable(this._parent,this._demod.demodulateAsStr(s));
		} catch (RecoverableException e) {
			return null;
		}
	}
	public DemodulateAsStrIterable demodulateAsStr(Double[] s) {
		try {
			return new DemodulateAsStrIterable(this._parent,this._demod.demodulateAsStr(compatibility.toDoublePyIterator(s),"utf-8"));
		} catch (RecoverableException e) {
			return null;
		}
	}
	public DemodulateAsStrIterable demodulateAsStr(double[] s) {
		try {
			return new DemodulateAsStrIterable(this._parent,this._demod.demodulateAsStr(compatibility.toDoublePyIterator(s),"utf-8"));
		} catch (RecoverableException e) {
			return null;
		}		
	}
	public DemodulateAsStrIterable demodulateAsStr(float[] s) {
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
	
	


	
	private IAudioPlayer _async_player=null;
	/**
	 * トランザクションが送信可能かを返す。
	 * @return
	 */
	public boolean txReady() {
		try {
			return this._async_player==null || this._async_player.waitForFinished(0);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 進行中のトランザクションを停止する。
	 * @return
	 */
	public void txStop() {
		try {
			if(this._async_player!=null && !this._async_player.waitForFinished(0)) {
				this._async_player.close();
				this._async_player=null;
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * トランザクションを送信する。
	 * @param s
	 * @param async
	 */
	public void tx(ModulateIterable s,boolean async)
	{
		if(this._aif==null) {
			throw new RuntimeException("This instance has not Audio interface");
		}
		//有効な非同期プレイヤーが動作中なら再生が完了するまで待つ。
		IAudioPlayer async_player=this._async_player;
		if(async_player!=null) {
			boolean wr;
			try {
				wr=async_player.waitForFinished(-1);
			} catch (InterruptedException e1) {
				throw new RuntimeException(e1);
			}
			if(wr) {
				try {
					async_player.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}finally {
					this._async_player=null;
				}
			}else {
				throw new RuntimeException("Asynchronous send is in use. Wait until the txReady function becomes true.");
			}			
		}
		assert(this._async_player==null);

		//新規再生
		IAudioPlayer player=this._aif.createPlayer(s);
		if(player==null) {
			throw new RuntimeException("This instance has not Player interface");
		}
		player.play();
		if(!async) {
			//同期送信ならブロックする
			try {
				player.waitForFinished(-1);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}finally {
				try {
					player.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}else {
			this._async_player=player;			
		}
	}
	public void tx(Iterable<Integer> s,boolean async) {
		this.tx(this.modulate(s), async);
	}
	public void tx(Integer[] s,boolean async) {
		this.tx(this.modulate(s), async);
	}
	public void tx(Short[] s,boolean async) {
		this.tx(this.modulate(s), async);
	}
	public void tx(Byte[] s,boolean async) {
		this.tx(this.modulate(s), async);
	}
	public void tx(int[] s,boolean async) {
		this.tx(this.modulate(s), async);
	}
	public void tx(short[] s,boolean async) {
		this.tx(this.modulate(s), async);
	}
	public void tx(byte[] s,boolean async) {
		this.tx(this.modulate(s), async);
	}
	public void tx(String s,boolean async) {
		this.tx(this.modulate(s), async);
	}

	
	public void tx(Iterable<Integer> s) {
		this.tx(s,true);
	}
	public void tx(Integer[] s) {
		this.tx(s,true);
	}
	public void tx(Short[] s) {
		this.tx(s,true);
	}
	public void tx(Byte[] s) {
		this.tx(s,true);
	}
	public void tx(int[] s) {
		this.tx(s,true);
	}
	public void tx(short[] s) {
		this.tx(s,true);
	}
	public void tx(byte[] s) {
		this.tx(s,true);
	}
	public void tx(String s) {
		this.tx(s,true);
	}



	/**
	 * return the version of the Library.
	 * 
	 * @return String
	 */
	public static String version() {
		return Version.STRING;
	}



}

