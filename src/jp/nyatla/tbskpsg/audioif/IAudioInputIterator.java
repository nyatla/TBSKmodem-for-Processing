package jp.nyatla.tbskpsg.audioif;

import java.io.Closeable;

import jp.nyatla.kokolink.types.Py_interface__.IPyIterator;

/**
 * Audioデバイスからサンプリング値を取り込むイテレータです。
 *
 */
public interface IAudioInputIterator extends IPyIterator<Double>,Closeable
{
	/**
	 * 受信したフレーム数を返します。
	 * @return
	 */
	public long acceptedSamples();
	/**
	 * Σsqrt(sample[n]^2)/nを返します。
	 * @return
	 */
	public double getRMS();
	/**
	 * trueにセットされた場合には、返却値を全て0にする。
	 * @param v
	 */
	public void mute(boolean v);
}
