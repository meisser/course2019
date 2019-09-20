// Created on Jun 1, 2015 by Luzius Meisser

package com.agentecon.sim;

import com.agentecon.agent.IAgent;
import com.agentecon.market.IMarket;
import com.agentecon.market.IStatistics;
import com.agentecon.util.AbstractListenerList;

public class SimulationListeners extends AbstractListenerList<ISimulationListener> implements ISimulationListener {
	
	@Override
	public void notifyGoodsMarketOpened(IMarket market) {
		for (ISimulationListener l: list){
			l.notifyGoodsMarketOpened(market);
		}
	}

	@Override
	public void notifyDayStarted(int day) {
		for (ISimulationListener l: list){
			l.notifyDayStarted(day);
		}
	}
	
	@Override
	public void notifyDayEnded(IStatistics stats) {
		for (ISimulationListener l: list){
			l.notifyDayEnded(stats);
		}
	}

	@Override
	public void notifyEvent(Event e) {
		for (ISimulationListener l: list){
			l.notifyEvent(e);
		}		
	}

	@Override
	public void notifyStockMarketOpened(IMarket market) {
		for (ISimulationListener l: list){
			l.notifyStockMarketOpened(market);
		}		
	}
	
	@Override
	public void notifyAgentCreated(IAgent agent) {
		for (ISimulationListener l: list){
			l.notifyAgentCreated(agent);
		}	
	}

	@Override
	public void notifyAgentDied(IAgent agent) {
		for (ISimulationListener l: list){
			l.notifyAgentDied(agent);
		}	
	}
	
}
