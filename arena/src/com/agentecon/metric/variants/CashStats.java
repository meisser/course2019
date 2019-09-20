package com.agentecon.metric.variants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import com.agentecon.ISimulation;
import com.agentecon.agent.IAgent;
import com.agentecon.market.IStatistics;
import com.agentecon.metric.SimStats;
import com.agentecon.metric.series.TimeSeries;
import com.agentecon.metric.series.TimeSeriesCollector;

public class CashStats extends SimStats {

	private TimeSeriesCollector totalCash, averageCash;

	public CashStats(ISimulation agents, boolean individuals) {
		super(agents);
		this.totalCash = new TimeSeriesCollector(individuals, getMaxDay());
		this.averageCash = new TimeSeriesCollector(individuals, getMaxDay());
	}

	@Override
	public void notifyDayEnded(IStatistics stats) {
		super.notifyDayEnded(stats);
		int day = stats.getDay();
		for (IAgent a : getAgents().getAgents()) {
			double money = a.getMoney().getNetAmount();
			totalCash.record(day, a, money);
			averageCash.record(day, a, money);
		}
		totalCash.flushDay(day, false);
		averageCash.flushDay(day, true);
	}

	@Override
	public Collection<TimeSeries> getTimeSeries() {
		ArrayList<TimeSeries> list = new ArrayList<>();
		list.addAll(totalCash.getTimeSeries());
		list.addAll(averageCash.getTimeSeries().stream().map(s -> s.prefix("Avg ")).collect(Collectors.toList()));
		return list;
	}
	
}
