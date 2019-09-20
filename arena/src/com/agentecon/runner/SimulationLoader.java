// Created on May 29, 2015 by Luzius Meisser

package com.agentecon.runner;

import java.io.IOException;
import java.net.SocketTimeoutException;

import com.agentecon.ISimulation;
import com.agentecon.classloader.CompilingClassLoader;
import com.agentecon.classloader.RemoteLoader;
import com.agentecon.classloader.SimulationHandle;

public class SimulationLoader {

	public static final String SIM_CLASS = "com.agentecon.Simulation";

	private SimulationHandle handle;
	private RemoteLoader classLoader;

	public SimulationLoader(SimulationHandle handle) throws SocketTimeoutException, IOException {
		this.handle = handle;
		this.classLoader = new CompilingClassLoader(handle);
	}

	/**
	 * Creates a new simulation loader that recycles the classes that are still up to date and do not need to be reloaded.
	 * 
	 * @throws NothingChangedException
	 */
	public SimulationLoader(SimulationLoader toRecycle) throws SocketTimeoutException, IOException, NothingChangedException {
		this.handle = toRecycle.handle;
		RemoteLoader remote = toRecycle.classLoader;
		if (remote.isUptoDate()) {
			if (remote.refreshSubloaders()) {
				this.classLoader = remote;
			} else {
				throw new NothingChangedException();
			}
		} else {
			// Do not recycle the sub-loaders, as their loaded classes still
			// refer to superclasses from the old simulation loader
			this.classLoader = new CompilingClassLoader(handle);
		}
	}

	public boolean usesRepository(String repo) {
		return classLoader.usesRepository(repo);
	}

	@SuppressWarnings("unchecked")
	public Class<? extends ISimulation> loadSimClass() {
		try {
			return (Class<? extends ISimulation>) classLoader.loadClass(SIM_CLASS);
		} catch (ClassNotFoundException e) {
			throw new java.lang.RuntimeException(e);
		}
	}

	public ISimulation loadSimulation() throws IOException {
		try {
			return (ISimulation) loadSimClass().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new IOException("Failed to load simulation", e);
		}
	}

}
