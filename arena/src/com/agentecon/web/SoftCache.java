package com.agentecon.web;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Iterator;

import com.agentecon.runner.IFactory;

public class SoftCache<K, V> {

	private HashMap<K, SoftReference<V>> simulations;

	public SoftCache() {
		this.simulations = new HashMap<>();
	}

	public synchronized V getOrSetCachedItem(K key, IFactory<V> factory) throws IOException {
		V prev = get(key);
		if (prev == null) {
			V value = factory.create();
			put(key, value);
			return value;
		} else {
			return prev;
		}
	}

	public synchronized void put(K key, V value) {
		if (value == null) {
			this.simulations.remove(key);
		} else {
			this.simulations.put(key, new SoftReference<V>(value));
		}
	}

	public synchronized V get(K handle) {
		SoftReference<V> ref = simulations.get(handle);
		if (ref != null) {
			V stepper = ref.get();
			if (stepper == null) {
				cleanup();
			}
			return stepper;
		} else {
			return null;
		}
	}

	private void cleanup() {
		Iterator<SoftReference<V>> iter = simulations.values().iterator();
		while (iter.hasNext()) {
			if (iter.next().get() == null) {
				iter.remove();
			}
		}
	}

}
