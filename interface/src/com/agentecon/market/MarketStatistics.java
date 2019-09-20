package com.agentecon.market;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.BiConsumer;

import com.agentecon.agent.IAgent;
import com.agentecon.firm.IFirm;
import com.agentecon.firm.Ticker;
import com.agentecon.goods.Good;
import com.agentecon.production.PriceUnknownException;
import com.agentecon.util.Average;
import com.agentecon.util.InstantiatingHashMap;
import com.agentecon.util.MovingAverage;

public class MarketStatistics implements IMarketStatistics, IMarketListener {

	private Average index;
	private HashMap<Good, GoodStats> prices;
	private HashMap<Ticker, FirmStats> pending, firms;

	public MarketStatistics() {
		this.firms = new InstantiatingHashMap<Ticker, FirmStats>() {

			@Override
			protected FirmStats create(Ticker key) {
				return new FirmStats();
			}
		};
		this.prices = new InstantiatingHashMap<Good, GoodStats>() {

			@Override
			protected GoodStats create(Good key) {
				return new GoodStats();
			}
		};
	}

	@Override
	public Collection<Good> getTradedGoods() {
		return prices.keySet();
	}

	public void notifyMarketOpened() {
		this.pending = new InstantiatingHashMap<Ticker, FirmStats>() {

			@Override
			protected FirmStats create(Ticker key) {
				return new FirmStats();
			}
		};
	}

	@Override
	public void notifyTraded(IAgent seller, IAgent buyer, Good good, double quantity, double payment) {
		assert quantity > 0.0;
		prices.get(good).notifyTraded(quantity, payment / quantity);
		if (pending == null) {
			notifyMarketOpened();
		}
		if (seller instanceof IFirm) {
			pending.get(((IFirm) seller).getTicker()).notifySold(good, quantity, payment);
		}
		if (buyer instanceof IFirm) {
			pending.get(((IFirm) buyer).getTicker()).notifyBought(good, quantity, payment);
		}
	}

	@Override
	public void notifyTradesCancelled() {
		for (GoodStats good : prices.values()) {
			good.resetCurrent();
		}
		pending.clear();
	}

	@Override
	public void notifyMarketClosed(int day, IPriceTakerMarket market) {
		for (GoodStats good : prices.values()) {
			good.commitCurrent();
		}
		this.firms = pending;
		this.pending = null;
		this.index = null;
	}

	@Override
	public GoodStats getStats(Good good) {
		return prices.get(good);
	}

	@Override
	public FirmStats getFirmStats(Ticker ticker) {
		return firms.get(ticker);
	}

	@Override
	public void print(PrintStream out) {
		out.println("Good\tPrice\tVolume");
		prices.forEach(new BiConsumer<Good, GoodStats>() {

			@Override
			public void accept(Good t, GoodStats u) {
				out.println(t + "\t" + u.toTabString());
			}
		});
	}

	@Override
	public String toString() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream stream = new PrintStream(out);
		print(stream);
		return new String(out.toByteArray());
	}

	@Override
	public double getPriceBelief(Good good) throws PriceUnknownException {
		MovingAverage avg = getStats(good).getMovingAverage();
		if (avg.hasSamples()) {
			return avg.getAverage();
		} else {
			throw new PriceUnknownException();
		}
	}

	@Override
	public double getPriceIndex() {
		if (index == null) {
			index = new Average();
			prices.forEach(new BiConsumer<Good, GoodStats>() {

				@Override
				public void accept(Good t, GoodStats u) {
					if (t.getPersistence() < 1.0) {
						Average avg = u.getYesterday();
						if (avg.hasValue()) {
							index.add(avg.getTotWeight() * avg.getAverage(), avg.getAverage());
						}
					}
				}
			});
		}
		return index.getAverage();
	}

	@Override
	public double getLatestPrice(Ticker ticker) {
		return getStats(ticker).getYesterday().getAverage();
	}

}
