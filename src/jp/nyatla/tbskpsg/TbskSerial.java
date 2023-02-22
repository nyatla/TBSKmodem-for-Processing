package jp.nyatla.tbskpsg;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;


import jp.nyatla.tbskpsg.audioif.IAudioInterface;
import processing.core.PApplet;
import jp.nyatla.kokolink.protocol.tbsk.preamble.CoffPreamble;

/**
 * ProcessingのSerialと同じ型式の関数を持つTBSKmodemのラッパーです。
 * 以下の関数は使用できません。
 * readStringUntil,serialEvent,readBytesUntil,buffer,bufferUntil
 *
 */
public class TbskSerial {
	final private TbskModem _modem;
	public TbskSerial(PApplet parent,IAudioInterface aif) {
		this(parent,TbskTone.xpskSin(),aif);
	}
	public TbskSerial(PApplet parent,TbskTone tone,IAudioInterface aif)
	{
		this(parent,tone,(float)CoffPreamble.DEFAULT_TH,CoffPreamble.DEFAULT_CYCLE,aif);
	}
	/**
	 * インスタンスのコンストラクタです。
	 * modemの機能は生成と同時に利用可能になります。
	 * @param parent
	 * processingアプレットのインスタンス
	 * @param tone
	 * トーン信号のインスタンス
	 * @param preamble_th
	 * 信号の検出閾値
	 * @param preamble_cycle
	 * プリアンブルのシンボルサイクル数	 * 
	 */
	public TbskSerial(PApplet parent,TbskTone tone,float preamble_th,int preamble_cycle,IAudioInterface aif)
	{	
		this._modem=new TbskModem(parent,tone, preamble_th, preamble_cycle,aif);
		this._modem.start();
	}	
	/**
	 * @deprecated
	 * Create modem instance attached Audio interface.
	 * @param parent
	 * PApplet instance.
	 * @param tone
	 * Tone symbol for TBSK modulation.
	 * @param preamble
	 * Preamble format for TBSK modulation.
	 * @param aif
	 * Aufio Interface.
	 */
	public TbskSerial(PApplet parent,TbskTone tone,TbskPreamble preamble,IAudioInterface aif) {
		this._modem=new TbskModem(parent,tone,preamble,aif);
		this._modem.start();
	}
	
	
	
	
	/**
	 * Danger ZONE!!
	 * Low level modem interface.
	 * @return
	 */
	public TbskModem internalModem() {
		return this._modem;
	}
	/**
	 * Returns the number of bytes available
	 * @return
	 */
	public int available() {
		return this._modem.rxTotalSize();
	}

	//buffer()	Sets the number of bytes to buffer before calling serialEvent()
	//bufferUntil()	Sets a specific byte to buffer until before calling serialEvent()
	/**
	 * Empty the buffer, removes all the data stored there
	 */
	public void clear() {
		this._modem.rxClear();
	}
	/**
	 * Returns last byte received or -1 if there is none available
	 * @return
	 */
	public int last() {
		int l=this._modem.rxTotalSize();
		if(l==0) {
			return -1;
		}
		for(int i=0;i<l-1;i++) {
			this._modem.rx();
		}
		return this._modem.rx();
	}
	/**
	 * Returns the last byte received as a char or Character.MAX_SURROGATE if there is none available
	 * @return
	 */
	public char lastChar() {
		Character c=null;
		while(this._modem.rxAsCharReady()) {
			c=this._modem.rxAsChar();
		}
		return c==null?Character.MAX_SURROGATE:c;
	}
	/**
	 * Gets a list of all available serial ports
	 * @return
	 */
	public String[] list() {
		String s=String.format("%s;%dHz %dbps",Version.STRING,this._modem.sampleRate(),this._modem.baud());
		return new String[] {s};
	}
	/**
	 * Returns a number between 0 and 255 for the next byte that's waiting in the buffer
	 * @return
	 */
	public int read() {
		if(this._modem.rxReady()) {
			return this._modem.rx();
		}else {
			return -1;
		}		
	}
	

	/**
	 * Reads a group of bytes from the buffer or null if there are none available
	 * @return
	 */
	public byte[] readBytes() {
		return this.readBytes(Integer.MAX_VALUE);
	}
	public byte[] readBytes(int max) {
		int l=this._modem.rxTotalSize();
		if(l<1) {
			return null;
		}
		byte[] r=new byte[this._modem.rxTotalSize()];
		int c=Math.max(r.length,max);
		for(int i=0;i<c;i++) {
			r[i]=(byte)this._modem.rx();
		}
		return r;
	}
	public int readBytes(byte[] dest)
	{
		int l=this._modem.rxTotalSize();
		if(l<1) {
			return 0;
		}
		int c=Math.max(l,dest.length);
		for(int i=0;i<c;i++) {
			dest[i]=(byte)this._modem.rx();
		}
		return c;
		
	}

	//readBytesUntil()	Reads from the port into a buffer of bytes up to and including a particular character

	/**
	 * Returns the next byte in the buffer as a char
	 * @return
	 */
	public Character readChar() {
		if(this._modem.rxAsCharReady()) {
			return this._modem.rxAsChar();
		}else {
			return Character.MAX_SURROGATE;
		}		
	}
	/**
	 * Returns all the data from the buffer as a String or null if there is nothing available
	 * @return
	 */
	public String readString() {
		String r="";
		while(this._modem.rxAsCharReady()) {
			r=r+this._modem.rxAsChar();
		}
		return r.length()>0?r:null;		
	}
	
	

//	readStringUntil()	Combination of readBytesUntil() and readString()
//	serialEvent()	Called when data is available
	
	/**
	 * Stops data communication on this port
	 */
	public void stop() {
		this._modem.stop();
	}
	/**
	 * Writes bytes, chars, ints, bytes[], Strings to the serial port	
	 */
	public void write(byte[] s) {
		this._modem.tx(s);		
	}
	public void write(char[] s)
	{
		CharsetEncoder enc=Charset.forName("utf-8").newEncoder();
		CharBuffer c=CharBuffer.wrap(s);
		ByteBuffer d;
		try {
			d = enc.encode(c);
		} catch (CharacterCodingException e) {
			throw new RuntimeException(e);
		}
		this._modem.tx(d.array());
	}
	public void write(int[] s) {
		this._modem.tx(s);
	}
	public void write(String s) {
		this._modem.tx(s);
	}
	public void write(char s) {
		this._modem.tx(new char[] {s});	
	}
	public void write(int s) {
		this._modem.tx(new int[] {s});
	}
	public void write(byte s) {
		this._modem.tx(new byte[] {s});		
	}

}
