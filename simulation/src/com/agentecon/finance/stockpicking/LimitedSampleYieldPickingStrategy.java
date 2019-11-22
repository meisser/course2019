package com.agentecon.finance.stockpicking;

import com.agentecon.firm.IStockMarket;
import com.agentecon.firm.Ticker;
import com.agentecon.market.Ask;

public class LimitedSampleYieldPickingStrategy implements IStockPickingStrategy {

	private int samples;

	public LimitedSampleYieldPickingStrategy(int samples) {
		this.samples = samples;
	}

	@Override
	public Ticker findStockToBuy(IStockMarket stocks) {
		Ticker highest = null;
		double highestYield = 0.0;
		for (int i = 0; i < samples; i++) {
			Ask ask = stocks.getRandomAsk();
			if (ask == null) {
				return null;
			} else {
				Ticker t = (Ticker) ask.getGood();
				double price = ask.getPrice().getPrice();
				double dividend = stocks.getFirmData(t).getDailyDividendPerShare();
				double yield = dividend / price;
				if (yield > highestYield) {
					highestYield = yield;
					highest = t;
				}
			}
		}
		return highest;
	}

}
