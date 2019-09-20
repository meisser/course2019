package com.agentecon.metric.variants;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.agentecon.ISimulation;
import com.agentecon.agent.IAgent;
import com.agentecon.firm.IFirm;
import com.agentecon.firm.IFirmListener;
import com.agentecon.goods.Good;
import com.agentecon.goods.Quantity;
import com.agentecon.market.IMarket;
import com.agentecon.market.IMarketListener;
import com.agentecon.market.IStatistics;
import com.agentecon.metric.SimStats;
import com.agentecon.metric.series.Chart;
import com.agentecon.metric.series.TimeSeries;
import com.agentecon.production.IProducer;
import com.agentecon.production.IProducerListener;
import com.agentecon.util.AccumulatingAverage;
import com.agentecon.util.InstantiatingHashMap;

public class MarketMetrics extends SimStats implements IMarketListener, IFirmListener, IProducerListener {

	private static final double MEMORY = 0.95;

	private boolean stable;
	private AccumulatingAverage dividends, profits;
	private HashMap<Good, AccumulatingAverage> prices;
	private HashMap<Good, AccumulatingAverage> production;
	private ArrayList<AccumulatingAverage> all;

	public MarketMetrics(ISimulation sim) {
		super(sim);
		this.all = new ArrayList<>();
		this.dividends = new AccumulatingAverage(MEMORY);
		this.all.add(dividends);
		this.profits = new AccumulatingAverage(MEMORY);
		this.all.add(profits);
		this.prices = new InstantiatingHashMap<Good, AccumulatingAverage>() {

			@Override
			protected AccumulatingAverage create(Good key) {
				AccumulatingAverage wma = new AccumulatingAverage(MEMORY);
				all.add(wma);
				return wma;
			}
		};
		this.production = new InstantiatingHashMap<Good, AccumulatingAverage>() {

			@Override
			protected AccumulatingAverage create(Good key) {
				AccumulatingAverage ama = new AccumulatingAverage(MEMORY);
				all.add(ama);
				return ama;
			}
		};
	}

	@Override
	public void notifyProduced(IProducer comp, Quantity[] inputs, Quantity output) {
		this.production.get(output.getGood()).add(output.getAmount());
	}

	@Override
	public void reportDividend(IFirm comp, double amount) {
		this.dividends.add(amount);
	}

	@Override
	public void reportResults(IProducer comp, double revenue, double cogs, double profits) {
		this.profits.add(profits);
	}
	
	@Override
	public void notifyTraded(IAgent seller, IAgent buyer, Good good, double quantity, double payment) {
		this.prices.get(good).add(quantity, payment / quantity);			
	}

	@Override
	public void notifyTradesCancelled() {
		for (AccumulatingAverage avg : all) {
			avg.reset();
		}
	}

	@Override
	public void notifyGoodsMarketOpened(IMarket market) {
		market.addMarketListener(this);
	}
	
	@Override
	public void notifyAgentCreated(IAgent agent) {
		agent.addListener(this);
	}

	public boolean hasJustReachedStability() {
		boolean wasStable = this.stable;
		this.stable = isStable();
		return this.stable && !wasStable;
	}

	public boolean isStable() {
		for (AccumulatingAverage ma : all) {
			if (!ma.isStable()) {
				return false;
			}
		}
		return true;
	}

	private double util;

	public double getLatestUtility() {
		return util;
	}

	@Override
	public void notifyDayEnded(IStatistics stats) {
		this.util = stats.getAverageUtility().getAverage();
		for (AccumulatingAverage avg : all) {
			avg.flush();
		}
	}

	public void printResult(PrintStream ps) {
		ps.println("Dividends " + dividends);
		ps.println("Profits " + profits);
		for (Map.Entry<Good, AccumulatingAverage> e : prices.entrySet()) {
			ps.println(e.getKey() + " price: " + e.getValue());
			if (production.containsKey(e.getKey())) {
				ps.println(e.getKey() + " production: " + production.get(e.getKey()));
			}
		}

	}

	@Override
	public Collection<? extends Chart> getCharts() {
		return Arrays.asList();
	}

	@Override
	public Collection<TimeSeries> getTimeSeries() {
		return Collections.emptyList();
	}

}
