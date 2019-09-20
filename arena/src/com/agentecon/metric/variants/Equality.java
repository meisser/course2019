package com.agentecon.metric.variants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.agentecon.ISimulation;
import com.agentecon.consumer.IConsumer;
import com.agentecon.market.IMarket;
import com.agentecon.market.IMarketListener;
import com.agentecon.market.IStatistics;
import com.agentecon.metric.SimStats;
import com.agentecon.metric.series.AveragingTimeSeries;
import com.agentecon.metric.series.TimeSeries;

public class Equality extends SimStats implements IMarketListener {

	private static final int STEP = 100;
	private static final int AGGREGATION_PERIOD = 20;

	private List<AveragingTimeSeries> wealth;
	private List<AveragingTimeSeries> utility;
	private int maxAge;

	public Equality(ISimulation agents) {
		super(agents);
		this.maxAge = agents.getConfig().getMaxAge();
		this.wealth = new ArrayList<>();
		this.utility = new ArrayList<>();
		this.wealth.add(new AveragingTimeSeries("Wealth Gini", getMaxDay()));
		this.utility.add(new AveragingTimeSeries("Utility Gini", getMaxDay()));
		if (maxAge < Integer.MAX_VALUE) {
			this.wealth.addAll(createTimeSeries("Wealth", maxAge, STEP));
			this.utility.addAll(createTimeSeries("Utility", maxAge, STEP));
		}
	}

	private Collection<AveragingTimeSeries> createTimeSeries(String string, int maxAge, int step) {
		ArrayList<AveragingTimeSeries> list = new ArrayList<>();
		for (int from = 0; from < maxAge; from += step) {
			list.add(new AveragingTimeSeries(string + " Gini (age " + from + " to " + (from + step) + ")", getMaxDay()));
		}
		return list;
	}

	private double calculateGini(List<GiniData> list) {
		if (list.size() <= 1) {
			return 0.0;
		} else {
			double totDifference = 0.0;
			double totSum = 0.0;
			for (int i = 0; i < list.size(); i++) {
				double v1 = list.get(i).value;
				totSum += v1;
				for (int j = i + 1; j < list.size(); j++) {
					double v2 = list.get(j).value;
					totDifference += Math.abs(v1 - v2);
				}
			}
			return totDifference / (list.size() * totSum);
		}
	}

	@Override
	public void notifyGoodsMarketOpened(IMarket market) {
		market.addMarketListener(this);
	}

	@Override
	public void notifyDayEnded(IStatistics stats) {
		int day = stats.getDay();
		List<List<GiniData>> data = getCollections(c -> c.getUtilityFunction().getLatestExperiencedUtility(), STEP);
		assert data.size() == utility.size();
		for (int i = 0; i < data.size(); i++) {
			utility.get(i).add(calculateGini(data.get(i)));
		}
		if (day % AGGREGATION_PERIOD == AGGREGATION_PERIOD - 1) {
			for (AveragingTimeSeries ts : wealth) {
				ts.push(day);
			}
			for (AveragingTimeSeries ts : utility) {
				ts.push(day);
			}
		}
	}

	private List<List<GiniData>> getCollections(Function<IConsumer, Double> fun, int step) {
		ArrayList<List<GiniData>> lists = new ArrayList<>();
		List<GiniData> all = getAgents().getConsumers().stream().map(c -> new GiniData(c, fun.apply(c))).collect(Collectors.toList());
		lists.add(all);
		if (maxAge < Integer.MAX_VALUE) {
			for (int from = 0; from < maxAge; from += step) {
				final int fromFinal = from;
				lists.add(all.stream().filter(c -> c.c.getAge() >= fromFinal && c.c.getAge() < (fromFinal + step)).collect(Collectors.toList()));
			}
		}
		// List<GiniData> retired = new ArrayList<>(all.stream().filter(c -> c.c.isRetired()).collect(Collectors.toList()));
		return lists;
	}

	@Override
	public Collection<TimeSeries> getTimeSeries() {
		ArrayList<TimeSeries> list = new ArrayList<>();
		list.addAll(AveragingTimeSeries.unwrap(wealth));
		list.addAll(AveragingTimeSeries.unwrap(utility));
		return list;
	}

	@Override
	public void notifyMarketClosed(int day) {
		// Calculate wealth after market close so goods that will be consumed soon are included
		IStatistics stats = getStats();
		List<List<GiniData>> data = getCollections(c -> c.getWealth(stats), STEP);
		assert data.size() == wealth.size();
		for (int i = 0; i < data.size(); i++) {
			wealth.get(i).add(calculateGini(data.get(i)));
		}
	}

	class GiniData {
		IConsumer c;
		double value;

		public GiniData(IConsumer c, double value) {
			this.c = c;
			this.value = value;
		}

		@Override
		public String toString() {
			return c.getName() + " has " + value;
		}
	}

}
