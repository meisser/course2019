package com.agentecon.web;

import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.agentecon.web.methods.Parameters;
import com.agentecon.web.methods.WebApiMethod;

public class Refresher {

	private int cacheSize;
	private LinkedList<Request> requests;
	private Executor updateExecutor;

	public Refresher(int cacheSize) {
		this.cacheSize = cacheSize;
		this.requests = new LinkedList<>();
		this.updateExecutor = Executors.newFixedThreadPool(3);
	}

	public synchronized void notifyCalled(WebApiMethod calledMethod, Parameters parameters) {
		requests.addFirst(new Request(calledMethod, parameters));
		if (requests.size() > cacheSize) {
			requests.removeLast();
		}
	}

	public synchronized void notifySimulationUpdated(String identifier) {
		for (Request r : requests) {
			if (identifier.equals(r.params.getSimulation())) {
				updateExecutor.execute(new Runnable() {
					@Override
					public void run() {
						try {
							long t0 = System.nanoTime();
							r.method.refreshCache(r.params);
							long t1 = System.nanoTime();
							System.out.println("Refreshed call to " + r.method.getName() + " with " + r.params + " in " + (t1-t0)/1000000 + "ms");
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
						}
					}
				});
			}
		}
	}

	class Request {

		WebApiMethod method;
		Parameters params;

		public Request(WebApiMethod method, Parameters params) {
			this.method = method;
			this.params = params;
		}

	}

}
