package com.agentecon.finance.stockpicking;

import java.util.Collection;

import com.agentecon.firm.IStockMarket;
import com.agentecon.firm.Ticker;

public interface IStockPickingStrategy {

	public default Ticker findStockToBuy(IStockMarket stocks) {
		return null;
	}
	
	public default Ticker findStockToSell(Collection<Ticker> whatWeHave, IStockMarket stocks) {
		return null;
	}

}
