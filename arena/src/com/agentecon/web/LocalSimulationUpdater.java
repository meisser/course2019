package com.agentecon.web;

import com.agentecon.classloader.LocalSimulationHandle;
import com.agentecon.web.methods.ListMethod;

public class LocalSimulationUpdater extends Thread {
	
	private ListMethod simulations;
	
	public LocalSimulationUpdater(ListMethod simulations) {
		this.simulations = simulations;
		this.setDaemon(true);
	}
	
	@Override
	public void run() {
		try {
			while (true) {
				Thread.sleep(5000);
				System.out.println("Checking for local changes... (doing so every 10 seconds)");
				simulations.notifyRepositoryChanged(LocalSimulationHandle.REPO_NAME);
				Thread.sleep(5000);
			}
		} catch (InterruptedException e) {
		}
	}

}
