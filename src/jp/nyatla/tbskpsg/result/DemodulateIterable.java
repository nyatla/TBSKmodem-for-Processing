package jp.nyatla.tbskpsg.result;

import java.util.List;

import processing.core.PApplet;

public class DemodulateIterable extends BaseResultIterable<Integer>
{	
	public DemodulateIterable(PApplet parent,Iterable<Integer> s) {
		super(parent,s);
	}
	public int[] toArray() {
		this.checkExpired();
		List<Integer> t=this.toList();//in check expired
    	int[] r=new int[t.size()];
    	for(int i=0;i<r.length;i++) {
    		r[i]=t.get(i);
    	}
    	return r;			
	}
}