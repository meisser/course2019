// Created by Luzius on Apr 28, 2014

package com.agentecon.market;

import com.agentecon.agent.IAgent;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Quantity;

public class Ask extends AbstractOffer {
	
	public Ask(IAgent initiator, IStock wallet, IStock stock, double price) {
		this(initiator, wallet, stock, new Price(stock.getGood(), price), stock.getAmount());
	}

	public Ask(IAgent initiator, IStock wallet, IStock stock, Price price, double amount) {
		super(initiator, wallet, stock, price, amount);
		assert amount <= stock.getAmount();
	}

	@Override
	public double getAmount() {
		// allow to make offers whose stock only gets filled over the course of trading
		return Math.min(stock.getAmount(), super.getAmount());
	}
	
	@Override
	public double accept(IAgent acceptingAgent, IStock payer, IStock target, Quantity targetAmount) {
		assert target.getGood().equals(targetAmount.getGood());
		double amount = Math.min(getAmount(), targetAmount.getAmount());
		double price = getPrice().getPrice();
		double total = amount * price;
		if (total > payer.getAmount()) {
			amount = payer.getAmount() / price;
			total = payer.getAmount();
		}
		assert amount >= 0;
		transfer(acceptingAgent, payer, total, target, -amount);
		assert getAmount() <= stock.getAmount();
		return amount;
	}

	@Override
	public final boolean isBid() {
		return false;
	}

	@Override
	public IOffer getBetterOne(IOffer other) {
		return getPrice().isAbove(other.getPrice()) ? other : this;
	}

	public void match(Bid bid) {
		if (!getPrice().isAbove(bid.getPrice())){
			double moneyBefore = wallet.getAmount();
			double amount = bid.accept(getOwner(), wallet, stock, getQuantity());
			double moneyReceived = wallet.getAmount() - moneyBefore;
			super.reduceOffer(moneyReceived, amount);
		}
	}

}
