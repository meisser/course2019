package com.agentecon.fund;

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.firm.IStockMarket;
import com.agentecon.firm.Ticker;
import com.agentecon.fund.HistoricalMarketData.HistoricalStockData;

public class MarketDataExample extends InvestmentFund {
	
	private HistoricalMarketData marketData = new HistoricalMarketData();
	
	public MarketDataExample(IAgentIdGenerator world, Endowment end) {
		super(world, end);
	}
	
	@Override
	public void managePortfolio(IStockMarket dsm) {
//		if (!leverageEnabled && dsm.getLeverageProvider() != null) {
//			getPortfolio().enableLeverage(this, dsm.getLeverageProvider());
//			leverageEnabled = true;
//		}
//		portfolio.invest(new HighestYieldPickingStrategy(dsm.getLeverageProvider().getInterestRate()), dsm, this, portfolio.getAvailableBudget() * 0.05);
//		portfolio.sell(dsm, this, 0.005);
		
		marketData.update(dsm); // update the data once per day
		
		Ticker ticker = null; // some ticker, set to a real value.
		double priceFiveDaysAgo = marketData.getData(ticker).getSeries().get(5);
		
		HistoricalStockData stockData = marketData.getData(ticker);
		if (stockData.getShortTermAverage() > stockData.getLongTermAverage()) {
			// stock has been going upwards, if we want to do trend following, we should buy it
		}
	}
	
}
