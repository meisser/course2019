package com.agentecon.consumer;

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.firm.Portfolio;
import com.agentecon.goods.Inventory;

public class MortalConsumer extends Consumer {

	private int maxAge;

	public MortalConsumer(IAgentIdGenerator id, int maxAge, Endowment end, IUtility utility) {
		super(id, end, utility);
		this.maxAge = maxAge;
	}
	
	public int getMaxAge() {
		return maxAge;
	}

	@Override
	public boolean isMortal() {
		return true;
	}
	
	@Override
	public Inheritance considerDeath() {
		Inheritance inh = super.considerDeath();
		assert inh == null; // super is immortal and should never return an inheritance
		if (getAge() > maxAge) {
			Inventory inv = super.dispose();
			Portfolio portfolio = new Portfolio(inv.getMoney(), true);
			portfolio.absorb(getPortfolio());
			return new Inheritance(inv, portfolio);
		} else {
			return null;
		}
	}

}