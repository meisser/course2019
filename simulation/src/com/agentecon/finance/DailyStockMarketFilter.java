package com.agentecon.finance;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.agentecon.agent.IAgent;
import com.agentecon.firm.FirmFinancials;
import com.agentecon.firm.IBank;
import com.agentecon.firm.IStockMarket;
import com.agentecon.firm.Position;
import com.agentecon.firm.Ticker;
import com.agentecon.goods.IStock;
import com.agentecon.market.Ask;
import com.agentecon.market.Bid;
import com.agentecon.market.IMarketListener;
import com.agentecon.market.IMarketStatistics;

public class DailyStockMarketFilter implements IStockMarket {

	private IStockMarket wrapped;
	private Predicate<Ticker> typeFilter;

	public DailyStockMarketFilter(IStockMarket wrapped, Predicate<Ticker> typeFilter) {
		this.wrapped = wrapped;
		this.typeFilter = typeFilter;
	}
	
	@Override
	public IMarketStatistics getMarketStatistics() {
		return wrapped.getMarketStatistics();
	}

	public FirmFinancials getFirmData(Ticker ticker) {
		return wrapped.getFirmData(ticker);
	}

	public void addMarketListener(IMarketListener listener) {
		wrapped.addMarketListener(listener);
	}

	public void offer(Bid offer) {
		wrapped.offer(offer);
	}

	public void offer(Ask offer) {
		wrapped.offer(offer);
	}

	@Override
	public boolean hasData(Ticker t) {
		return wrapped.hasData(t);
	}

	public List<Ticker> getTradedStocks() {
		ArrayList<Ticker> filtered = new ArrayList<>();
		for (Ticker t : wrapped.getTradedStocks()) {
			if (!typeFilter.test(t)) {
				filtered.add(t);
			}
		}
		return filtered;
	}

	public Ticker findAnyAsk(List<Ticker> preferred, boolean marketCapWeight) {
		return wrapped.findAnyAsk(preferred, marketCapWeight);
	}

	public Ticker getRandomStock(boolean marketCapWeight) {
		Ticker random = findAnyAsk(getTradedStocks(), marketCapWeight);
		if (random == null || typeFilter.test(random)) {
			return null;
		} else {
			return random;
		}
	}

	public Position buy(IAgent buyer, Ticker ticker, Position existing, IStock wallet, double budget) {
		assert !typeFilter.test(ticker);
		return wrapped.buy(buyer, ticker, existing, wallet, budget);
	}

	public double sell(IAgent seller, Position pos, IStock wallet, double maxAmount) {
		return wrapped.sell(seller, pos, wallet, maxAmount);
	}

	public Ask getAsk(Ticker ticker) {
		return typeFilter.test(ticker) ? null : wrapped.getAsk(ticker);
	}

	public Bid getBid(Ticker ticker) {
		return wrapped.getBid(ticker);
	}

	public boolean hasBid(Ticker ticker) {
		return wrapped.hasBid(ticker);
	}

	public boolean hasAsk(Ticker ticker) {
		return !typeFilter.test(ticker) && wrapped.hasAsk(ticker);
	}

	@Override
	public IBank getLeverageProvider() {
		return wrapped.getLeverageProvider();
	}

}
