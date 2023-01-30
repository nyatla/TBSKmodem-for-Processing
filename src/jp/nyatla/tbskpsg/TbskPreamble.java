package jp.nyatla.tbskpsg;

import jp.nyatla.kokolink.protocol.tbsk.preamble.CoffPreamble;
import jp.nyatla.kokolink.protocol.tbsk.preamble.Preamble;

public class TbskPreamble
{
	private Preamble _base;
	private TbskPreamble(Preamble base) {
		this._base=base;
	}
	public Preamble getBase() {
		return this._base;
	}
	public static TbskPreamble coff(TbskTone tone) {
		return new TbskPreamble(new CoffPreamble(tone.getBase(),CoffPreamble.DEFAULT_TH,CoffPreamble.DEFAULT_CYCLE));
	}
}
