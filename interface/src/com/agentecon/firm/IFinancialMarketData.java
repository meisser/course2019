package com.agentecon.firm;

import com.agentecon.market.IMarketStatistics;

public interface IFinancialMarketData {
	
	public IMarketStatistics getMarketStatistics();
	
	public default FirmFinancials getFirmData(Ticker ticker){
		throw new RuntimeException("Not implemented");
	}

	public default boolean hasData(Ticker t) {
		return true;
	}

}
