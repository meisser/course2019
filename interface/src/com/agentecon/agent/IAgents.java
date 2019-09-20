package com.agentecon.agent;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.Inheritance;
import com.agentecon.firm.IBank;
import com.agentecon.firm.IFirm;
import com.agentecon.firm.IShareholder;
import com.agentecon.firm.Ticker;
import com.agentecon.production.IGoodsTrader;

public interface IAgents {
	
public Collection<? extends IAgent> getAgents();

	public Collection<? extends IConsumer> getConsumers();
	
	public Collection<? extends IFirm> getFirms();
	
	public Collection<? extends IGoodsTrader> getRandomGoodsMarketMakers();
	
	public Collection<? extends IShareholder> getShareholders();
	
	public default Collection<? extends IBank> getBanks(){
		return Collections.emptyList();
	}
	
	public IFirm getFirm(Ticker ticker);

	public IAgent getAgent(int agentId);

	public Set<String> getFirmTypes();
	
	public Set<String> getConsumerTypes();

	public Collection<IAgent> getAgents(String type);

	public default Collection<Inheritance> getPendingInheritances(){
		return Collections.emptyList();
	}

}
