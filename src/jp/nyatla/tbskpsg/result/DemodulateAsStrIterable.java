package jp.nyatla.tbskpsg.result;

import java.util.List;

import processing.core.PApplet;


/**
 * {@link TbskModem#demodulate(Iterable)} の戻り値を格納するクラス。
 * {@link Iterable<Characotr>}型を継承し、値全体を配列や文字列として返す関数を持つ。
 * <br/>
 * Result value type of the {@link TbskModem#demodulate(Iterable)} function.
 * This is {@link Iterable<Characotr>} object with functions that return the entire value as a String or array.
 */
public class DemodulateAsStrIterable extends BaseResultIterable<Character>
{	
	public DemodulateAsStrIterable(PApplet parent,Iterable<Character> s) {
		super(parent,s);
	}
	/**
	 * 値全体の文字列を返す。
	 * <br/>
	 * Make string value from iterated values.
	 */
	public String toString() {
		return String.valueOf(this.toArray());//in check expired
	}
	
	/**
	 * 値全体の配列を返す。
	 * <br/>
	 * Make array value from iterated values.
	 */
	public char[] toArray() {
		List<Character> t=this.toList();//in check expired
		char[] r=new char[t.size()];
    	for(int i=0;i<r.length;i++) {
    		r[i]=t.get(i);
    	}
    	return r;			
	}
}