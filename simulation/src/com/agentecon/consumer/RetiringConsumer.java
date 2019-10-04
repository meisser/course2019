// Created by Luzius on Apr 22, 2014

package com.agentecon.consumer;

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.goods.Inventory;
import com.agentecon.market.IPriceTakerMarket;
import com.agentecon.util.MovingAverage;

public class RetiringConsumer extends BufferingMortalConsumer {
	
	private MovingAverage dailySpendings;

	public RetiringConsumer(IAgentIdGenerator id, int maxAge, Endowment end, IUtility utility) {
		super(id, maxAge, end, utility);
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
	public Inheritance considerDeath() {
		Inheritance inh = super.considerDeath();
		int age = getAge();
		if (age == getRetirementAge()) {
			listeners.notifyRetiring(this, age);
		}
		return inh;
	}
	
	@Override
	protected void trade(Inventory inv, IPriceTakerMarket market) {
		if (isRetired()) {
			inv = inv.hide(getManHours()); // cannot work any more, hide hours
		}
		super.trade(inv, market);
	}

	@Override
	public boolean isRetired() {
		return getAge() > getRetirementAge();
	}
	
	public int getRetirementAge() {
		return getMaxAge() / 5 * 4;
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
	public RetiringConsumer clone() {
		RetiringConsumer klon = (RetiringConsumer) super.clone();
		klon.dailySpendings = dailySpendings.clone();
		return klon;
	}

	@Override
	public String toString() {
		return super.toString();
	}

}
