package com.agentecon.metric.series;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import com.agentecon.agent.IAgent;
import com.agentecon.consumer.IConsumer;
import com.agentecon.firm.IFirm;
import com.agentecon.util.InstantiatingConcurrentHashMap;

public class TimeSeriesCollector {

	private Map<String, AveragingTimeSeries> type;
	private Map<String, AveragingTimeSeries> individual;
	private AveragingTimeSeries firms, consumers;

	public TimeSeriesCollector(int maxDay) {
		this(true, maxDay);
	}

	public TimeSeriesCollector(boolean includeIndividuals, int maxDay) {
		this.firms = new AveragingTimeSeries("Firms", maxDay);
		this.consumers = new AveragingTimeSeries("Consumers", maxDay);
		this.type = new InstantiatingConcurrentHashMap<String, AveragingTimeSeries>() {

			@Override
			protected AveragingTimeSeries create(String key) {
				return new AveragingTimeSeries(key, maxDay);
			}
		};
		if (includeIndividuals) {
			this.individual = new InstantiatingConcurrentHashMap<String, AveragingTimeSeries>() {

				@Override
				protected AveragingTimeSeries create(String key) {
					return new AveragingTimeSeries(key, maxDay);
				}
			};
		}
	}

	public void record(int day, IAgent agent, double number) {
		record(day, new IAgentType() {

			@Override
			public String getName() {
				return agent.getName();
			}

			@Override
			public String[] getTypeKeys() {
				return new String[] { agent.getType() };
			}

			@Override
			public boolean isConsumer() {
				return agent instanceof IConsumer;
			}

			@Override
			public boolean isFirm() {
				return agent instanceof IFirm;
			}

		}, number);
	}

	public void record(int day, IAgentType agent, double number) {
		if (individual != null && agent.getName() != null) {
			individual.get(agent.getName()).add(number);
		}
		for (String t : agent.getTypeKeys()) {
			type.get(t).add(number);
		}
		if (agent.isConsumer()) {
			consumers.add(number);
		}
		if (agent.isFirm()) {
			firms.add(number);
		}
	}

	public void reportZeroIfNoData() {
		if (individual != null) {
			for (AveragingTimeSeries ts : individual.values()) {
				ts.pushZeroIfNothing();
			}
		}
		for (AveragingTimeSeries ts : type.values()) {
			ts.pushZeroIfNothing();
		}
		firms.pushZeroIfNothing();
		consumers.pushZeroIfNothing();
	}

	public void flushDay(int day, boolean average) {
		if (individual != null) {
			for (AveragingTimeSeries ts : individual.values()) {
				flush(day, average, ts);
			}
		}
		for (AveragingTimeSeries ts : type.values()) {
			flush(day, average, ts);
		}
		flush(day, average, firms);
		flush(day, average, consumers);
	}

	protected void flush(int day, boolean average, AveragingTimeSeries ts) {
		if (average) {
			ts.push(day);
		} else {
			ts.pushSum(day);
		}
	}

	private Collection<TimeSeries> getFirmAndConsumerSeries() {
		ArrayList<TimeSeries> ts = new ArrayList<>();
		if (consumers.getTimeSeries().isInteresting()) {
			ts.add(consumers.getTimeSeries());
		}
		if (firms.getTimeSeries().isInteresting()) {
			ts.add(firms.getTimeSeries());
		}
		return ts;
	}

	public Collection<TimeSeries> getTimeSeries() {
		Collection<TimeSeries> ts = getFirmAndConsumerSeries();
		ts.addAll(sort(AveragingTimeSeries.unwrap(type.values())));
		if (individual != null) {
			ts.addAll(sort(AveragingTimeSeries.unwrap(individual.values())));
		}
		return ts;
	}

	private Collection<TimeSeries> sort(Collection<TimeSeries> values) {
		ArrayList<TimeSeries> list = new ArrayList<>(values.size());
		for (TimeSeries ts : values) {
			if (ts.isInteresting()) {
				list.add(ts);
			}
		}
		Collections.sort(list);
		return list;
	}
	
	public Collection<TimeSeries> getIndividualTimeSeries() {
		return sort(AveragingTimeSeries.unwrap(individual.values()));
	}

	public Collection<? extends TimeSeries> getTypeTimeSeries() {
		return sort(AveragingTimeSeries.unwrap(type.values()));
	}

}
