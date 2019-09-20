// Created on Jun 23, 2015 by Luzius Meisser

package com.agentecon.market;

import com.agentecon.agent.IAgent;
import com.agentecon.firm.IStockMarket;
import com.agentecon.goods.Good;

public interface IMarketListener {
	
	public default void notifyOffersPosted(IStockMarket market) {
	}
	
	public default void notifyTraded(IAgent seller, IAgent buyer, Good good, double quantity, double payment) {
	}

	public default void notifyTradesCancelled() {
	}
	
	@Deprecated
	public default void notifyMarketClosed(int day) {
	}
	
	public default void notifyMarketClosed(int day, IPriceTakerMarket market) {
		notifyMarketClosed(day);
	}

}
