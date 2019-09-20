package com.agentecon.metric.variants;

import java.util.Collection;

import com.agentecon.ISimulation;
import com.agentecon.agent.IAgent;
import com.agentecon.consumer.IConsumer;
import com.agentecon.firm.IFirm;
import com.agentecon.market.IStatistics;
import com.agentecon.metric.SimStats;
import com.agentecon.metric.series.IAgentType;
import com.agentecon.metric.series.TimeSeries;
import com.agentecon.metric.series.TimeSeriesCollector;

public class WealthStats extends SimStats {

	private TimeSeriesCollector wealth;

	public WealthStats(ISimulation agents, boolean individuals) {
		super(agents);
		this.wealth = new TimeSeriesCollector(individuals, getMaxDay());
	}

	@Override
	public void notifyDayEnded(IStatistics stats) {
		super.notifyDayEnded(stats);
		int day = stats.getDay();
		for (IAgent a : getAgents().getAgents()) {
			wealth.record(day, new IAgentType() {

				@Override
				public boolean isFirm() {
					return a instanceof IFirm;
				}

				@Override
				public boolean isConsumer() {
					return a instanceof IConsumer;
				}

				@Override
				public String[] getTypeKeys() {
					if (isConsumer()) {
						IConsumer c = (IConsumer) a;
						return new String[] { a.getType(), a.getType() + " " + (c.isRetired() ? "retiree" : "worker") };
					} else {
						return new String[] { a.getType() };
					}
				}

				@Override
				public String getIndividualKey() {
					return a.getName();
				}
			}, a.getWealth(stats));
		}
		wealth.flushDay(day, false);
	}

	@Override
	public Collection<TimeSeries> getTimeSeries() {
		return wealth.getTimeSeries();
	}

}
