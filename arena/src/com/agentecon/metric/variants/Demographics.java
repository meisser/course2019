package com.agentecon.metric.variants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import com.agentecon.ISimulation;
import com.agentecon.consumer.IConsumer;
import com.agentecon.market.IStatistics;
import com.agentecon.metric.SimStats;
import com.agentecon.metric.series.AveragingTimeSeries;
import com.agentecon.metric.series.Chart;
import com.agentecon.metric.series.TimeSeries;
import com.agentecon.util.InstantiatingConcurrentHashMap;

public class Demographics extends SimStats {

	private TimeSeries retired;
	private TimeSeries working;
	private TimeSeries population, dependency, dailyutility;
	private Map<String, AveragingTimeSeries> populationByType;
//	private Map<String, AveragingTimeSeries> utilityOnDeath;

	public Demographics(ISimulation agents) {
		super(agents);
		this.retired = new TimeSeries("Retirees", getMaxDay());
		this.working = new TimeSeries("Workers", getMaxDay());
		this.population = new TimeSeries("Population", getMaxDay());
		this.dependency = new TimeSeries("Inverse Dependency Ratio", getMaxDay());
		this.dailyutility = new TimeSeries("Average Daily Utility", getMaxDay());
//		this.utilityOnDeath = new InstantiatingConcurrentHashMap<String, AveragingTimeSeries>() {
//
//			@Override
//			protected AveragingTimeSeries create(String key) {
//				return new AveragingTimeSeries(key, getMaxDay());
//			}
//		};
		this.populationByType = new InstantiatingConcurrentHashMap<String, AveragingTimeSeries>() {

			@Override
			protected AveragingTimeSeries create(String key) {
				return new AveragingTimeSeries(key, getMaxDay());
			}
		};
	}

	@Override
	public void notifyDayEnded(IStatistics stats) {
		int day = stats.getDay();
		double util = stats.getAverageUtility().getAverage();
		dailyutility.set(day, util);
//		for (AveragingTimeSeries pc : utilityOnDeath.values()) {
//			pc.push(day);
//		}
		for (AveragingTimeSeries type : populationByType.values()) {
			type.pushSum(day);
		}
		Collection<? extends IConsumer> cons = getAgents().getConsumers();
		int retired = 0, working = 0, total = 0;
		for (IConsumer c : cons) {
			total++;
			if (c.isRetired()) {
				retired++;
			} else {
				working++;
			}
			populationByType.get(c.getType()).add(1.0);
		}
		this.retired.set(day, retired);
		this.population.set(day, total);
		this.working.set(day, working);
		if (retired > 0) {
			this.dependency.set(day, ((double) working) / retired);
		}
	}

//	@Override
//	public void notfiyConsumerDied(IConsumer consumer) {
//		utilityOnDeath.get(consumer.getType()).add(consumer.getUtilityFunction().getStatistics().getTotal());
//	}
//
//	public Collection<? extends TimeSeries> getUtilityData() {
//		ArrayList<TimeSeries> ts = new ArrayList<>();
//		for (AveragingTimeSeries pc : utilityOnDeath.values()) {
//			ts.add(pc.getTimeSeries());
//		}
//		return ts;
//	}

	@Override
	public Collection<? extends Chart> getCharts() {
		return Arrays.asList(new Chart("Population", "Retired, working and total population", retired, working, population), new Chart("Dependency Ratio", "Retirees per workers", dependency));
	}

	@Override
	public Collection<TimeSeries> getTimeSeries() {
		ArrayList<TimeSeries> all = new ArrayList<>();
		all.addAll(Arrays.asList(population, retired, working, dependency));
//		all.addAll(TimeSeries.prefix("Utility on death", getUtilityData()));
//		all.add(dailyutility);
		if (populationByType.size() > 1) {
			all.addAll(TimeSeries.prefix("Population of ", AveragingTimeSeries.unwrap(populationByType.values())));
		}
		return all;
	}

}
