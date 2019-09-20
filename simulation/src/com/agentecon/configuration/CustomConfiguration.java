package com.agentecon.configuration;

import java.io.IOException;
import java.io.PrintStream;
import java.net.SocketTimeoutException;
import java.util.Collection;

import com.agentecon.ISimulation;
import com.agentecon.classloader.RemoteLoader;
import com.agentecon.classloader.SimulationHandle;
import com.agentecon.goods.Good;
import com.agentecon.research.IInnovation;
import com.agentecon.sim.Event;
import com.agentecon.sim.SimulationConfig;

public class CustomConfiguration extends SimulationConfig {

	private SimulationConfig delegate;

	public CustomConfiguration() throws IOException {
		this("com.agentecon.exercise3.MoneyConfiguration2");
	}

	public CustomConfiguration(String classname) throws IOException {
		this((RemoteLoader) CustomConfiguration.class.getClassLoader(), classname);
	}

	public CustomConfiguration(RemoteLoader parent, String className) throws IOException {
		super(10000);
		try {
			// use same source as parent
			SimulationHandle handle = parent.getSource().copy(false);
			ClassLoader loader = findLoader(parent, handle);
			delegate = (SimulationConfig) loader.loadClass(className).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	protected ClassLoader findLoader(RemoteLoader parent, SimulationHandle handle) throws SocketTimeoutException, IOException {
		return parent.obtainChildLoader(handle);
	}

	@Override
	public Good getMoney() {
		return delegate.getMoney();
	}

	@Override
	public long getSeed() {
		return delegate.getSeed();
	}

	@Override
	public Collection<Event> getEvents() {
		return delegate.getEvents();
	}

	@Override
	public void addEvent(Event e) {
		delegate.addEvent(e);
	}
	
	@Override
	public int getMaxAge() {
		return delegate.getMaxAge();
	}

	@Override
	public int getRounds() {
		return delegate.getRounds();
	}

	@Override
	public int getIntradayIterations() {
		return delegate.getIntradayIterations();
	}

	@Override
	public IInnovation getInnovation() {
		return delegate.getInnovation();
	}

	@Override
	public String getName() {
		return delegate.getName();
	}

	@Override
	public void diagnoseResult(PrintStream out, ISimulation stats) {
		delegate.diagnoseResult(out, stats);
	}

	@Override
	public double getCurrentDiscountRate() {
		return delegate.getCurrentDiscountRate();
	}

}
