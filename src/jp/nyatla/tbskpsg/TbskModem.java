package jp.nyatla.tbskpsg;




import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import jp.nyatla.tbskpsg.audioif.IAudioInputIterator;
import jp.nyatla.tbskpsg.audioif.IAudioInterface;
import jp.nyatla.tbskpsg.audioif.IAudioPlayer;

import jp.nyatla.kokolink.compatibility;
import jp.nyatla.kokolink.utils.BrokenTextStreamDecoder;
import jp.nyatla.kokolink.utils.recoverable.RecoverableException;
import jp.nyatla.tbskmodem.TbskDemodulator;
import jp.nyatla.tbskmodem.TbskDemodulator.DemodulateAsIntAS;
import jp.nyatla.tbskmodem.TbskModulator;
import jp.nyatla.tbskpsg.result.ModulateIterable;
import processing.core.*;

/**
 * TbskModem for JavaのProcessingバインドです。
 * TbskModemを簡単に使うための関数を定義します。
 *
 */

public class TbskModem
{
	public static boolean _DEBUG=false;
	static public void debug(String messgae) {
		if(TbskModem._DEBUG) {
			System.out.println(messgae);
		}
	}
	
	private TbskModulator _mod;
	private TbskDemodulator _demod;
	
	// myParent is a reference to the parent sketch
	private PApplet _parent;


	private IAudioInterface _aif;
	private RxTask _rxtask;
	
	
	public final static TbskTone DEFAULT_TONE=TbskTone.xpskSin();
	

	public TbskModem(PApplet parent,TbskTone tone,TbskPreamble preamble,IAudioInterface aif)
	{
		assert(aif!=null);
		this._parent = parent;
		this._mod=new TbskModulator(tone.getBase(),preamble.getBase());
		this._demod=new TbskDemodulator(tone.getBase(),preamble.getBase());
		this._rxtask=null;
		this._aif=aif;

		try
		{
			parent.registerMethod("dispose", this);
		}
		catch ( SecurityException e )
		{
			e.printStackTrace();
		}
		catch ( IllegalArgumentException e )
		{
			e.printStackTrace();
		}
		welcome();
	}
	public TbskModem(PApplet parent,IAudioInterface aif)
	{
		this(parent,TbskTone.xpskSin(),TbskPreamble.coff(TbskTone.xpskSin()),aif);
	}
	
	private void welcome() {
		System.out.println(Version.STRING+" by Ryo Iizuka");
	}
	public void dispose() {
		TbskModem.debug("Enter dispose sequence.");
		this.stop();
	}
	

	public void start()
	{
		if(this._rxtask!=null) {
			throw new RuntimeException("RxTask is already Running.");
		}
		IAudioInputIterator input=this._aif.createInputIterator();
		if(input==null) {
			throw new RuntimeException("This instance has not Audio input iterator.");
		}
		RxTask task=new RxTask(this._demod,input);
		task.start();
		this._rxtask=task;
	}
	public void stop()
	{
		if(this._rxtask==null) {
			TbskModem.debug("RxTask is already Stopped.");
			return;
		}
		this._rxtask.dispose();
		TbskModem.debug("RxTask disposed.");
		this._rxtask=null;
	}
	/**
	 * 1/100サンプルの平均RMS
	 */
	public float rms() {
		return (float)this._rxtask._input.getRMS();
	}
	/**
	 * オーディオシステムが受信したサンプル数
	 * @return
	 */
	public long acceptedSampleCount(){
		return this._rxtask._input.acceptedSamples();
	}

	

	

 	private class RxBuffer
	{
		/**
		 * パケットごとにInt値を格納するのバッファ
		 */
		public class RxData
		{
			private Deque<Integer> _q=new ArrayDeque<Integer>();
			private BrokenTextStreamDecoder _decoder=new BrokenTextStreamDecoder("utf-8");
			private final int _number;
			private boolean _is_stop;
			public RxData(int number)
			{
				this._number=number;
				this._is_stop=false;
			}
			/**
			 * 通し版を返す。
			 * @return
			 */
			public int getNumber() {
				return this._number;
			}
			synchronized public boolean add(Integer v) {
				return this._q.add(v);
			}		
			synchronized void stop() {
				this._is_stop=true;
			}
			synchronized boolean readyInt()
			{
				return this._q.size()>0 || this._decoder.holdLen()>0;
			}
			synchronized int getInt()
			{
				assert(this.readyInt());
				if(this._decoder.holdLen()>0) {
					int r=this._decoder.peekFront();
					this._decoder.shift(1);
					return (int)(0xff & r);
				}else {
					Integer r=this._q.poll();
					assert(r!=null);
					return r;
				}
			}
			synchronized boolean readyChar()
			{
				//関数は、dec.updateが動作するときだけtrueを返す。
				BrokenTextStreamDecoder dec=this._decoder;				
				if(!this.available()) {
					//終端確定の場合はupdateで処理するから解析キューに文字があればtrue
					return dec.holdLen()>0;
				}else {
					//現在のバッファでCharが構成できる場合
					int l=dec.test();
					if(l>0) {
						return true;
					}
					//バッファが追加出来なくなるまでCharが構成できるか確認
					while(!dec.isBufferFull()) {
						if(this.readyInt()) {
							l=dec.test((byte)this.getInt());
							if(l>0) {
								return true;
							}
						}else {
							break;
						}
					}
					//バッファフルなら次のupdateは成功する
					if(dec.isBufferFull()) {
						return true;
					}
				}
				return false;
			}
			synchronized char getChar()
			{
				assert(this.readyChar());
				BrokenTextStreamDecoder dec=this._decoder;				
				Character c=dec.update();
				assert(c!=null);
				return c;
			}			
			/**
			 * このデータブロックが利用できなければtrue
			 * @return
			 */
			synchronized boolean available() {
				return !(this._is_stop && this._q.isEmpty() && this._decoder.holdLen()==0);
			}	
		}

		private List<RxData> _buf=new ArrayList<RxData>();

		/**
		 * 新しくパケットを登録する。
		 * @param number
		 * @return
		 */
		public RxData enter(int number)
		{
			List<RxData> buf=this._buf;
			RxData n=new RxData(number);
			buf.add(n);
			//ひとつ前のキューを閉じる
			int prev=buf.size()-2;
			if(prev>=0) {
				buf.get(prev).stop();
			}
			return n;
		}
		/**
		 * パケットに新しいデータを足す
		 * @param v
		 */
		public void push(int v) {
			List<RxData> buf=this._buf;
			RxData d=buf.get(buf.size()-1);
			d.add(v);
		}
		/**
		 * getが実行可能状態であるかを返す。
		 * @return
		 */
		public boolean ready()
		{
			//trueを返した場合、パケットキューの先頭からreadIntができること。
			List<RxData> buf=this._buf;
			if(buf.size()<=0){
				//ブロックがなければfalse
				return false;
			}
			//先頭からブロックを走査
			while(buf.size()>0) {
				RxData d=buf.get(0);
				if(d.readyInt()) {
					//先頭ブロックにデータがあればtrue
					return true;
				}
				if(d.available()) {
					//先頭ブロックが有効ならfalse
					return false;
				}else {
					//先頭ブロック無効かつサイズ0なら削除してリトライ
					buf.remove(0);
				}
			}
			return false;
		}
		public boolean readyChar()
		{
			//trueを返した場合、パケットキューの先頭からreadCharができること。
			List<RxData> buf=this._buf;
			if(buf.size()<=0){
				//ブロックがなければfalse
				return false;
			}
			//先頭からブロックを走査
			while(buf.size()>0) {
				RxData d=buf.get(0);
				if(d.readyChar()) {
					//先頭ブロックにデータがあればtrue
					return true;
				}
				if(d.available()) {
					//先頭ブロックが有効ならfalse
					return false;
				}else {
					//先頭ブロック無効かつサイズ0なら削除してリトライ
					buf.remove(0);
				}
			}
			return false;
		}
		/**
		 * 1バイトのデータを返す
		 * @return
		 */
		public int get(){
			if(!this.ready()) {
				throw new RuntimeException();
			}
			//戻り値を先頭ブロックの先頭から取得
			RxData current = this._buf.get(0);
			int r=current.getInt();
			//データが無効になってたら削除
			if(!current.available()) {
				this._buf.remove(0);
			}
			return r;
		}
		public char getChar(){
			if(!this.ready()) {
				throw new RuntimeException();
			}
			//戻り値を先頭ブロックの先頭から取得
			RxData current = this._buf.get(0);
			char r=current.getChar();
			//データが無効になってたら削除
			if(!current.available()) {
				this._buf.remove(0);
			}
			return r;
		}		
	}


	
	private class RxTask extends Thread{
		private TbskDemodulator _demod;
		private RxBuffer _rxb;
		private IAudioInputIterator _input;
		public RxTask(TbskDemodulator demod,IAudioInputIterator input) {
			this._demod=demod;
			this._input=input;
			this._rxb=new RxBuffer();
		}
		public void run()
		{
			TbskModem.debug("start RxTask");
			int number=0;
			for(;;) {
				//bit iterableを起動	
				TbskModem.debug("Preamble detection");
				Iterable<Integer> iter;
				try {
					iter = this._demod.demodulateAsInt(this._input);
				} catch (RecoverableException e1) {
					DemodulateAsIntAS recover=e1.detach();
					for(;;) {
						if(!recover.run()) {
							continue;
						}
						iter=recover.getResult();
						break;
					}
				}
				if(iter==null) {
					//終端まで信号が発見できなかった。マジか。（まじか）
					TbskModem.debug("RXTask closing.");
					return;
				}
				TbskModem.debug("Payload detection");
				RxBuffer.RxData rxd;
				synchronized(this) {
					rxd=this._rxb.enter(number++);
				}
				for(Integer i:iter) {	//このイテレータはInterruptでstopiterationを出す。
					rxd.add(i);			//データ追記
				}
				rxd.stop();//停止
			}
		}
		synchronized public boolean ready() {
			return this._rxb.ready();
		}
		synchronized public int read() {
			if(!this._rxb.ready()) {
				throw new RuntimeException("Not ready(Q).");
			}
			return this._rxb.get();
		}
		synchronized public boolean readyChar() {
			return this._rxb.readyChar();
		}
		synchronized public char readChar() {
			if(!this._rxb.readyChar()) {
				throw new RuntimeException("Not ready(Q).");
			}
			return this._rxb.getChar();
		}		
		synchronized public void dispose() {
			this.interrupt();//ここでキューが飛んでrunの待機関数がStopiterationで停止する。
			try {
				this.join();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}finally {
				//nextが飛んでるのでcloseが成功するはず。
				try {
					this._input.close();
					this._input=null;
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}
	
	public boolean rxReady() {
		return this._rxtask.ready();
	}
	public int rx() {
		return this._rxtask.read();		
	}
	public boolean rxReadyAsChar() {
		return this._rxtask.readyChar();
	}
	public char rxAsChar() {
		return this._rxtask.readChar();		
	}
	


	
	
	private IAudioPlayer _async_player=null;
	/**
	 * Return tx enable.
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
	 * Break current processing send signal.
	 * @return
	 */
	public void txBreak() {
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
	synchronized public void tx(ModulateIterable s,boolean async)
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
	
	/**
	 * Modulate the sequence with TBSK.
	 * This function can only be used when {@link #txReady()} is True.
	 * @param s
	 * The data is modulated into one signal.
	 * @param async
	 * Asynchronous flag. If true, the signal will be sent asynchronously.
	 * @return
	 */

	public void tx(Iterable<Integer> s,boolean async) {
		this.tx(new ModulateIterable(this._parent,this._mod.modulate(s,8)), async);
	}
	public void tx(Integer[] s,boolean async) {
		this.tx(new ModulateIterable(this._parent,this._mod.modulate(compatibility.toIntegerPyIterator(s),8)), async);
	}
	public void tx(Short[] s,boolean async) {
		this.tx(new ModulateIterable(this._parent,this._mod.modulate(compatibility.toIntegerPyIterator(s),8)), async);
	}
	public void tx(Byte[] s,boolean async) {
		this.tx(new ModulateIterable(this._parent,this._mod.modulate(compatibility.toIntegerPyIterator(s),8)), async);
	}
	public void tx(int[] s,boolean async) {
		this.tx(new ModulateIterable(this._parent,this._mod.modulate(compatibility.toIntegerPyIterator(s),8)), async);
	}
	public void tx(short[] s,boolean async) {
		this.tx(new ModulateIterable(this._parent,this._mod.modulate(compatibility.toIntegerPyIterator(s),8)), async);
	}
	public void tx(byte[] s,boolean async) {
		this.tx(new ModulateIterable(this._parent,this._mod.modulate(compatibility.toIntegerPyIterator(s),8)), async);
	}
	public void tx(String s,boolean async) {
		try {
			this.tx(new ModulateIterable(this._parent,this._mod.modulate(s)),async);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}		
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

	//Processing familier function


	/**
	 * return the version of the Library.
	 * 
	 * @return String
	 */
	public static String version() {
		return Version.STRING;
	}

	

}

