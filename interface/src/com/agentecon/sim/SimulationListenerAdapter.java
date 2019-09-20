// Created on Jun 23, 2015 by Luzius Meisser

package com.agentecon.sim;

import com.agentecon.agent.IAgent;
import com.agentecon.consumer.IConsumer;
import com.agentecon.firm.IFirm;
import com.agentecon.market.IMarket;
import com.agentecon.market.IStatistics;

public class SimulationListenerAdapter implements ISimulationListener {

	@Override
	public void notifyGoodsMarketOpened(IMarket market) {
	}

	@Override
	public void notifyDayStarted(int day) {
	}
	
	@Override
	public void notifyDayEnded(IStatistics stats) {
		notifyDayEnded(stats.getDay());
	}
	
	public void notifyDayEnded(int day) {
	}

	@Override
	public void notifyEvent(Event e) {
	}

	@Override
	public void notifyStockMarketOpened(IMarket market) {
	}

	@Override
	public void notifyAgentCreated(IAgent agent) {
		if (agent instanceof IConsumer){
			notifyConsumerCreated((IConsumer) agent);
		} else {
			notifyFirmCreated((IFirm) agent);
		}
	}
	
	public void notifyConsumerCreated(IConsumer consumer){
	}
	
	public void notifyFirmCreated(IFirm firm){
	}

	@Override
	public void notifyAgentDied(IAgent agent) {
		if (agent instanceof IConsumer){
			notfiyConsumerDied((IConsumer) agent);
		} else {
			notifyFirmDied((IFirm) agent);
		}
	}
	
	public void notfiyConsumerDied(IConsumer consumer) {
	}
	
	public void notifyFirmDied(IFirm firm) {
	}

}
