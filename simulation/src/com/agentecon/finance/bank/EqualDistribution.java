package com.agentecon.finance.bank;

import java.util.Collection;

import com.agentecon.consumer.IConsumer;
import com.agentecon.goods.IStock;

public class EqualDistribution implements IDistributionPolicy {
	
	private double lastDistribution;

	@Override
	public void distribute(IStock wallet, Collection<IConsumer> consumers) {
		this.lastDistribution = wallet.getAmount();
		double distributionPerConsumer = wallet.getAmount() / consumers.size();
		for (IConsumer c : consumers) {
			assert c.isAlive();
			c.receiveInterest(wallet, distributionPerConsumer);
		}
	}
	
	@Override
	public String toString() {
		return "Equal distribution policy, distributed " + lastDistribution;
	}

}
