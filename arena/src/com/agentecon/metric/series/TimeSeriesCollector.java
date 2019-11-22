package com.agentecon.metric.series;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import com.agentecon.agent.IAgent;
import com.agentecon.consumer.IConsumer;
import com.agentecon.firm.IFirm;
import com.agentecon.util.InstantiatingConcurrentHashMap;
import com.agentecon.util.InstantiatingHashMap;

public class TimeSeriesCollector {

	private Map<String, AveragingTimeSeries> type;
	private Map<IndividualKey, AveragingTimeSeries> individual;
	private AveragingTimeSeries firms, consumers;

	public TimeSeriesCollector(int maxDay) {
		this(true, maxDay);
	}

	public TimeSeriesCollector(boolean includeIndividuals, int maxDay) {
		this(includeIndividuals, maxDay, false);
	}

	public TimeSeriesCollector(boolean includeIndividuals, int maxDay, boolean integral) {
		this.firms = integral ? new AggregatingTimeSeries("Firms", maxDay) : new AveragingTimeSeries("Firms", maxDay);
		this.consumers = integral ? new AggregatingTimeSeries("Consumers", maxDay) : new AveragingTimeSeries("Consumers", maxDay);
		this.type = new InstantiatingConcurrentHashMap<String, AveragingTimeSeries>() {

			@Override
			protected AveragingTimeSeries create(String key) {
				if (integral) {
					return new AggregatingTimeSeries(key, maxDay);
				} else {
					return new AveragingTimeSeries(key, maxDay);
				}
			}
		};
		if (includeIndividuals) {
			this.individual = new InstantiatingConcurrentHashMap<IndividualKey, AveragingTimeSeries>() {

				@Override
				protected AveragingTimeSeries create(IndividualKey key) {
					if (integral) {
						return new AggregatingTimeSeries(key.getName(), maxDay);
					} else {
						return new AveragingTimeSeries(key.getName(), maxDay);
					}
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
			individual.get(new IndividualKey(agent.getName(), agent.getTypeKeys())).add(number);
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

	public Collection<TimeSeries> createTypeAveragesFromIndividualSeries() {
		HashMap<String, ArrayList<AveragingTimeSeries>> all = new InstantiatingHashMap<String, ArrayList<AveragingTimeSeries>>() {

			@Override
			protected ArrayList<AveragingTimeSeries> create(String key) {
				return new ArrayList<AveragingTimeSeries>();
			}

		};
		individual.forEach(new BiConsumer<IndividualKey, AveragingTimeSeries>() {

			@Override
			public void accept(IndividualKey t, AveragingTimeSeries u) {
				for (String type : t.getTypeKeys()) {
					all.get(type).add(u);
				}
			}
		});
		ArrayList<TimeSeries> aggregates = new ArrayList<TimeSeries>();
		all.forEach(new BiConsumer<String, ArrayList<AveragingTimeSeries>>() {

			@Override
			public void accept(String type, ArrayList<AveragingTimeSeries> lists) {
				TimeSeries ts = new TimeSeries(type, 0);
				for (AveragingTimeSeries individual : lists) {
					ts = ts.add(individual.getTimeSeries(), type);
				}
				aggregates.add(ts.divideBy(lists.size()));
			}
		});
		return aggregates;
	}

	public Collection<TimeSeries> getTimeSeries() {
		Collection<TimeSeries> ts = getFirmAndConsumerSeries();
		ts.addAll(sort(AveragingTimeSeries.unwrap(type.values())));
		if (individual != null) {
			ts.addAll(sort(AveragingTimeSeries.unwrap(individual.values())));
		}
		return ts;
	}

	private Collection<? extends TimeSeries> sort(Collection<TimeSeries> values) {
		ArrayList<TimeSeries> list = new ArrayList<>(values.size());
		for (TimeSeries ts : values) {
			if (ts.isInteresting()) {
				list.add(ts);
			}
		}
		Collections.sort(list);
		return list;
	}

	public Collection<? extends TimeSeries> getTypeTimeSeries() {
		return sort(AveragingTimeSeries.unwrap(type.values()));
	}

	class IndividualKey {

		private String key;
		private String[] types;

		public IndividualKey(String name, String[] typeKeys) {
			this.key = name;
			this.types = typeKeys;
		}

		public String getName() {
			return key;
		}

		public String[] getTypeKeys() {
			return types;
		}

		@Override
		public int hashCode() {
			return key.hashCode();
		}

		@Override
		public boolean equals(Object o) {
			return key.equals(((IndividualKey)o).key);
		}
		
		@Override
		public String toString() {
			return key.toString();
		}

	}

}
