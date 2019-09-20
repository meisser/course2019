package com.agentecon.firm;

import com.agentecon.goods.Inventory;

public interface IShareholder {
	
	public Portfolio getPortfolio();
	
	public Inventory getInventory();
	
	/**
	 * This is the time to trade stocks on the stock market.
	 * The stock market opens before the goods market, but after new firms have been founded
	 * and after dividends have been distributed.
	 */
	public void managePortfolio(IStockMarket dsm);
	
	public default double notifyFirmClosed(Ticker ticker) {
		return getPortfolio().notifyFirmClosed(ticker);
	}
	
}
