package com.agentecon.metric.variants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import com.agentecon.ISimulation;
import com.agentecon.agent.IAgent;
import com.agentecon.consumer.Inheritance;
import com.agentecon.firm.IBank;
import com.agentecon.goods.Good;
import com.agentecon.market.IMarketStatistics;
import com.agentecon.market.IStatistics;
import com.agentecon.metric.SimStats;
import com.agentecon.metric.series.Chart;
import com.agentecon.metric.series.TimeSeries;
import com.agentecon.util.Average;
import com.agentecon.util.InstantiatingConcurrentHashMap;

public class MonetaryStats extends SimStats {

	private TimeSeries velocity;
	private TimeSeries moneySupply;
	private TimeSeries credit;
	private Map<Good, TimeSeries> prices;
	private Map<Good, TimeSeries> volumes;

	public MonetaryStats(ISimulation agents) {
		super(agents);
		this.moneySupply = new TimeSeries("Money Supply", getMaxDay());
		this.credit = new TimeSeries("Credit", getMaxDay()).compact();
		this.prices = new InstantiatingConcurrentHashMap<Good, TimeSeries>() {

			@Override
			protected TimeSeries create(Good key) {
				return new TimeSeries(key.getName() + " price", getMaxDay());
			}
		};
		this.volumes = new InstantiatingConcurrentHashMap<Good, TimeSeries>() {

			@Override
			protected TimeSeries create(Good key) {
				return new TimeSeries(key.getName() + " volume", getMaxDay());
			}
		};
		this.velocity = new TimeSeries("Velocity of Money", getMaxDay());
	}

	@Override
	public void notifyDayEnded(IStatistics stats) {
		int day = stats.getDay();

		double moneySupply = 0.0;
		for (IAgent a : getAgents().getAgents()) {
			moneySupply += a.getMoney().getNetAmount();
		}
		for (Inheritance pending: getAgents().getPendingInheritances()) {
			moneySupply += pending.getMoney().getNetAmount();
		}
		this.moneySupply.set(day, moneySupply);
		
		double credit = 0.0;
		for (IBank bank: getAgents().getBanks()) {
			credit += bank.getOutstandingCredit();
		}
		this.credit.set(day, credit);

		double transactionVolume = 0.0;
		transactionVolume += record(day, stats.getGoodsMarketStats());
//		transactionVolume += record(day, stats.getStockMarketStats());
		this.velocity.set(day, transactionVolume / moneySupply); // Fisher equation
	}

	private double record(int day, IMarketStatistics stats) {
		double transactionVolume = 0.0;
		for (Good good : stats.getTradedGoods()) {
			Average priceData = stats.getStats(good).getYesterday();
			if (priceData.getTotWeight() > 0) {
				transactionVolume += priceData.getTotal();
				prices.get(good).set(day, priceData.getAverage());
				volumes.get(good).set(day, priceData.getTotWeight());
			}
		}
		return transactionVolume;
	}

	@Override
	public Collection<? extends Chart> getCharts() {
		Chart ch = new Chart("Monetary Statistics", "All relevant data for calculating monetary velocity according to the Fisher equation.", getTimeSeries());
		ch.setStacking("normal");
		return Collections.singleton(ch);
	}

	@Override
	public Collection<TimeSeries> getTimeSeries() {
		ArrayList<TimeSeries> list = new ArrayList<>();
		list.add(moneySupply);
		if (credit.isInteresting()) {
			list.add(credit);
		}
		list.add(velocity);
		list.addAll(prices.values());
		list.addAll(volumes.values());
		return list;
	}

}
