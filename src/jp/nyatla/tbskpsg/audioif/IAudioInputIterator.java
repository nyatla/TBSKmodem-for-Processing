package jp.nyatla.tbskpsg.audioif;

import java.io.Closeable;

import jp.nyatla.kokolink.types.Py_interface__.IPyIterator;

/**
 * Audioデバイスからサンプリング値を取り込むイテレータです。
 *
 */
public interface IAudioInputIterator extends IPyIterator<Float>,Closeable
{
	/**
	 * データの取り込みを開始します。
	 * 取り込みキューは初期化されます。
	 */
	public void start();
	/**
	 * データの取り込みを停止します。
	 * 待機している__next__は直ちに例外を発生させて停止します。
	 * Startで再開できます。
	 */
	public void stop();
}
