package jp.nyatla.tbskpsg;




import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.Semaphore;

import jp.nyatla.tbskpsg.audioif.IAudioInputIterator;
import jp.nyatla.tbskpsg.audioif.IAudioInterface;
import jp.nyatla.tbskpsg.audioif.IAudioPlayer;

import jp.nyatla.kokolink.compatibility.Functions;
import jp.nyatla.kokolink.protocol.tbsk.preamble.CoffPreamble;
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
	/**
	 * Seacret flags!
	 */
	public static boolean _DEBUG=false;
	public static boolean _STOPSYMBOL=true;
	
	/**
	 * For debugging!
	 * @param messgae
	 */
	static public void debug(String messgae) {
		if(TbskModem._DEBUG) {
			System.out.println(messgae);
		}
	}
	

	private TbskModulator _mod;
	private TbskDemodulator _demod;
	
	// myParent is a reference to the parent sketch
	private PApplet _parent;


	final private IAudioInterface _aif;
	private RxTask _rxtask;
	final private int _baud;
	/**
	 * TX終了後のガードタイム
	 */
	private int _tx_guard_sleep;
	
	
	

	/**
	 * @see {@link TbskModem#TbskModem(PApplet, TbskTone, float, int, IAudioInterface)}
	 * @param parent
	 * @param tone
	 * @param aif
	 */
	public TbskModem(PApplet parent,TbskTone tone,IAudioInterface aif)
	{
		this(parent,tone,(float)CoffPreamble.DEFAULT_TH,CoffPreamble.DEFAULT_CYCLE,aif);
	}
	/**
	 * TBSK信号復調器のコンストラクタです。
	 * @param parent
	 * processingアプレットのインスタンス
	 * @param tone
	 * トーン信号のインスタンス
	 * @param preamble_th
	 * 信号の検出閾値
	 * @param preamble_cycle
	 * プリアンブルのシンボルサイクル数	 * 
	 */
	public TbskModem(PApplet parent,TbskTone tone,float preamble_th,int preamble_cycle,IAudioInterface aif)
	{	
		this(parent,tone,TbskPreamble.coff(tone, preamble_th, preamble_cycle),aif);
	}
	
	/**
	 * @see {@link TbskModem#TbskModem(PApplet, TbskTone, float, int, IAudioInterface)}
	 * @param parent
	 * @param aif
	 */
	public TbskModem(PApplet parent,IAudioInterface aif)
	{
		this(parent,TbskTone.xpskSin(),aif);
	}
	
	/**
	 * @deprecated
	 * TbskModemのコンストラクタです.
	 * @param parent
	 * PApplet instance.
	 * @param tone
	 * Tone symbol for TBSK modulation.
	 * @param preamble
	 * Preamble format for TBSK modulation.
	 * @param aif
	 * Aufio Interface.
	 */
	public TbskModem(PApplet parent,TbskTone tone,TbskPreamble preamble,IAudioInterface aif)
	{
		assert(aif!=null);
		this._parent = parent;
		this._mod=new TbskModulator(tone.getBase(),preamble.getBase());
		this._demod=new TbskDemodulator(tone.getBase(),preamble.getBase());
		this._rxtask=null;
		this._aif=aif;
		this._baud=aif.getSampleRate()/tone.getBase().size();
		this._tx_guard_sleep=preamble.getBase().getNumberOfTicks()/aif.getSampleRate();


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
	}	
	
	
	public int sampleRate() {
		return this._aif.getSampleRate();
	}
	public float baud() {
		return this._baud;
	}
	
	/**
	 * この関数はPAppletのファイナライザから呼び出されます。
	 */
	public void dispose() {
		TbskModem.debug("Enter dispose sequence.");
		this.stop();
	}
	
	/**
	 * モデムの動作を開始します。成功すると、受信タスクが起動します。
	 */
	synchronized public void start()
	{
		if(this._rxtask!=null) {
			throw new RuntimeException("Modem is already Running.");
		}
		IAudioInputIterator input=this._aif.createInputIterator();
		if(input==null) {
			throw new RuntimeException("This instance has not Audio input iterator.");
		}
		RxTask task=new RxTask(this._demod,input);
		task.start();
		this._rxtask=task;
	}
	/**
	 * モデムの動作を停止します。
	 */
	synchronized public void stop()
	{
		if(this._rxtask==null) {
			TbskModem.debug("Modem is already Stopped.");
			return;
		}
		this._rxtask.dispose();
		TbskModem.debug("RxTask disposed.");
		this._rxtask=null;
	}
	/**
	 * 入力した波形データのRMS値を返します。
	 * RMS=√(Σ(sample[x]^2)/n),n=max(sampleRate/100,10)
	 * 
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
			private boolean _is_stop;//フレームが確定するとtrue
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
			/**
			 * フレームが確定したら呼び出す。
			 */
			synchronized void stop() {
				this._is_stop=true;
			}
			synchronized boolean readyFrame()
			{
				return this._is_stop;
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
					return (0xff & r);
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
				//現在のバッファでCharが構成できる場合
				int l=dec.test();
				if(l>0) {
					return true;
				}
				//バッファが追加出来なくなるまでCharが構成できるか確認
				while(!dec.isBufferFull()) {
					if(this._q.size()>0) {
						Integer tmp=this._q.poll();
						assert(tmp!=null);							
						l=dec.test(tmp.byteValue());
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
				if(this._is_stop) {
					//終端確定の場合はupdateで処理するから解析キューに文字があればtrue
					return dec.holdLen()>0;
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
			/**
			 * このブロックの追加更新が停止していたらtrue
			 */
			synchronized boolean isStopped() {
				return this._is_stop;
			}
			/**
			 * 格納しているバイト数
			 * @return
			 */
			synchronized int size() {
				return this._q.size()+this._decoder.holdLen();
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
		 * 先頭キューのデータサイズを返します。
		 * @return
		 */
		public int currentSize() {
			if(this._buf.isEmpty()) {
				return 0;
			}
			return this._buf.get(0).size();
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
		public boolean readyFrame()
		{
			List<RxData> buf=this._buf;
			if(buf.size()<=1 || !buf.get(0).readyFrame()) {
				return false;
			}
			return true;	
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
		public void clear() {
			while(!this._buf.isEmpty()) {
				if(this._buf.get(0).isStopped()) {
					this._buf.remove(0);
				}else {
					break;
				}
			}
		}
		/**
		 * バッファ全体のサイズを返す。
		 * @return
		 */
		public int totalSize() {
			int r=0;
			for(RxData d:this._buf) {
				r=r+d.size();
			}
			return r;			
		}
	}


	
	private class RxTask extends Thread{
		private TbskDemodulator _demod;
		private RxBuffer _rxb;
		private IAudioInputIterator _input;
		private int _number;
		private boolean _break_flag;
		public RxTask(TbskDemodulator demod,IAudioInputIterator input) {
			this._number=0;
			this._demod=demod;
			this._input=input;
			this._rxb=new RxBuffer();
		}
		public void run()
		{
			TbskModem.debug("start RxTask");
			this._number=0;
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
					rxd=this._rxb.enter(this._number++);
				}
				this._break_flag=false;	//RX中断リセット
				for(Integer i:iter) {	//このイテレータはInterruptでstopiterationを出す。
					if(this._break_flag)//RX中断確認
					{
						break;
					}
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
		synchronized public boolean readyFrame() {
			return this._rxb.readyFrame();
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
		synchronized long getNumber() {
			return this._number;
		}
		synchronized void mute() {
			TbskModem.debug("rx:mute");
			this._input.mute(true);
		}
		synchronized void unmute() {
			TbskModem.debug("rx:unmute");
			this._input.mute(false);
		}
		synchronized void clear() {
			this._rxb.clear();
		}
		synchronized int totalSize() {
			return this._rxb.totalSize();
		}
		synchronized int currentSize() {
			return this._rxb.currentSize();
		}
		synchronized public void rxBreak() {
			this._break_flag=true;
		}
		
	}
	/**
	 * 現在受信しているTBSK信号の通し番号です。この番号はTBSK信号の境界識別に使用します。
	 * @return
	 */
	synchronized public long rxNumber() {
		return this._rxtask.getNumber();
	}
	/**
	 * {#rx()}が利用可能ならTrueを返します。
	 * @return
	 * True if {@link #rx()} is callable.
	 */
	synchronized public boolean rxReady() {
		return this._rxtask.ready();
	}
	/**
	 * 現在のフレームが確定済みであればクリアします。
	 * 未確定ならクリアしません。
	 * @return
	 */
	synchronized public void rxClear() {
		this._rxtask.clear();
	}
	
	/**
	 * 1バイトを符号なし整数で読みだします。
	 * @return
	 * 255>=n>=0
	 */
	synchronized public int rx() {
		return this._rxtask.read();		
	}
	/**
	 * {#rxAsChar()}が利用可能ならTrueを返します。
	 * @return
	 * True if {@link #rxAsChar()} is callable.
	 */
	synchronized public boolean rxAsCharReady() {
		return this._rxtask.readyChar();
	}
	/**
	 * 文字を読み出します。
	 * @return
	 * UTF-8 encoded charactor. "?" is returned if bad encoding.
	 */
	synchronized public char rxAsChar() {
		return this._rxtask.readChar();		
	}	
	/**
	 * 受信の完了したTBSKフレームを読みだせる場合、Trueを返します。
	 */
	public boolean readyFrame() {
		return this._rxtask.readyFrame();
	}	

	/**
	 * Number of total bytes onrx buffer.
	 * <br></br>
	 * rxキュー全体の格納サイズです
	 * @return
	 */
	synchronized public int rxTotalSize() {
		return this._rxtask.totalSize();
	}
	/**
	 * 先頭にあるカレントrxキューのサイズです。
	 * この値はシグナル単位です。
	 * @return
	 */
	public int rxCurrentSize() {
		return this._rxtask.currentSize();
	}
	/**
	 * パケットを受信中なら打ち切ります。
	 */
	public void rxBreak() {
		this._rxtask.rxBreak();
	}
	
	
	
	private AsycPlayer _async_player=null;	
	
	/**
	 * tx関数の状態を返します。非同期送信の場合に有効です。
	 * @return
	 * Trueの場合、tx関数は非同期で動作中です。
	 */
	synchronized public boolean txReady() {
		if(this._async_player!=null) {
			if(this._async_player._finished) {
				//finishedしてるなら待てばいい。
				return true;
			}
			return false;
		}else {
			return this._async_player==null;			
		}
	}
	/**
	 * 送信中の非同期トランザクションを中止します。
	 * @return
	 */
	synchronized public void txBreak() {
		try {
			if(this._async_player!=null) {
				this._async_player.close();
				this._async_player=null;
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * プレイヤーが停止する迄待ってくれる。有難いクラス
	 * なぜこんな馬鹿な仕組みになってるのかというと、そうせざる得ないからだ。
	 *
	 */
	abstract class AsycPlayer
	{
		private IAudioPlayer _ap;
		private Semaphore _se=new Semaphore(1);
		private Thread _th;
		private boolean _finished;
		AsycPlayer(IAudioPlayer ap){
			this._finished=false;
			this._ap=ap;
		}
		//非同期再生を開始する。
		void play() throws InterruptedException {
			class Th extends Thread{
				public void run() {
					try {
						_ap.play();
						_se.release();//closeすると飛ぶはず。
					} catch (InterruptedException e) {
					}finally {
						_finished=true;
						onExpired();
					}
				}
			}
			this._se.acquire();
			Th th=new Th();
			th.start();
			this._th=th;
		}
		public boolean getFinished() {
			return this._finished;
		}
		void close() throws InterruptedException {
			this._ap.stop();
			this._th.interrupt();
			try{
				this._th.join();
			}finally {
				this._ap=null;
			}
		}
		public void waitForEnd() throws InterruptedException {
			this._se.acquire();//セマフォ待ち
		}
		abstract void onExpired();

	}
	/**
	 * sをTBSK変調して発信します。
	 * @param s
	 * 変調元の符号なし8bit整数
	 * @param async
	 * 非同期送信フラグです。trueの場合、送信の終了を待たずに関数が返ります。
	 */
	synchronized public void tx(ModulateIterable s,boolean async)
	{
		if(this._aif==null) {
			throw new RuntimeException("This instance has not Audio interface");
		}
		//有効な非同期プレイヤーが動作中なら再生が完了するまで待つ。
		{
			AsycPlayer pobs=this._async_player;
			if(pobs!=null) {
				try {
					pobs.waitForEnd();
					Thread.sleep(this._tx_guard_sleep);					
				} catch (InterruptedException e) {
					try {
						pobs.close();
					} catch (InterruptedException e1) {
						throw new RuntimeException(e);
					}//強制終了
					throw new RuntimeException(e);					
				}finally {
					this._async_player=null;					
				}
			}
		}
		{
			//新規再生
			IAudioPlayer player=this._aif.createPlayer(s);
			if(player==null) {
				throw new RuntimeException("This instance has not Player interface");
			}
			if(!async) {
				//同期
				try {
					this._rxtask.mute();
					player.play();
					//Txスリープはプリアンブル分くらい
					Thread.sleep(this._tx_guard_sleep);					
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}finally {
					try {
						player.close();
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
					this._rxtask.unmute();
				}				
			}else {
				//非同期
				AsycPlayer pobs=new AsycPlayer(player) {
					@Override
					void onExpired() {
						_rxtask.unmute();
					}
				};
				try {
					this._rxtask.mute();
					pobs.play();					
				} catch (InterruptedException e) {
					throw new RuntimeException(e);//起こらないはずなのだが。
				}
				this._async_player=pobs;
			}
		}
	}

	public void tx(Iterable<Integer> s,boolean async) {
		this.tx(new ModulateIterable(this._parent,this._mod.modulate(s,8,_STOPSYMBOL)), async);
	}
	public void tx(Integer[] s,boolean async) {
		this.tx(new ModulateIterable(this._parent,this._mod.modulate(Functions.toIntegerPyIterator(s),8,_STOPSYMBOL)), async);
	}
	public void tx(Short[] s,boolean async) {
		this.tx(new ModulateIterable(this._parent,this._mod.modulate(Functions.toIntegerPyIterator(s),8,_STOPSYMBOL)), async);
	}
	public void tx(Byte[] s,boolean async) {
		this.tx(new ModulateIterable(this._parent,this._mod.modulate(Functions.toIntegerPyIterator(s),8,_STOPSYMBOL)), async);
	}
	public void tx(int[] s,boolean async) {
		this.tx(new ModulateIterable(this._parent,this._mod.modulate(Functions.toIntegerPyIterator(s),8,_STOPSYMBOL)), async);
	}
	public void tx(short[] s,boolean async) {
		this.tx(new ModulateIterable(this._parent,this._mod.modulate(Functions.toIntegerPyIterator(s),8,_STOPSYMBOL)), async);
	}
	public void tx(byte[] s,boolean async) {
		this.tx(new ModulateIterable(this._parent,this._mod.modulate(Functions.toIntegerPyIterator(s),8,_STOPSYMBOL)), async);
	}
	public void tx(String s,boolean async) {
		try {
			this.tx(new ModulateIterable(this._parent,this._mod.modulate(s,_STOPSYMBOL)),async);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}		
	}

	/**
	 * @see {@link #tx(Integer[], boolean)
	 */	
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
	public void tx(char[] s) {		
		this.tx(String.valueOf(s),true);
	}
	public void tx(Character[] s) {
		this.tx(String.valueOf(s),true);
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

