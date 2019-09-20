package com.agentecon.finance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.agentecon.agent.IAgent;
import com.agentecon.firm.FirmFinancials;
import com.agentecon.firm.IBank;
import com.agentecon.firm.IFinancialMarketData;
import com.agentecon.firm.IStockMarket;
import com.agentecon.firm.Position;
import com.agentecon.firm.Ticker;
import com.agentecon.goods.IStock;
import com.agentecon.market.Ask;
import com.agentecon.market.BestPriceMarket;
import com.agentecon.market.Bid;
import com.agentecon.market.IMarketListener;
import com.agentecon.market.IMarketStatistics;
import com.agentecon.market.MarketListeners;
import com.agentecon.util.InstantiatingHashMap;
import com.agentecon.util.Numbers;

public class DailyStockMarket implements IStockMarket {

	private IBank bank;
	private Random rand;
	private MarketListeners listeners;
	private InstantiatingHashMap<Ticker, BestPriceMarket> market;
	private IFinancialMarketData bloomberg;

	private ArrayList<BestPriceMarket> marketCache;

	public DailyStockMarket(IFinancialMarketData bloomberg, IBank bank, Random rand) {
		this.rand = rand;
		this.bank = bank;
		this.bloomberg = bloomberg;
		this.listeners = new MarketListeners();
		this.market = new InstantiatingHashMap<Ticker, BestPriceMarket>() {

			@Override
			protected BestPriceMarket create(Ticker key) {
				return new BestPriceMarket(key);
			}
		};
	}

	@Override
	public IBank getLeverageProvider() {
		return bank;
	}
	
	@Override
	public Collection<Ticker> getTradedStocks() {
		ArrayList<Ticker> randomList = new ArrayList<>(market.size());
		for (Ticker t: market.keySet()) {
			if (hasData(t)) {
				randomList.add(t);
			} else {
				new Exception("Asked for firm data of non-existing firm " + t).printStackTrace();;
			}
		}
		Collections.shuffle(randomList, rand);
		return randomList;
	}

	@Override
	public FirmFinancials getFirmData(Ticker ticker) {
		return bloomberg.getFirmData(ticker);
	}
	
	@Override
	public boolean hasData(Ticker t) {
		return bloomberg.hasData(t);
	}

	@Override
	public void addMarketListener(IMarketListener listener) {
		this.listeners.add(listener);
	}

	@Override
	public void offer(Bid bid) {
		assert bid instanceof BidFin;
		bid.setListener(listeners);
		this.market.get(bid.getGood()).offer(bid);
	}

	@Override
	public void offer(Ask ask) {
		assert ask instanceof AskFin;
		ask.setListener(listeners);
		this.market.get(ask.getGood()).offer(ask);
		this.marketCache = null;
	}
	
	@Override
	public Ticker findAnyAsk(List<Ticker> preferred, boolean marketCapWeight) {
		while (preferred.size() > 0) {
			int choice = rand.nextInt(preferred.size());
			Ticker t = preferred.get(choice);
			if (hasAsk(t)) {
				return t;
			} else {
				preferred.remove(choice);
			}
		}
		return getRandomStock(marketCapWeight);
	}

	@Override
	public Ticker getRandomStock(boolean marketCapWeight) {
		if (marketCapWeight) {
			return findMarketCapWeightedRandomAsk();
		} else {
			return findRandomAsk();
		}
	}

	private Ticker findRandomAsk() {
		if (marketCache == null) {
			marketCache = new ArrayList<>(market.size());
			for (BestPriceMarket bpm : market.values()) {
				if (bpm.getAsk() != null) {
					marketCache.add(bpm);
				}
			}
		}
		while (marketCache.size() > 0) {
			int index = rand.nextInt(marketCache.size());
			Ask ask = marketCache.get(index).getAsk();
			if (ask == null) {
				marketCache.remove(index);
			} else {
				return (Ticker) ask.getGood();
			}
		}
		return null;
	}

	protected Ticker findMarketCapWeightedRandomAsk() {
		// TEMP improve performance
		ArrayList<Ask> list = new ArrayList<>();
		double total = 0.0;
		for (BestPriceMarket market: market.values()){
			Ask ask = market.getAsk();
			if (ask != null){
				list.add(ask);
				total += ask.getPrice().getPrice();
			}
		}
		double selection = rand.nextDouble() * total;
		double pos = 0.0;
		for (Ask a: list){
			pos += a.getPrice().getPrice();
			if (pos >= selection){
				return (Ticker) a.getGood();
			}
		}
		
		return null;
	}

	@Override
	public Position buy(IAgent owner, Ticker ticker, Position existing, IStock wallet, double budget) {
		AskFin ask = getAsk(ticker);
		if (ask != null) {
			return ask.accept(owner, wallet, existing, budget);
		} else {
			return existing;
		}
	}

	public Ticker findHighestBid(Collection<Ticker> keySet) {
		BidFin highest = null;
		for (Ticker ticker : keySet) {
			BidFin bid = getBid(ticker);
			if (bid != null) {
				if (highest == null || bid.getPrice().getPrice() > highest.getPrice().getPrice()) {
					highest = bid;
				}
			}
		}
		return highest == null ? null : highest.getTicker();
	}

	@Override
	public double sell(IAgent owner, Position pos, IStock wallet, double shares) {
		double sold = 0.0;
		while (Numbers.isSmaller(sold, shares) && pos.hasSome()) {
			BidFin bid = getBid(pos.getTicker());
			if (bid != null) {
				sold += bid.accept(owner, wallet, pos, shares);
			} else {
				break;
			}
		}
		return sold;
	}

	@Override
	public AskFin getAsk(Ticker ticker) {
		BestPriceMarket best = market.getWithoutCreating(ticker);
		return best == null ? null : (AskFin) best.getAsk();
	}

	@Override
	public BidFin getBid(Ticker ticker) {
		BestPriceMarket best = market.getWithoutCreating(ticker);
		return best == null ? null : (BidFin) best.getBid();
	}

	@Override
	public boolean hasBid(Ticker ticker) {
		return getBid(ticker) != null;
	}

	@Override
	public boolean hasAsk(Ticker ticker) {
		return getAsk(ticker) != null;
	}

	public String getTradingStats() {
		int asks = 0, bids = 0;
		for (BestPriceMarket bpm : market.values()) {
			if (bpm.getAsk() != null) {
				asks++;
			}
			if (bpm.getBid() != null) {
				bids++;
			}
		}
		return asks + "/" + market.size() + " asks and " + bids + "/" + market.size() + " bids left";
	}
	
	public void notifyOffersPosted() {
		listeners.notifyOffersPosted(this);
	}

	public void close(int day) {
		listeners.notifyMarketClosed(day, null);
	}

	@Override
	public IMarketStatistics getMarketStatistics() {
		return bloomberg.getMarketStatistics();
	}

}
