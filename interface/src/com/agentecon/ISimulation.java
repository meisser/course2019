// Created on May 28, 2015 by Luzius Meisser

package com.agentecon;

import com.agentecon.agent.IAgents;
import com.agentecon.market.IStatistics;
import com.agentecon.sim.ISimulationListener;
import com.agentecon.sim.SimulationConfig;


/**
 * The interface
 * 
 */
public interface ISimulation {
	
	public int getDay();
	
	public IStatistics getStatistics();
	
	public SimulationConfig getConfig();
	
	public IAgents getAgents();
	
	public void addListener(ISimulationListener listener);
	
	public void removeListener(ISimulationListener listener);
	
	/**
	 * Runs the simulation up to the start of the provided day.
	 * This method can only go forward in time. This method has no effect
	 * when the provided day is in the past. 
	 */
	public void forwardTo(int day);
	
	public boolean isFinished();
	
	public void run();
	
}
