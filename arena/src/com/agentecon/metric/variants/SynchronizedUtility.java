package com.agentecon.metric.variants;

import java.util.Collection;
import java.util.Map;

import com.agentecon.ISimulation;
import com.agentecon.consumer.IConsumer;
import com.agentecon.market.IStatistics;
import com.agentecon.metric.SimStats;
import com.agentecon.metric.series.AverageSeries;
import com.agentecon.metric.series.TimeSeries;
import com.agentecon.util.InstantiatingConcurrentHashMap;

public class SynchronizedUtility extends SimStats {

	private boolean individuals;
	private Map<String, AverageSeries> utilities;

	public SynchronizedUtility(ISimulation sim, boolean details) {
		super(sim);
		this.individuals = details;
		this.utilities = new InstantiatingConcurrentHashMap<String, AverageSeries>() {

			@Override
			protected AverageSeries create(String key) {
				return new AverageSeries(key);
			}
		};
	}

	@Override
	public void notifyDayEnded(IStatistics stats) {
		for (IConsumer consumer : getAgents().getConsumers()) {
			double util = consumer.getUtilityFunction().getLatestExperiencedUtility();
			String label = individuals ? consumer.getName() : consumer.getType();
			utilities.get(label).add(consumer.getAge() - 1, util); // age starts at one
		}
	}

	@Override
	public String toString() {
		return "Synchronized Utility";
	}

	@Override
	public Collection<TimeSeries> getTimeSeries() {
		return AverageSeries.unwrap(utilities.values());
	}

}
