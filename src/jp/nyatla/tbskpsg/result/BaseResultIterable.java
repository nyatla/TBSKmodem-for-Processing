package jp.nyatla.tbskpsg.result;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import processing.core.PApplet;

public class BaseResultIterable<T> implements Iterable<T>{
	protected final PApplet _parent;
	private Iterator<T> _iter;
	BaseResultIterable(PApplet parent,Iterable<T> s){
		this._iter=s.iterator();
		this._parent=parent;
	}
	protected void checkExpired() {
		if(this._iter==null) {
			throw new RuntimeException("This instance has been used.");
		}
	}

	@Override
	final public Iterator<T> iterator() {
		this.checkExpired();
		Iterator<T> r=this._iter;
		this._iter=null;
		return r;
	}
	final public List<T> toList()
	{
		this.checkExpired();
    	List<T> t=new ArrayList<T>();
    	for(T i:this) {
    		t.add(i);
    	}
    	return t;
	}		
}