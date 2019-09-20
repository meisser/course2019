// Created by Luzius on Apr 22, 2014

package com.agentecon.consumer;

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.firm.Portfolio;
import com.agentecon.goods.Inventory;
import com.agentecon.util.MovingAverage;

public class MortalConsumer extends Consumer {

	private int maxAge;
	private MovingAverage dailySpendings;

	public MortalConsumer(IAgentIdGenerator id, int maxAge, Endowment end, IUtility utility) {
		super(id, end, utility);
		this.maxAge = maxAge;
		this.dailySpendings = new MovingAverage(0.9);
	}
	
	public double getDailySpendings() {
		return dailySpendings.getAverage();
	}
	
	@Override
	protected void notifySpent(double spendings) {
		dailySpendings.add(spendings);
	}

	@Override
	public boolean isMortal() {
		return true;
	}

	@Override
	public Inheritance considerDeath() {
		Inheritance inh = super.considerDeath();
		assert inh == null; // super is immortal and should never return an inheritance
		int age = getAge();
		if (age == getRetirementAge()) {
			listeners.notifyRetiring(this, age);
		}
		if (age > maxAge) {
			Inventory inv = super.dispose();
			Portfolio portfolio = new Portfolio(inv.getMoney(), true);
			portfolio.absorb(getPortfolio());
			return new Inheritance(inv, portfolio);
		} else {
			return null;
		}
	}

	@Override
	public boolean isRetired() {
		return getAge() > getRetirementAge();
	}
	
	public int getMaxAge() {
		return maxAge;
	}

	public int getRetirementAge() {
		return maxAge / 5 * 4;
	}
	
	@Override
	public double consume() {
		if (isRetired()) {
			// Let retirees only enjoy half of their time.
			// Purpose: make utility difference between work age and retirement age smaller.
			getInventory().getStock(getManHours()).remove(12.0);
		}
		return super.consume();
	}

	@Override
	public MortalConsumer clone() {
		MortalConsumer klon = (MortalConsumer) super.clone();
		klon.dailySpendings = dailySpendings.clone();
		return klon;
	}

	@Override
	public String toString() {
		return super.toString();
	}

}
