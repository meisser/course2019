package com.agentecon.fund;

import com.agentecon.finance.stockpicking.IStockPickingStrategy;
import com.agentecon.firm.IStockMarket;
import com.agentecon.firm.Ticker;
import com.agentecon.market.Ask;

public class CustomStockPickingStrategy implements IStockPickingStrategy {
	
	private static final double MAX_REASONABLE_PRICE = 500000;

	private double minYield;

	public CustomStockPickingStrategy() {
		this(-1.0);
	}

	public CustomStockPickingStrategy(double minYield) {
		this.minYield = minYield;
	}

	@Override
	public Ticker findStockToBuy(IStockMarket stocks) {
		Ticker highest = null;
		double highestYield = 0.0;
		for (Ticker t : stocks.getTradedStocks()) {
			Ask ask = stocks.getAsk(t);
			if (ask != null) {
				double price = ask.getPrice().getPrice();
				if (price < MAX_REASONABLE_PRICE) {
					double dividend = stocks.getFirmData(t).getDailyDividendPerShare();
					double yield = dividend / price;
					if (yield > highestYield) {
						highestYield = yield;
						highest = t;
					}
				}
			}
		}
		if (highestYield > minYield) {
			return highest;
		} else {
			return null;
		}
	}

}
