package com.agentecon.market;

import java.io.PrintStream;
import java.util.Collection;

import com.agentecon.firm.Ticker;
import com.agentecon.goods.Good;
import com.agentecon.production.IPriceProvider;

public interface IMarketStatistics extends IPriceProvider {
	
	public Collection<Good> getTradedGoods();
	
	public GoodStats getStats(Good good);
	
	public default FirmStats getFirmStats(Ticker ticker) {
		return new FirmStats();
	}

	public void print(PrintStream out);

	public double getPriceIndex();

	public double getLatestPrice(Ticker ticker);
	
}
