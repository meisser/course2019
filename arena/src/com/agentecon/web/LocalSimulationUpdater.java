package com.agentecon.web;

import com.agentecon.web.methods.ListMethod;

public class LocalSimulationUpdater extends Thread {
	
	private String repo;
	private ListMethod simulations;
	
	public LocalSimulationUpdater(ListMethod simulations, String repo) {
		this.simulations = simulations;
		this.setDaemon(true);
		this.repo = repo;
	}
	
	@Override
	public void run() {
		try {
			while (true) {
				Thread.sleep(5000);
				System.out.println("Checking for local changes... (doing so every 10 seconds)");
				simulations.notifyRepositoryChanged(repo);
				Thread.sleep(5000);
			}
		} catch (InterruptedException e) {
		}
	}

}
