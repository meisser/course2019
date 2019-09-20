package com.agentecon.market;

import com.agentecon.agent.IAgent;
import com.agentecon.firm.IStockMarket;
import com.agentecon.goods.Good;
import com.agentecon.util.AbstractListenerList;

public class MarketListeners extends AbstractListenerList<IMarketListener> implements IMarketListener {

	@Override
	public void notifyTraded(IAgent seller, IAgent buyer, Good good, double quantity, double payment) {
		for (IMarketListener l: list){
			l.notifyTraded(seller, buyer, good, quantity, payment);
		}
	}

	@Override
	public void notifyTradesCancelled() {
		for (IMarketListener l: list){
			l.notifyTradesCancelled();
		}
	}
	
	@Override
	public void notifyOffersPosted(IStockMarket market) {
		for (IMarketListener l: list){
			l.notifyOffersPosted(market);
		}
	}

	@Override
	public void notifyMarketClosed(int day) {
		for (IMarketListener l: list){
			l.notifyMarketClosed(day);
		}
	}

	@Override
	public void notifyMarketClosed(int day, IPriceTakerMarket market) {
		for (IMarketListener l: list){
			l.notifyMarketClosed(day, market);
		}
	}

}
