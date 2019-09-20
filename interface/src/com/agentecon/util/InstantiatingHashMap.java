// Created on Jun 23, 2015 by Luzius Meisser

package com.agentecon.util;

import java.util.HashMap;

public abstract class InstantiatingHashMap<K, V> extends HashMap<K, V> {

	private static final long serialVersionUID = 1L;
	
	public InstantiatingHashMap(){
		super();
	}
	
	public final V getWithoutCreating(Object key) {
		return super.get(key);
	}
	
	@SuppressWarnings("unchecked")
	public final V get(Object key){
		V val = super.get(key);
		if (val == null){
			val = create((K)key);
			put((K)key, val);
		}
		return val;
	}

	protected abstract V create(K key);

}
