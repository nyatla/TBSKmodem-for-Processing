package jp.nyatla.tbskpsg;




import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.util.ArrayDeque;
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
	private IAudioInputIterator _inputiter;
	private RxTask _rxtask;
	
	
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
	
	

	

	

	


	class RxData{
		private ArrayDeque<Integer> _q=new ArrayDeque<Integer>();
		private BrokenTextStreamDecoder _decoder;
		private boolean _is_stop;
		public RxData()
		{
			this._is_stop=false;
		}
		synchronized void push_bits(int v) {
			this._q.add(v);
		}
		synchronized void stop() {
			this._is_stop=true;
		}
		/**
		 * このインスタンスで受信できる可能性があるときはtrue
		 * @return
		 */
		synchronized public boolean active() {
			return this._decoder.holdLen()>0 ||this._q.size()>0 || this._is_stop==false;
		}		
		/**
		 * 1バイトのデータを得る。readyAsIntがtrueである必要がある。
		 * @return
		 */
		synchronized public int asInt()
		{
			//文字解析キューに入っているものを先に消費
			Byte s=this._decoder.peekFront();
			if(s!=null) {
				return Byte.toUnsignedInt(s);
			}			
			assert(this._q.size()>0);
			return this._q.poll();
		}

		/**
		 * 読み出し可能状態にあるか返す。
		 * @return
		 * trueの場合、asIntが利用できます。
		 */
		synchronized public boolean readyAsInt() {
			return this._q.size()>0 || this._decoder.holdLen()>0;
		}/*
		
		synchronized public Character asChar()
		{
			//キューにbyteがある間頑張る
			while(this._q.size()>0){
				Character c=this._decoder.update(this._q.poll().byteValue());
				if(c!=null) {
					return c;
				}
			}
			//キューにbyteがない
			if(this._is_stop) {
				//入力見込みがない
				return this._decoder.update();
			}
			//入力見込みがある
			return null;
		}		
		
		
		synchronized public boolean readyAsChar()
		{
			while(!this._decoder.isBufferFull()) {
				if(this._q.size()>0){
					Character c=this._decoder.test(this._q.poll().byteValue());
					if(c!=null) {
						return c;
					}
				}
				
			}

			if(this._decoder.isBufferFull()) {
				int l=this._decoder.test();
				if(l==-1) {
					return true;//"?"が返ってくる。
				}else if(l==0) {
				}
			}
			int ql=this._decoder.holdLen();
			
			while(this._q.size()>0){
				Character c=this._decoder.test(this._q.poll().byteValue());
				if(c!=null) {
					return c;
				}
			}
			
			return this._decoder.test();
			return this._q.size()>0 || this._decoder.holdLen()>0;
		}	*/	

	}



	
	class RxTask extends Thread{
		private TbskDemodulator _demod;
		private BlockingQueue<RxData> _rxq;
		private IAudioInputIterator _input;
		public RxTask(TbskDemodulator demod,IAudioInputIterator input) {
			this._demod=demod;
			this._input=input;
			this._rxq=new ArrayBlockingQueue<RxData>(1);
		}
		public void run()
		{
			for(;;) {
				//bit iterableを起動	
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
					PApplet.debug("RXTask closing.");
					return;
				}
				RxData last=new RxData();
				synchronized(this) {
					this._rxq.add(last);					
				}
				for(Integer i:iter) {	//このイテレータはInterruptでstopiterationを出す。
					last.push_bits(i);//データ追記
				}
				last.stop();//停止
			}
		}
		synchronized public boolean ready() {
			//先頭から無効なRxDataを削除
			while(this._rxq.size()>0) {
				var d=this._rxq.peek();
				if(d.active()) {
					break;
				}
				this._rxq.remove();
			}				
			if(this._rxq.size()==0) {
				return false;
			}
			if(this._rxq.size()>0) {
				RxData d=this._rxq.peek();
				return d.readyAsInt();
			}
			return false;
		}
		synchronized public int read() {
			if(!this.ready()) {
				throw new RuntimeException("Not ready(Q).");
			}
			RxData d=this._rxq.peek();
			return d.asInt();
		}
	}	

	public void startRx()
	{
		if(this._rxtask!=null) {
			throw new RuntimeException("RxTask is already Running.");
		}
		if(this._aif==null) {
			throw new RuntimeException("This instance has not Audio interface");
		}
		IAudioInputIterator input=this._aif.createInputIterator();
		if(input==null) {
			throw new RuntimeException("This instance has not Audio input iterator.");
		}
		RxTask task=new RxTask(this._demod,input);
		this._rxtask=task;
	}
	public void stopRx()
	{
		if(this._aif==null) {
			throw new RuntimeException("This instance has not Audio interface");
		}
		if(this._inputiter==null) {
			assert(this._rxtask==null);
			throw new RuntimeException("This instance has not Audio input iterator.");
		}
		try {
			//
			this._inputiter.close();//ここでStopIterationが発生して、waitForSignalが停止するはず。
		} catch (IOException e) {
			throw new RuntimeException(e);
		}finally {
			this._inputiter=null;
			this._rxtask.interrupt();
			try {
				this._rxtask.join();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}finally {
				this._rxtask=null;
			}
		}
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
	 * @param s
	 * 
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




	/**
	 * return the version of the Library.
	 * 
	 * @return String
	 */
	public static String version() {
		return Version.STRING;
	}

	

}

