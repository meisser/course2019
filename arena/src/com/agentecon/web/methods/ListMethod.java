/**
 * Created by Luzius Meisser on Jun 15, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.web.methods;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

import com.agentecon.classloader.SimulationHandle;
import com.agentecon.runner.NothingChangedException;
import com.agentecon.runner.SimulationLoader;
import com.agentecon.runner.SimulationStepper;
import com.agentecon.web.Refresher;
import com.agentecon.web.data.JsonData;

public class ListMethod extends WebApiMethod {

	private transient Refresher updateListener;
	private transient HashMap<String, SimulationHandle> handles;
	private transient ArrayList<String> simulationNamesInCorrectOrder;
	private transient HashMap<SimulationHandle, SimulationStepper> simulations;
	private transient Executor simulationUpdateExecutor;

	public ListMethod(Refresher updateListener) {
		this.handles = new HashMap<>();
		this.simulations = new HashMap<>();
		this.updateListener = updateListener;
		this.simulationNamesInCorrectOrder = new ArrayList<>();
		this.simulationUpdateExecutor = Executors.newSingleThreadExecutor();
	}

	public Collection<String> getSimulations() {
		return handles.keySet();
	}

	public void add(SimulationHandle handle) {
		this.simulationNamesInCorrectOrder.add(handle.getIdentifier());
		this.handles.put(handle.getIdentifier(), handle);
	}

	protected void update(SimulationHandle handle, SimulationStepper stepper) {
		synchronized (this) {
			simulations.put(handle, stepper);
		}
		updateListener.notifySimulationUpdated(handle.getIdentifier());
	}

	public synchronized void notifyRepositoryChanged(String repo) {
		simulations.forEach(new BiConsumer<SimulationHandle, SimulationStepper>() {

			@Override
			public void accept(SimulationHandle t, SimulationStepper u) {
				simulationUpdateExecutor.execute(new Runnable() {

					@Override
					public void run() {
						try {
							try {
								update(t, u.refreshSimulation(repo));
							} catch (SocketTimeoutException e) {
								System.out.println("Pausing simulation updates for a minute due to " + e.toString());
								Thread.sleep(60000);
								simulationUpdateExecutor.execute(this);
							} catch (IOException e) {
								e.printStackTrace();
							} catch (NothingChangedException e) {
								// good, no need to update
							}
						} catch (InterruptedException e) {
						}
					}
				});
			}
		});
	}

	public synchronized SimulationStepper getSimulation(SimulationHandle handle) throws IOException {
		SimulationStepper stepper = this.simulations.get(handle);
		if (stepper == null) {
			stepper = new SimulationStepper(handle);
			simulations.put(handle, stepper);
		} else if (stepper.isObsolete()) {
			stepper = stepper.getSuccessor();
			simulations.put(handle, stepper);
		}
		return stepper;
	}

	public SimulationStepper getSimulation(String name) throws IOException {
		SimulationHandle handle = getHandle(name);
		return getSimulation(handle);
	}

	public SimulationHandle getHandle(String name) {
		return handles.get(SimulationHandle.toIdentifier(name));
	}

	@Override
	public JsonData getJsonAnswer(Parameters params) {
		return new SimulationList(simulationNamesInCorrectOrder, handles);
	}

	class SimulationList extends JsonData {

		public Collection<SimulationInfo> sims = new ArrayList<>();

		public SimulationList(ArrayList<String> simulationNamesInCorrectOrder, HashMap<String, SimulationHandle> handles) {
			for (String name : simulationNamesInCorrectOrder) {
				this.sims.add(new SimulationInfo(handles.get(name)));
			}
		}
	}

	class SimulationInfo {

		public String owner;
		public String path;
		public String sourceUrl;

		public SimulationInfo(SimulationHandle handle) {
			this.owner = handle.getOwner();
			this.path = handle.getBranch();
			this.sourceUrl = handle.getBrowsableURL(SimulationLoader.SIM_CLASS).toExternalForm();
		}

	}
}
