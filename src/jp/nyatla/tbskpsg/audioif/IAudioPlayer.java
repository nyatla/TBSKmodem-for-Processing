package jp.nyatla.tbskpsg.audioif;

import java.io.Closeable;

/**
 * ワンショットのメディア再生を実行します。
 *
 */
public interface IAudioPlayer extends Closeable{
	/**
	 * 先頭から再生します。再生中の場合は失敗します。
	 * @param async
	 * メディア再生を非同期に実行する場合はtrueを設定します。
	 */
    public void play();
    /**
     * 再生を停止します。再生を停止すると再開できません。
     */
    public void stop();
    /**
     * 再生が終わるまで規定時間待機します。
     * 待機中に再生が完了した倍はTrueです。完了しない場合Falseです。
     * @params timeout_in_msec
     * -1で無限にブロックします。0で現状を返します。
     * @throws InterruptedException 
     */
    public boolean waitForFinished(long timeout_in_msec) throws InterruptedException;
}