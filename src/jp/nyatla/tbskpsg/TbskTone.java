package jp.nyatla.tbskpsg;

import java.util.ArrayList;
import java.util.List;

import jp.nyatla.kokolink.protocol.tbsk.toneblock.SinTone;
import jp.nyatla.kokolink.protocol.tbsk.toneblock.TraitTone;
import jp.nyatla.kokolink.protocol.tbsk.toneblock.XPskSinTone;

/**
 * An instance that wraps the TBSK tone.
 * Use static function instead of a constructor.
 */
public class TbskTone
{
	private TraitTone _src;
	private TbskTone(TraitTone src){
		this._src=src;
	}
	/**
	 * ラップしているトーン信号を返します。
	 * @return
	 */
	public TraitTone getBase() {
		return this._src;
	}
	/**
	 * Multiply the volume of the tone signal by vol.
	 * <br></br>
	 * トーン信号の音量をvol倍します。
	 * 
	 * @param vol
	 * @return
	 */
	public TbskTone mul(float vol) {
		this._src.mul(vol);
		return this;
	}
	
	/**
	 * Creates a phase-shifted sine wave, a type of spread spectrum signal.
	 * <br><br/>
	 * 位相をPN符号でシフトしたトーン信号を生成します。ティック数がpoints*cycle個のトーン信号を生成します。
	 * トーン信号は、points個のサンプルで構成したSin一周期を、2π/div*pn[i]づつ位相をずらして再配置したスペクトラム信号です。
	 * 
	 * @param points
	 * Number of point per 1 cycle.
	 * <br></br>
	 * 1周期のポイント数
	 * 
	 * @param cycle
	 * Number of cycle.
	 * 
	 * @param div
	 * Number of phase shift divisions.
	 * <br></br>
	 * 位相シフトの分割数
	 * 
	 * @return
	 * points*cycleのスペクトラム拡散信号。
	 */
	public static TbskTone xpskSin(int points,int cycle,int div) {
		return new TbskTone(new XPskSinTone(points, cycle,div,null));
	}
	/**
	 * Same as xpskSin(points,cycle,8);
	 * @param points
	 * @param cycle
	 * @return
	 */
	public static TbskTone xpskSin(int points,int cycle) {
		return xpskSin(points,cycle,8);
	}
	/**
	 * point=10,cycle=10をセットした100tickのトーンです。
	 * @return
	 */
	public static TbskTone xpskSin() {
		return xpskSin(10,10,8);
	}

	/**
	 * Create connected sin wave tone.
	 * <br></br>
	 * Sin波を結合したpoints*cycle個のトーン信号を生成します。
	 * トーン信号は、周期がcycle個あるサイン波です。
	 * @param points
	 * Number of point per 1 cycle.
	 * 1周期のポイント数
	 * @param cycle
	 * Number of cycle.
	 * <br></br>
	 * 繰り返し回数
	 * @return
	 * Tone which has points*cycle ticks.
	 */
	public static TbskTone sin(int points,int cycle) {
		return new TbskTone(new SinTone(points, cycle));
	}
	/**
	 * Same as sin(points,1)
	 * @return
	 */	
	public static TbskTone sin(int points) {
		return sin(points,1);
	}
	/**
	 * Same as sin(10,10)
	 * @return
	 */	
	public static TbskTone sin() {
		return sin(10,10);
	}

	/**
	 * Create tone instance with custom pattern.
	 * <br></br>
	 * カスタム形式のトーン信号を生成します。
	 * @param d
	 * [-1,1] array.
	 * @return
	 * new instance.
	 */
	public static <T>TbskTone custom(Iterable<Double> d) {
		return new TbskTone(new TraitTone(d));
	}
	/**
	 * @see #custom(Iterable)
	 */
	public static TbskTone custom(double[] d) {
		List<Double> t=new ArrayList<Double>();
		for(double i:d) {
			t.add(i);
		}
		return custom(t);
	}
	/**
	 * @see #custom(Iterable)
	 */
	public static TbskTone custom(float[] d) {
		List<Double> t=new ArrayList<Double>();
		for(float i:d) {
			t.add((double)i);
		}
		return custom(t);
	}


}
