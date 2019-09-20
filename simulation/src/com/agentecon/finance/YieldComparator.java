package com.agentecon.finance;

import java.util.Comparator;

import com.agentecon.firm.IStockMarket;
import com.agentecon.firm.Ticker;
import com.agentecon.market.AbstractOffer;

public class YieldComparator implements Comparator<Ticker> {

	private boolean buying;
	private IStockMarket dsm;

	public YieldComparator(IStockMarket dsm, boolean buying) {
		this.dsm = dsm;
		this.buying = buying;
	}

	@Override
	public int compare(Ticker o1, Ticker o2) {
		double yield1 = getYield(o1);
		double yield2 = getYield(o2);
		return Double.compare(yield1, yield2);
	}

	public double getYield(Ticker o1) {
		double dividend = dsm.getFirmData(o1).getDailyDividendPerShare();
		double price = getPrice(o1);
		return dividend / price;
	}

	public double getPrice(Ticker ticker) {
		AbstractOffer offer = buying ? dsm.getAsk(ticker) : dsm.getBid(ticker);
		return offer.getPrice().getPrice();
	}

}
