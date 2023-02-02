package jp.nyatla.tbskpsg.audioif;

import java.io.Closeable;

/**
 * ワンショットのメディア再生を実行します。
 *
 */
public interface IAudioPlayer extends Closeable{
	/**
	 * 先頭から再生します。再生中の場合は失敗します。この関数はブロックします。
	 */
    public void play() throws InterruptedException;
    /**
     * 再生を停止します。再生を停止すると再開できません。
     */
    public void stop();
    /**
     * 再生が終了したか返します。
     */
    public boolean isFinished();
}