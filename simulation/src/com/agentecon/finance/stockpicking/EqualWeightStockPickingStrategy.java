package com.agentecon.finance.stockpicking;

import com.agentecon.firm.IStockMarket;
import com.agentecon.firm.Ticker;

public class EqualWeightStockPickingStrategy implements IStockPickingStrategy {

	@Override
	public Ticker findStockToBuy(IStockMarket stocks) {
		return stocks.getRandomStock(false);
	}

}
