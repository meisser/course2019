package com.agentecon.metric.variants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.agentecon.ISimulation;
import com.agentecon.agent.IAgent;
import com.agentecon.consumer.IConsumer;
import com.agentecon.firm.IFirm;
import com.agentecon.goods.Good;
import com.agentecon.market.IStatistics;
import com.agentecon.metric.SimStats;
import com.agentecon.metric.series.IAgentType;
import com.agentecon.metric.series.TimeSeries;
import com.agentecon.metric.series.TimeSeriesCollector;
import com.agentecon.production.IProducer;
import com.agentecon.sim.IOptimalityIndicator;
import com.agentecon.util.InstantiatingConcurrentHashMap;

public class TypeStatistics extends SimStats {

	private TimeSeriesCollector agentCounts;
	private Map<Good, TimeSeries> optimalCount;
	private IOptimalityIndicator[] indicators;

	public TypeStatistics(ISimulation sim) {
		super(sim);
		this.agentCounts = new TimeSeriesCollector(getMaxDay());
		this.indicators = sim.getConfig().getOptimalFirmCountIndicators();
		this.optimalCount = new InstantiatingConcurrentHashMap<Good, TimeSeries>() {

			@Override
			protected TimeSeries create(Good key) {
				return new TimeSeries("Optimal number of " + key.getName().toLowerCase() + " producers", getMaxDay());
			}
		};
	}

	@Override
	public void notifyDayEnded(IStatistics stats) {
		for (IAgent agent : getAgents().getAgents()) {
			agentCounts.record(stats.getDay(), new IAgentType() {

				@Override
				public String[] getTypeKeys() {
					if (agent instanceof IProducer) {
						return new String[] { agent.getType(), ((IProducer) agent).getOutput() + " producer" };
					} else {
						return new String[] { agent.getType() };
					}
				}

				@Override
				public String getIndividualKey() {
					return null;
				}

				@Override
				public boolean isConsumer() {
					return agent instanceof IConsumer;
				}

				@Override
				public boolean isFirm() {
					return agent instanceof IFirm;
				}
			}, 1.0);
		}
		agentCounts.flushDay(stats.getDay(), false);

		for (IOptimalityIndicator indicator : indicators) {
			optimalCount.get(indicator.getOutputGood()).set(stats.getDay(), indicator.getOptimum(stats.getGoodsMarketStats()));
		}
	}

	@Override
	public Collection<TimeSeries> getTimeSeries() {
		ArrayList<TimeSeries> list = new ArrayList<>();
		list.addAll(optimalCount.values());
		list.addAll(agentCounts.getTimeSeries());
		return list;
	}

}
