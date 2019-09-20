// Created on May 22, 2015 by Luzius Meisser

package com.agentecon.firm;

import com.agentecon.goods.Good;
import com.agentecon.goods.Quantity;
import com.agentecon.goods.Stock;
import com.agentecon.market.IOffer;
import com.agentecon.market.Market;
import com.agentecon.market.Price;

public class TestConsumer {

	private static final double INFINITE = 1000000; // :)

	private Price[] prices;
	private Good money;

	public TestConsumer(Good money, Price... prices) {
		this.prices = prices;
		this.money = money;
	}

	public void buyAndSell(Market market) {
		for (Price value : prices) {
			IOffer ask = market.getAsk(value.getGood());
			while (ask != null && value.isAbove(ask.getPrice())) {
				ask.accept(null, getWallet(), getStock(ask.getGood()), new Quantity(ask.getGood(), INFINITE));
				ask = market.getAsk(value.getGood());
			}
			IOffer bid = market.getBid(value.getGood());
			while (bid != null && bid.getPrice().isAbove(value)) {
				bid.accept(null, getWallet(), getStock(bid.getGood()), new Quantity(bid.getGood(), INFINITE));
				bid = market.getBid(value.getGood());
			}
		}
	}

	private Stock getStock(Good good) {
		return new Stock(good, INFINITE);
	}

	private Stock getWallet() {
		return new Stock(money, INFINITE);
	}

	public boolean checkPrices(Market market, double accuracy) {
		boolean ok = true;
		for (Price p : prices) {
			Price mp = market.getPrice(p.getGood());
			ok &= mp.equals(p, accuracy);
		}
		return ok;
	}

	public double getPriceSquareError(Market market) {
		double tot = 0.0;
		for (Price p : prices) {
			Price mp = market.getPrice(p.getGood());
			if (mp == null) {
				return Double.POSITIVE_INFINITY;
			} else {
				double diff = mp.getPrice() - p.getPrice();
				tot += diff * diff;
			}
		}
		return tot;
	}

}
