package com.agentecon.configuration;

import com.agentecon.agent.IAgent;
import com.agentecon.agent.IAgents;
import com.agentecon.events.SimEvent;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.market.IStatistics;
import com.agentecon.production.PriceUnknownException;
import com.agentecon.world.ICountry;

public class CentralBankEvent extends SimEvent {

	private Good indexGood;

	public CentralBankEvent(Good indexGood) {
		super(1, 1, 1);
		this.indexGood = indexGood;
	}

	@Override
	public void execute(int day, ICountry sim, IStatistics stats) {
		try {
			double price = stats.getGoodsMarketStats().getPriceBelief(indexGood);
			if (price < 1.0) {
				payInterest(sim, 0.0005);
			}
		} catch (PriceUnknownException e) {
		}
	}

	private void payInterest(ICountry sim, double interestRate) {
		// TODO: pay interest to inheritances!
		IAgents agents = sim.getAgents();
		for (IAgent a: agents.getAgents()) {
			IStock money = a.getMoney();
			double interest = money.getNetAmount() * interestRate;
			money.add(interest);
		}		
	}

}
