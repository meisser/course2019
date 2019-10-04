package com.agentecon.finance.bank;

import java.util.Collection;

import com.agentecon.consumer.IConsumer;
import com.agentecon.goods.IStock;

public enum EDistributionMode {

	EQUAL, INTEREST;

	public void distribute(IStock wallet, Collection<IConsumer> consumers) {
		switch (this) {
		case EQUAL:
			distributeEqually(wallet, consumers);
			return;
		case INTEREST:
			distributeByWealth(wallet, consumers);
			return;
		}
	}

	private void distributeByWealth(IStock wallet, Collection<IConsumer> consumers) {
		double available = wallet.getAmount();
		double totalMoney = calculateTotalMoney(consumers);
		if (totalMoney > 0.0) {
			for (IConsumer c : consumers) {
				assert c.isAlive();
				IStock consumerWallet = c.getMoney();
				double distribution = consumerWallet.getAmount() / totalMoney * available;
				consumerWallet.transfer(wallet, distribution);
			}
		} else {
			distributeEqually(wallet, consumers);
		}
	}

	private double calculateTotalMoney(Collection<IConsumer> consumers) {
		double total = 0.0;
		for (IConsumer consumer : consumers) {
			total += consumer.getMoney().getAmount();
		}
		return total;
	}

	private void distributeEqually(IStock wallet, Collection<IConsumer> consumers) {
		double distributionPerConsumer = wallet.getAmount() / consumers.size();
		for (IConsumer c : consumers) {
			assert c.isAlive();
			c.getMoney().transfer(wallet, distributionPerConsumer);
		}
	}

}
