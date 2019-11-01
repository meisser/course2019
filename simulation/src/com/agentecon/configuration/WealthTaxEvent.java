package com.agentecon.configuration;

import java.util.Collection;

import com.agentecon.agent.IAgent;
import com.agentecon.consumer.IConsumer;
import com.agentecon.events.SimEvent;
import com.agentecon.goods.Stock;
import com.agentecon.market.IStatistics;
import com.agentecon.world.ICountry;

public class WealthTaxEvent extends SimEvent {

	private static final int STEP = 1;
	
	private double taxRate;

	public WealthTaxEvent(double rate) {
		super(1, STEP, 1);
		this.taxRate = rate * STEP;
	}

	@Override
	public void execute(int day, ICountry sim, IStatistics stats) {
		Stock temp = new Stock(sim.getMoney());
		for (IAgent a : sim.getAgents().getAgents()) {
			double wealth = a.getWealth(stats);
			double tax = wealth * taxRate;
			temp.transfer(a.getMoney(), Math.min(a.getMoney().getAmount(), tax));
		}
		distribute(sim, stats, temp);
	}

	protected void distribute(ICountry sim, IStatistics stats, Stock temp) {
		Collection<IConsumer> consumers = sim.getAgents().getConsumers();
		int alive = 0;
		for (IConsumer c: consumers) {
			if (c.isAlive()) {
				alive++;
			}
		}
		double transfer = temp.getAmount() / alive;
		for (IConsumer c : sim.getAgents().getConsumers()) {
			if (c.isAlive()) {
				c.getMoney().transfer(temp, transfer);
			}
		}
		consumers.iterator().next().getMoney().absorb(temp);
	}

}
