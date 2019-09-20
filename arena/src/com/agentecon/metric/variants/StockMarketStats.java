// Created on Jun 23, 2015 by Luzius Meisser

package com.agentecon.metric.variants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.agentecon.ISimulation;
import com.agentecon.agent.IAgent;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IConsumerListener;
import com.agentecon.firm.IFirm;
import com.agentecon.firm.Ticker;
import com.agentecon.goods.Good;
import com.agentecon.goods.Inventory;
import com.agentecon.market.IMarket;
import com.agentecon.market.IMarketListener;
import com.agentecon.metric.SimStats;
import com.agentecon.metric.series.AveragingTimeSeries;
import com.agentecon.metric.series.Chart;
import com.agentecon.metric.series.TimeSeries;
import com.agentecon.util.Average;
import com.agentecon.util.InstantiatingConcurrentHashMap;
import com.agentecon.util.InstantiatingHashMap;
import com.agentecon.util.Numbers;

public class StockMarketStats extends SimStats implements IMarketListener, IConsumerListener {

	private Good index = new Good("Index");
	private Map<Ticker, Average> averages;
	private Map<Good, TimeSeries> prices;
	private Map<Good, TimeSeries> volumes;
	private Map<Good, TimeSeries> dividendYield;
	private AveragingTimeSeries investments, divestments, difference;

	private boolean includeIndex;
	private boolean individualStocks;

	public StockMarketStats(ISimulation agents, boolean includeIndex, boolean individualStocks) {
		super(agents);
		this.includeIndex = includeIndex;
		this.individualStocks = individualStocks;
		this.investments = new AveragingTimeSeries("Inflows", getMaxDay());
		this.divestments = new AveragingTimeSeries("Outflows", getMaxDay());
		this.difference = new AveragingTimeSeries("Inflows - Outflows", getMaxDay());
		this.averages = new InstantiatingConcurrentHashMap<Ticker, Average>() {

			@Override
			protected Average create(Ticker key) {
				return new Average();
			}
		};
		this.prices = new InstantiatingConcurrentHashMap<Good, TimeSeries>() {

			@Override
			protected TimeSeries create(Good key) {
				return new TimeSeries(key.getName(), getMaxDay());
			}

		};
		this.volumes = new InstantiatingConcurrentHashMap<Good, TimeSeries>() {

			@Override
			protected TimeSeries create(Good key) {
				return new TimeSeries(key.getName(), getMaxDay());
			}

		};
		this.dividendYield = new InstantiatingConcurrentHashMap<Good, TimeSeries>() {

			@Override
			protected TimeSeries create(Good key) {
				return new TimeSeries(key.getName(), getMaxDay());
			}

		};
	}

	public double getPrice(Ticker ticker) {
		return averages.get(ticker).getAverage();
	}

	@Override
	public void notifyDayStarted(int day) {
		averages.clear();
	}

	@Override
	public void notifyStockMarketOpened(IMarket market) {
		market.addMarketListener(this);
	}

	@Override
	public void notifyConsumerCreated(IConsumer consumer) {
		consumer.addListener(this);
	}

	@Override
	public void notifyTraded(IAgent seller, IAgent buyer, Good good, double quantity, double payment) {
		averages.get(good).add(quantity, payment / quantity);
	}

	@Override
	public void notifyTradesCancelled() {
		averages.clear();
	}

	@Override
	public void notifyMarketClosed(int day) {
		investments.pushSum(day);
		divestments.pushSum(day);
		difference.pushSum(day);
		Average indexPoints = new Average();
		Average indexYield = new Average();
		HashMap<Good, Average> sectorIndices = new InstantiatingHashMap<Good, Average>() {

			@Override
			protected Average create(Good key) {
				return new Average();
			}
		};
		HashMap<Good, Average> sectorYields = new InstantiatingHashMap<Good, Average>() {

			@Override
			protected Average create(Good key) {
				return new Average();
			}
		};
		for (Entry<Ticker, Average> e : averages.entrySet()) {
			IFirm firm = getAgents().getFirm(e.getKey());
			Good sector = new Good(firm.getType());
			Average avgPrice = e.getValue();
			if (individualStocks) {
				prices.get(e.getKey()).set(day, avgPrice.getAverage());
			}
			double marketCap = firm.getShareRegister().getFreeFloatShares() * avgPrice.getAverage();
			indexPoints.add(marketCap, avgPrice);
			sectorIndices.get(sector).add(marketCap, avgPrice);

			double dividends = firm.getShareRegister().getAverageDividend();
			if (dividends > Numbers.EPSILON) {
				double yield = dividends / marketCap;
				indexYield.add(marketCap, yield);
				sectorYields.get(sector).add(marketCap, yield);
				if (individualStocks) {
					dividendYield.get(e.getKey()).set(day, yield);
				}
			}
		}
		// printTicker(day);
		if (includeIndex) {
			sectorIndices.put(index, indexPoints);
			dividendYield.get(index).set(day, indexYield.hasValue() ? indexYield.getAverage() : 0.0);
		}
		for (Map.Entry<Good, Average> e : sectorIndices.entrySet()) {
			Average ind = e.getValue();
			if (ind.hasValue()) {
				Good sector = e.getKey();
				prices.get(sector).set(day, ind.getAverage());
				volumes.get(sector).set(day, ind.getTotWeight());
			}
		}
		for (Map.Entry<Good, Average> e : sectorYields.entrySet()) {
			dividendYield.get(e.getKey()).set(day, e.getValue().getAverage());
		}
		// if (indexRatio.hasValue()) {
		// peratio.get(index).set(day, indexRatio.getAverage());
		// }
	}

	private ArrayList<Ticker> toPrint = new ArrayList<>();

	protected void printTicker(int day) {
		if (day == 1000) {
			toPrint.addAll(averages.keySet());
			Collections.sort(toPrint);
			printLabels();
		} else if (day > 1000 && toPrint.size() < averages.size()) {
			for (Ticker t : averages.keySet()) {
				if (!toPrint.contains(t)) {
					toPrint.add(t);
				}
			}
			printLabels();
		}
		if (toPrint.size() > 0) {
			String line = Integer.toString(day);
			for (Good g : toPrint) {
				Average avg = averages.get(g);
				if (avg == null) {
					line += "\t";
				} else {
					line += "\t" + avg.getAverage();
				}
			}
			System.out.println(line);
		}
	}

	protected void printLabels() {
		String labels = "";
		for (Good g : toPrint) {
			labels += "\t" + g;
		}
		System.out.println(labels);
	}

	@Override
	public Collection<? extends Chart> getCharts() {
		Chart ch1 = new Chart("Stock Market Prices", "Volume-weighted stock prices for each sector", prices.values());
		Chart ch2 = new Chart("Stock Market Volumes", "Stock trading volumes of each sector", volumes.values());
		Chart ch3 = new Chart("Price/Earning Ratios", "P/E ratios by sector", dividendYield.values());
		Chart ch4 = new Chart("Investment Flows", "Worker investments versus retiree divestments", investments.getTimeSeries(), divestments.getTimeSeries());
		return Arrays.asList(ch1, ch2, ch3, ch4);
	}

	@Override
	public String toString() {
		return "Sales stats on " + prices.size() + " stocks";
	}

	@Override
	public Collection<TimeSeries> getTimeSeries() {
		ArrayList<TimeSeries> list = new ArrayList<>();
		list.addAll(TimeSeries.prefix("Price", prices.values()));
		// ArrayList<TimeSeries> logReturns = TimeSeries.logReturns(list);
		// list.addAll(logReturns);
		list.addAll(TimeSeries.prefix("Volume", volumes.values()));
		list.addAll(TimeSeries.prefix("Dividend yield", dividendYield.values()));
		// if (includeIndex && prices.get(index).isInteresting()) {
		// list.add(createTotalReturnIndex(prices.get(index), dividendYield.get(index)));
		// }
		if (investments.getTimeSeries().compact().isInteresting()) {
			list.add(investments.getTimeSeries());
			list.add(divestments.getTimeSeries());
			// list.add(difference.getTieSeries()); TEMP
		}
		return list;
	}

	private TimeSeries createTotalReturnIndex(TimeSeries prices, TimeSeries yields) {
		TimeSeries returns = prices.getReturns().add(yields);
		TimeSeries totalReturnIndex = new TimeSeries("Total return index (logarithmic)", getMaxDay());
		double current = Math.E;
		totalReturnIndex.set(0, current);
		for (int i = 1; i <= returns.getEnd(); i++) {
			current *= returns.get(i);
			if (current > 0.0) {
				totalReturnIndex.set(i, Math.log(current));
			}
		}
		return totalReturnIndex;
	}

	@Override
	public void notifyConsuming(IConsumer inst, int age, Inventory inv, double utility) {
	}

	@Override
	public void notifyRetiring(IConsumer inst, int age) {
	}

	@Override
	public void notifyInvested(IConsumer inst, double amount) {
		investments.add(amount);
		difference.add(amount);
	}

	@Override
	public void notifyDivested(IConsumer inst, double amount) {
		divestments.add(amount);
		difference.add(-amount);
	}

}
