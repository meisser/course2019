// Created on Jun 23, 2015 by Luzius Meisser

package com.agentecon.metric.variants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import com.agentecon.ISimulation;
import com.agentecon.agent.IAgent;
import com.agentecon.goods.Good;
import com.agentecon.market.IMarket;
import com.agentecon.market.IMarketListener;
import com.agentecon.market.IOffer;
import com.agentecon.market.IPriceTakerMarket;
import com.agentecon.metric.SimStats;
import com.agentecon.metric.series.AveragingTimeSeries;
import com.agentecon.metric.series.TimeSeries;
import com.agentecon.util.Average;
import com.agentecon.util.InstantiatingConcurrentHashMap;

public class MarketStats extends SimStats implements IMarketListener {

	private Map<Good, Average> averages;
	// private HashMap<Good, Average> averageOffers;
	private Map<Good, TimeSeries> prices;
	// private HashMap<Good, MinMaxTimeSeries> priceBeliefs; // de facto almost same as prices
	private Map<Good, TimeSeries> volume;
	private Map<Good, AveragingTimeSeries> unfilledOffers;
	private TimeSeries index;

	public MarketStats(ISimulation sim, boolean inclVolume) {
		super(sim);
		this.index = new TimeSeries("Price Index", getMaxDay());
		this.averages = new InstantiatingConcurrentHashMap<Good, Average>() {

			@Override
			protected Average create(Good key) {
				return new Average();
			}
		};
		this.prices = new InstantiatingConcurrentHashMap<Good, TimeSeries>() {

			@Override
			protected TimeSeries create(Good key) {
				return new TimeSeries(key.getName(), getMaxDay());
			}

		};
		this.unfilledOffers = new InstantiatingConcurrentHashMap<Good, AveragingTimeSeries>() {

			@Override
			protected AveragingTimeSeries create(Good key) {
				return new AveragingTimeSeries("Unfilled " + key.getName() + " offer volume after market close", getMaxDay());
			}

		};
		if (inclVolume) {
			this.volume = new InstantiatingConcurrentHashMap<Good, TimeSeries>() {

				@Override
				protected TimeSeries create(Good key) {
					return new TimeSeries(key.getName(), getMaxDay());
				}

			};
		}
	}

	public double getIndex() {
		return index.getLatest();
	}

	public double getPrice(Good good) {
		return averages.get(good).getAverage();
	}

	@Override
	public void notifyGoodsMarketOpened(IMarket market) {
		averages.clear();
		market.addMarketListener(this);
	}

	@Override
	public void notifyTraded(IAgent seller, IAgent buyer, Good good, double quantity, double payment) {
		if (quantity >= 0.001) {
			averages.get(good).add(quantity, payment / quantity);
		}
	}

	@Override
	public void notifyTradesCancelled() {
	}
	
	@Override
	public void notifyMarketClosed(int day, IPriceTakerMarket market) {
		notifyMarketClosed(day);
		for (IOffer offer: market.getBids()) {
			unfilledOffers.get(offer.getGood()).add(offer.getAmount());
		}
		for (IOffer offer: market.getAsks()) {
			unfilledOffers.get(offer.getGood()).add(offer.getAmount());
		}
		for (AveragingTimeSeries ts: unfilledOffers.values()) {
			ts.pushSum(day);
			ts.pushZeroIfNothing();
		}
	}
	
	@Override
	public void notifyMarketClosed(int day) {
		Average indexValue = new Average();
		for (Entry<Good, Average> e : averages.entrySet()) {
			Average avg = e.getValue();
			double price = avg.getAverage();
			double vol = avg.getTotWeight();
			indexValue.add(price * vol, price);
			prices.get(e.getKey()).set(day, price); // , avg.getMin(), avg.getMax());
			if (volume != null) {
				volume.get(e.getKey()).set(day, vol);
			}
		}
		if (indexValue.hasValue()) {
			index.set(day, indexValue.getAverage());
		}
	}

	@Override
	public String toString() {
		return "prices " + prices.values();
	}

	@Override
	public Collection<TimeSeries> getTimeSeries() {
		ArrayList<TimeSeries> list = new ArrayList<>();
		list.addAll(TimeSeries.prefix("Price", prices.values()));
		list.add(index);
		list.add(index.getLogReturns().rename("Inflation rate"));
		for (TimeSeries ts: AveragingTimeSeries.unwrap(unfilledOffers.values())) {
			if (ts.isInteresting()) {
				list.add(ts);
			}
		}
		if (volume != null) {
			list.addAll(createRealPrices());
			list.addAll(TimeSeries.prefix("Volume", volume.values()));
		}
		return list;
	}

	private Collection<? extends TimeSeries> createRealPrices() {
		ArrayList<TimeSeries> list = new ArrayList<>();
		for (TimeSeries ts : prices.values()) {
			list.add(ts.divideBy(index));
		}
		return list;
	}

}
