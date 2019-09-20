package com.agentecon.metric.variants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import com.agentecon.ISimulation;
import com.agentecon.consumer.IConsumer;
import com.agentecon.firm.IShareholder;
import com.agentecon.market.IStatistics;
import com.agentecon.metric.SimStats;
import com.agentecon.metric.series.AveragingTimeSeries;
import com.agentecon.metric.series.Chart;
import com.agentecon.metric.series.TimeSeries;
import com.agentecon.util.InstantiatingConcurrentHashMap;

public class UtilityStats extends SimStats {

	private TimeSeries tot, min, max;
	private AveragingTimeSeries retirees, shareHolders;
	private Map<String, TimeSeries> utilities;

	public UtilityStats(ISimulation sim) {
		super(sim);
		this.tot = new TimeSeries("Average", getMaxDay());
		this.min = new TimeSeries("Min", getMaxDay());
		this.max = new TimeSeries("Max", getMaxDay());
		this.retirees = new AveragingTimeSeries("Retirees", getMaxDay());
		this.shareHolders = new AveragingTimeSeries("Shareholders", getMaxDay());
		this.utilities = new InstantiatingConcurrentHashMap<String, TimeSeries>() {

			@Override
			protected TimeSeries create(String key) {
				return new TimeSeries(key, getMaxDay());
			}
		};
	}

	@Override
	public void notifyDayEnded(IStatistics stats) {
		this.tot.set(stats.getDay(), stats.getAverageUtility().getAverage());
		this.min.set(stats.getDay(), stats.getAverageUtility().getMin());
		this.max.set(stats.getDay(), stats.getAverageUtility().getMax());
		for (IConsumer consumer: getAgents().getConsumers()) {
			double util = consumer.getUtilityFunction().getLatestExperiencedUtility();
			if (consumer instanceof IShareholder && ((IShareholder)consumer).getPortfolio().hasPositions()) {
				this.shareHolders.add(util);
			}
			if (consumer.isRetired()) {
				this.retirees.add(util);
			}
			utilities.get(consumer.getName()).set(stats.getDay(), util);
		}
		this.shareHolders.push(stats.getDay());
		this.retirees.push(stats.getDay());
	}

	@Override
	public Collection<? extends Chart> getCharts() {
		Chart ch = new Chart("Average Utility", "Average daily utility per consumer in each iteration", getTimeSeries());
		return Collections.singleton(ch);
	}
	
	@Override
	public String toString(){
		return tot.toString();
	}

	@Override
	public Collection<TimeSeries> getTimeSeries() {
		Collection<TimeSeries> list = new ArrayList<>(Arrays.asList(tot, min, max, retirees.getTimeSeries(), shareHolders.getTimeSeries()));
		ArrayList<TimeSeries> individuals = new ArrayList<>(utilities.values());
		Collections.sort(individuals);
		list.addAll(individuals);
		return list;
	}
	
}
