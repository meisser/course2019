package com.agentecon.finance.bank;

import java.util.Collection;

import com.agentecon.consumer.IConsumer;
import com.agentecon.goods.IStock;

public class InterestDistribution extends EqualDistribution {
	
	private double latestDistributionAmount;
	private double latestWealthBase;

	@Override
	public void distribute(IStock wallet, Collection<IConsumer> consumers) {
		double available = wallet.getAmount();
		this.latestDistributionAmount = available;
		double totalMoney = calculateTotalMoney(consumers);
		this.latestWealthBase = totalMoney;
		if (totalMoney > 0.0) {
			for (IConsumer c : consumers) {
				assert c.isAlive();
				IStock consumerWallet = c.getMoney();
				double distribution = consumerWallet.getAmount() / totalMoney * available;
				consumerWallet.transfer(wallet, distribution);
			}
		} else {
			// if everyone has 0 wealth, distribute evenly
			super.distribute(wallet, consumers);
		}
	}

	private double calculateTotalMoney(Collection<IConsumer> consumers) {
		double total = 0.0;
		for (IConsumer consumer : consumers) {
			total += consumer.getMoney().getAmount();
		}
		return total;
	}
	
	public double getImpliedInterest() {
		return latestDistributionAmount / latestWealthBase;
	}
	
	@Override
	public String toString() {
		return "Distributed " + latestDistributionAmount + " on " + latestWealthBase + " which implies an interest of " + getImpliedInterest();
	}

}
