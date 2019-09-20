package com.agentecon.firm;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.agentecon.agent.IAgent;
import com.agentecon.goods.IStock;
import com.agentecon.market.Ask;
import com.agentecon.market.Bid;
import com.agentecon.market.IPriceMakerMarket;

public interface IStockMarket extends IPriceMakerMarket, IFinancialMarketData {
	
	/**
	 * A list of stocks for which there are offers in the market.
	 * Note that for some of them their might be no open bid or no open ask.
	 */
	public default Collection<Ticker> getTradedStocks(){
		throw new RuntimeException("Not implemented");
	}
	
	public IBank getLeverageProvider();
	
	public Ticker findAnyAsk(List<Ticker> preferred, boolean marketCapWeight);
	
	/**
	 * Get the ticker of a random stock, with picking probabilities either proportionally
	 * to the market capitalization or equally weighted.
	 * 
	 * Stocks for which their is no offer available are ignored! So this is a little biased.
	 */
	public default Ticker getRandomStock(boolean marketCapWeight){
		return findAnyAsk(Collections.emptyList(), marketCapWeight);
	}
	
	public Position buy(IAgent buyer, Ticker ticker, Position existing, IStock wallet, double budget);

	/**
	 * Sell up to maxAmount of shares from the given position, putting the proceeds into
	 * the provided wallet.
	 * 
	 * Returns the actual number of shares sold.
	 */
	public double sell(IAgent seller, Position pos, IStock wallet, double maxAmount);

	public Ask getAsk(Ticker ticker);
	
	public Bid getBid(Ticker ticker);

	public boolean hasBid(Ticker ticker);

	public boolean hasAsk(Ticker ticker);	

}
