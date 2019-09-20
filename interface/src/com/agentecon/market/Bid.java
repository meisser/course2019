// Created by Luzius on Apr 28, 2014

package com.agentecon.market;

import com.agentecon.agent.IAgent;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Quantity;
import com.agentecon.util.Numbers;

public class Bid extends AbstractOffer {
	
	public Bid(IAgent initiator, IStock wallet, IStock stock, double price, double quantity){
		this(initiator, wallet, stock, new Price(stock.getGood(), price), quantity);
	}
	
	public Bid(IAgent initiator, IStock wallet, IStock stock, Price price, double quantity){
		super(initiator, wallet, stock, price, quantity);
		assert wallet.getAmount() - getAmount() * getPrice().getPrice() >= -Numbers.EPSILON;
//		assert quantity > 0;
	}
	
	@Override
	public double getAmount(){
		// in case money got removed from wallet elsewhere
		return Math.min(super.getAmount(), wallet.getAmount() / getPrice().getPrice());
	}
	
	@Override
	public double accept(IAgent acceptingAgent, IStock seller, IStock sellerStock, Quantity targetAmount){
		assert sellerStock.getGood().equals(targetAmount.getGood());
//		assert sellerStock.getAmount() >= targetAmount.getAmount(); TEMP
		double amount = Math.min(targetAmount.getAmount(), getAmount());
		amount = Math.min(amount, sellerStock.getAmount());
		assert amount >= 0;
		double total = amount * getPrice().getPrice();
		transfer(acceptingAgent, seller, -total, sellerStock, amount);
		return amount;
	}
	
	@Override
	public int compareTo(AbstractOffer o) {
		return -super.compareTo(o);
	}

	@Override
	public final boolean isBid() {
		return true;
	}
	
	@Override
	public IOffer getBetterOne(IOffer other) {
		return getPrice().isAbove(other.getPrice()) ? this : other;
	}
	
	public void match(Ask ask) {
		if (!ask.getPrice().isAbove(getPrice())){
			double moneyBefore = wallet.getAmount();
			double amount = ask.accept(getOwner(), wallet, stock, getQuantity());
			assert amount >= 0;
			double moneySpent = moneyBefore - wallet.getAmount();
			super.reduceOffer(moneySpent, amount);
		}
	}

}
