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

import com.agentecon.ISimulation;
import com.agentecon.classloader.LocalSimulationHandle;
import com.agentecon.classloader.SimulationHandle;
import com.agentecon.runner.Recyclable;
import com.agentecon.runner.SimulationStepper;

public abstract class SimSpecificMethod extends WebApiMethod {
	
	private transient ListMethod sims;
	
	public SimSpecificMethod(ListMethod listing){
		this.sims = listing;
	}
	
	@Override
	protected String createExamplePath() {
		return super.createExamplePath() + "?" + Parameters.SIM + "=" + new LocalSimulationHandle().getBranch() + "&" + Parameters.DAY + "=313";
	}
	
	protected SimulationHandle getHandle(Parameters params){
		return sims.getHandle(params.getSimulation());
	}
	
	protected SimulationStepper getSimulation(Parameters params) throws IOException{
		return sims.getSimulation(params.getSimulation());
	}

	public Recyclable<ISimulation> getSimulation(Parameters tok, int day) throws IOException{
		SimulationStepper stepper = getSimulation(tok);
		return stepper.getSimulation(day);
	}
	
}
