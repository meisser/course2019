package com.agentecon.runner;

import java.io.IOException;
import java.net.SocketTimeoutException;

import com.agentecon.ISimulation;
import com.agentecon.classloader.LocalSimulationHandle;
import com.agentecon.classloader.SimulationHandle;
import com.agentecon.util.LogClock;
import com.agentecon.web.SoftCache;

public class SimulationStepper {

	private SimulationCache simulation;
	private SimulationLoader loader;
	private SoftCache<Object, Object> cachedData;

	private SimulationStepper successor;

	public SimulationStepper(SimulationHandle handle) throws IOException {
		this(new SimulationLoader(handle));
	}

	public SimulationStepper(SimulationLoader loader) throws IOException {
		this.loader = loader;
		this.simulation = new SimulationCache(loader);
		this.cachedData = new SoftCache<>();
		this.successor = null;
	}
	
	public boolean isObsolete() {
		return successor != null;
	}

	public SimulationStepper getSuccessor() {
		return successor;
	}

	public Recyclable<ISimulation> getSimulation() throws IOException {
		return this.simulation.borrow(0);
	}

	public Recyclable<ISimulation> getSimulation(int day) throws IOException {
		Recyclable<ISimulation> rec = this.simulation.borrow(day);
		ISimulation simulation = rec.getItem();
		assert day <= simulation.getConfig().getRounds();
		assert simulation.getDay() <= day;
		simulation.forwardTo(day);
		assert simulation.getDay() == day;
		return rec;
	}

	public SimulationStepper refreshSimulation(String repo) throws SocketTimeoutException, IOException, NothingChangedException {
		SimulationLoader loader = SimulationStepper.this.loader;
		if (loader.usesRepository(repo)) {
			SimulationStepper newstepper = new SimulationStepper(new SimulationLoader(loader));
			System.out.println("Refreshed " + this);
			return newstepper;
		} else {
			throw new NothingChangedException();
		}
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		LogClock clock = new LogClock();
		// SimulationHandle local = new GitSimulationHandle("meisserecon", "agentecon",
		// "master");
		SimulationHandle local = new LocalSimulationHandle();
		clock.time("Created handle");
		SimulationStepper stepper = new SimulationStepper(local);
		stepper.getSimulation(100);
		stepper.getSimulation(50);
	}
	
	public Object getOrCreate(Object key, IFactory<Object> value) throws IOException {
		return cachedData.getOrSetCachedItem(key, value);
	}

	public Object getCachedItem(Object key) {
		return cachedData.get(key);
	}

	public void putCached(Object string, Object chart) {
		cachedData.put(string, chart);
	}

	@Override
	public String toString() {
		return "Simulation stepper for " + loader;
	}

}
