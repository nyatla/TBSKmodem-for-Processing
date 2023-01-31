package jp.nyatla.tbskpsg.audioif;

import java.io.Closeable;

import jp.nyatla.kokolink.types.Py_interface__.IPyIterator;

/**
 * Audioデバイスからサンプリング値を取り込むイテレータです。
 *
 */
public interface IAudioInputIterator extends IPyIterator<Double>,Closeable
{
}
