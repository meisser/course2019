// Created on Jun 2, 2015 by Luzius Meisser

package com.agentecon.util;

import java.util.ArrayList;

public class AbstractListenerList<T> {

	protected ArrayList<T> list = new ArrayList<>();
	
	public AbstractListenerList() {
		super();
	}

	public void add(T l) {
		assert l != null;
		list.add(l);
	}
	
	public void remove(T l){
		list.remove(l);
	}

	@Override
	public String toString(){
		return list.toString();
	}
	
}