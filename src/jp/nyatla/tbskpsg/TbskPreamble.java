package jp.nyatla.tbskpsg;

import jp.nyatla.kokolink.protocol.tbsk.preamble.CoffPreamble;
import jp.nyatla.kokolink.protocol.tbsk.preamble.Preamble;

/**
 * An instance that wraps the TBSK preamble.
 * Use static function instead of a constructor.
 */
public class TbskPreamble
{
	private Preamble _base;
	private TbskPreamble(Preamble base) {
		this._base=base;
	}
	public Preamble getBase() {
		return this._base;
	}
	/**
	 * Create CoffPreamble with default parametor.
	 * @param tone
	 * Symbol tone.
	 * @return
	 * new Preamble instance.
	 */
	public static TbskPreamble coff(TbskTone tone) {
		return new TbskPreamble(new CoffPreamble(tone.getBase(),CoffPreamble.DEFAULT_TH,CoffPreamble.DEFAULT_CYCLE));
	}
	/**
	 * Create CoffPreamble with parameters.
	 * Wrong parameters will not synchronize. Avoid using this function.
	 * @param tone
	 * @param threshold
	 * Synchronus threshold.
	 * @param cycle
	 * Number of detection symbols.
	 * @return
	 * new Preamble instance.
	 */
	public static TbskPreamble coff(TbskTone tone,float threshold,int cycle) {
		return new TbskPreamble(new CoffPreamble(tone.getBase(),threshold,cycle));
	}
}
