package com.agentecon.finance;

import com.agentecon.firm.FirmFinancials;
import com.agentecon.firm.IFinancialMarketData;
import com.agentecon.firm.Ticker;
import com.agentecon.market.IMarketStatistics;
import com.agentecon.market.IStatistics;
import com.agentecon.world.Agents;

public class FinancialMarketData implements IFinancialMarketData {
	
	private Agents ags;
	private IStatistics stats;

	public FinancialMarketData(Agents ags, IStatistics stats) {
		this.ags = ags;
		this.stats = stats;
	}
	
	@Override
	public FirmFinancials getFirmData(Ticker ticker) {
		Firm firm = (Firm) ags.getAgent(ticker.getNumer());
		return new FirmFinancials(firm, stats);
	}
	
	@Override
	public boolean hasData(Ticker t) {
		return ags.getAgent(t.getNumer()) != null;
	}

	@Override
	public IMarketStatistics getMarketStatistics() {
		return stats.getStockMarketStats();
	}

}
