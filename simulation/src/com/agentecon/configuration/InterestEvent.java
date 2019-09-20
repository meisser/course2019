package com.agentecon.configuration;

import com.agentecon.agent.IAgent;
import com.agentecon.agent.IAgents;
import com.agentecon.events.SimEvent;
import com.agentecon.goods.IStock;
import com.agentecon.world.ICountry;

public class InterestEvent extends SimEvent {

	private double interestRate;
	
	public InterestEvent(double interestRate, int interval) {
		super(1, interval, 1);
		this.interestRate = interestRate;
	}

	@Override
	public void execute(int day, ICountry sim) {
		IAgents agents = sim.getAgents();
		for (IAgent a: agents.getAgents()) {
			IStock money = a.getMoney();
			double interest = money.getAmount() * interestRate;
			money.add(interest);
		}
	}

}
