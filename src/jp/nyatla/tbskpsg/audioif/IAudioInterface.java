package jp.nyatla.tbskpsg.audioif;

import jp.nyatla.tbskpsg.result.ModulateIterable;

public interface IAudioInterface
{
	public int getSampleRate();
	public IAudioInputIterator createInputIterator();
	public IAudioPlayer createPlayer(ModulateIterable s);
}

