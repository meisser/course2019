package com.agentecon.events;

import com.agentecon.consumer.IConsumer;
import com.agentecon.goods.IStock;
import com.agentecon.world.ICountry;

public class MoneyPrintEvent extends SimEvent {

	private double amount;

	public MoneyPrintEvent(int step, int card, double amount) {
		super(step, card);
		this.amount = amount;
	}

	@Override
	public void execute(int day, ICountry sim) {
		for (IConsumer c : sim.getAgents().getRandomConsumers(getCardinality())) {
			IStock money = c.getMoney();
			if (amount > 0) {
				money.add(amount);
			} else if (money.getAmount() >= -amount){
				money.remove(-amount);
			} else {
				System.out.println("Can only steal " + money.getAmount() + " from " + c);
				money.consume();
			}
		}
	}

}
