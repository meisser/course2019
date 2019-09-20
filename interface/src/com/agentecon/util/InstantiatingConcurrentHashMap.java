// Created on Jun 23, 2015 by Luzius Meisser

package com.agentecon.util;

import java.util.concurrent.ConcurrentHashMap;

public abstract class InstantiatingConcurrentHashMap<K, V> extends ConcurrentHashMap<K, V> {

	private static final long serialVersionUID = 1L;
	
	public InstantiatingConcurrentHashMap(){
		super();
	}
	
	@SuppressWarnings("unchecked")
	public V get(Object key){
		V val = super.get(key);
		if (val == null){
			val = create((K)key);
			put((K)key, val);
		}
		return val;
	}

	protected abstract V create(K key);

}
