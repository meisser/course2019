package com.agentecon.runner;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.agentecon.ISimulation;

public class SimulationCache {

	private SimulationLoader loader;
	private TreeMap<Integer, ArrayList<SoftReference<ISimulation>>> sims;

	public SimulationCache(SimulationLoader loader) throws IOException {
		this.loader = loader;
		this.sims = new TreeMap<>();
		ISimulation sim = loader.loadSimulation();
		recycle(sim);
	}

	public synchronized Recyclable<ISimulation> borrow(int day) throws IOException {
		Entry<Integer, ArrayList<SoftReference<ISimulation>>> entry = sims.floorEntry(day);
		if (entry == null) {
			return wrap(loader.loadSimulation());
		} else {
			ArrayList<SoftReference<ISimulation>> list = entry.getValue();
			ISimulation sim = extract(list);
			if (list.isEmpty()) {
				sims.remove(entry.getKey());
			}
			if (sim == null) {
				return borrow(day);
			} else {
				return wrap(sim);
			}
		}
	}

	private ISimulation extract(ArrayList<SoftReference<ISimulation>> list) {
		for (int i = list.size() - 1; i >= 0; i--) {
			SoftReference<ISimulation> ref = list.remove(i);
			ISimulation sim = ref.get();
			if (sim != null) {
				return sim;
			}
		}
		return null;
	}

	private Recyclable<ISimulation> wrap(ISimulation simulation) {
		return new Recyclable<ISimulation>(simulation) {

			@Override
			protected void recycle(ISimulation item) {
				SimulationCache.this.recycle(item);
			}
		};
	}

	protected synchronized void recycle(ISimulation sim) {
		ArrayList<SoftReference<ISimulation>> list = this.sims.get(sim.getDay());
		if (list == null) {
			list = new ArrayList<>();
			this.sims.put(sim.getDay(), list);
		}
		insert(sim, list);
	}

	private void insert(ISimulation sim, ArrayList<SoftReference<ISimulation>> list) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).get() == null) {
				list.set(i, new SoftReference<ISimulation>(sim));
				return;
			}
		}
		list.add(new SoftReference<ISimulation>(sim));
	}

}
