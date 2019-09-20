// Created by Luzius on Apr 28, 2014

package com.agentecon.market;

import com.agentecon.agent.IAgent;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Quantity;
import com.agentecon.util.Numbers;

public abstract class AbstractOffer implements Comparable<AbstractOffer>, IOffer {

	private IAgent owner;
	protected IStock wallet;
	protected IStock stock;
	private Price price;

	protected IMarketListener listener = NULL_LISTENER;
	private double volume;
	private double quantity;

	private static final IMarketListener NULL_LISTENER = new IMarketListener() {

		@Override
		public void notifyTraded(IAgent seller, IAgent buyer, Good good, double quantity, double payment) {
		}

		@Override
		public void notifyTradesCancelled() {
		}

		@Override
		public void notifyMarketClosed(int day) {
		}
	};

	public AbstractOffer(IAgent initator, IStock wallet, IStock stock, Price price, double quantity) {
		this.owner = initator;
		this.wallet = wallet;
		this.stock = stock;
		this.price = price;
		this.volume = 0.0;
		this.quantity = quantity;
		assert stock.getGood() == price.getGood();
		assert price.getPrice() >= Numbers.EPSILON;
	}

	public double getTransactionVolume() {
		return volume;
	}

	public void setListener(IMarketListener listener) {
		this.listener = listener == null ? NULL_LISTENER : listener;
	}

	public void transfer(IAgent counterParty, IStock sourceWallet, double moneyFlow, IStock target, double goodsFlow) {
		if (goodsFlow != 0.0) {
			wallet.transfer(sourceWallet, moneyFlow);
			stock.transfer(target, goodsFlow);
			doStats(counterParty, moneyFlow, goodsFlow);
		}
	}

	private void doStats(IAgent counterParty, double moneyFlow, double goodsFlow) {
		reduceOffer(moneyFlow, goodsFlow);
		if (moneyFlow >= 0) {
			assert goodsFlow <= 0;
			// we receive money and give goods
			listener.notifyTraded(owner, counterParty, getGood(), -goodsFlow, moneyFlow);
		} else {
			assert goodsFlow > 0;
			listener.notifyTraded(counterParty, owner, getGood(), goodsFlow, -moneyFlow);
		}
	}
	
	protected void reduceOffer(double moneyFlow, double goodsFlow) {
		this.volume += Math.abs(moneyFlow);
		this.quantity -= Math.abs(goodsFlow);
	}
	
	public Quantity getQuantity(){
		return new Quantity(getGood(), quantity);
	}

	public double getAmount() {
		return quantity;
	}

	public Price getPrice() {
		return price;
	}

	public Good getGood() {
		return stock.getGood();
	}

	public IAgent getOwner() {
		return owner;
	}

	public final boolean isUsed() {
		return getAmount() == 0.0;
	}

	public abstract double accept(IAgent acceptingAgent, IStock source, IStock target, Quantity amount);

	public int compareTo(AbstractOffer o) {
		return price.compareTo(o.price);
	}

	@Override
	public String toString() {
		String s = (isBid() ? "Buying " : "Selling ") + price.toString();
		if (isUsed()) {
			s += " (filled)";
		}
		return s;
	}

}
