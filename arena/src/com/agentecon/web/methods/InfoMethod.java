/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.web.methods;

import java.io.IOException;

import com.agentecon.ISimulation;
import com.agentecon.classloader.SimulationHandle;
import com.agentecon.runner.Recyclable;
import com.agentecon.runner.SimulationLoader;
import com.agentecon.sim.SimulationConfig;
import com.agentecon.web.data.JsonData;

public class InfoMethod extends SimSpecificMethod {

	public InfoMethod(ListMethod listing) {
		super(listing);
	}

	@Override
	protected JsonData getJsonAnswer(Parameters params) throws IOException {
		SimulationHandle handle = getHandle(params);
		Recyclable<ISimulation> sim = getSimulation(params).getSimulation();
		try {
			return new Info(handle, sim.getItem().getConfig());
		} finally {
			sim.recycle();
		}
	}

	class Info extends JsonData {

		public String name;
		public String simulationSourceURL;

		public String configurationName;
		public String configurationSourceURL;

		public int days;

		public Info(SimulationHandle handle, SimulationConfig config) {
			this.name = handle.getBranch();
			this.simulationSourceURL = handle.getBrowsableURL(SimulationLoader.SIM_CLASS).toExternalForm();

			this.configurationName = config.getName();
			this.configurationSourceURL = handle.getBrowsableURL(config.getClass().getName()).toExternalForm();
			this.days = config.getRounds();
		}

	}

}
