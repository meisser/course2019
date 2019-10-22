/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.metric.variants;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.agentecon.ISimulation;
import com.agentecon.agent.Agent;
import com.agentecon.agent.AgentRef;
import com.agentecon.agent.IAgent;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IConsumerListener;
import com.agentecon.goods.Inventory;
import com.agentecon.market.IStatistics;
import com.agentecon.metric.SimStats;
import com.agentecon.metric.series.AveragingTimeSeries;
import com.agentecon.metric.series.Chart;
import com.agentecon.metric.series.TimeSeries;
import com.agentecon.util.IAverage;
import com.agentecon.util.InstantiatingConcurrentHashMap;
import com.agentecon.util.MovingAverage;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class UtilityRanking extends SimStats {

	private boolean enableTimeSeries;
	private Map<String, AveragingTimeSeries> timeSeriesAll;
	private Map<String, AveragingTimeSeries> timeSeriesAlives;
	private ArrayList<ConsumerListener> list;

	public UtilityRanking(ISimulation sim, boolean enableTimeSeries) {
		super(sim);
		this.enableTimeSeries = enableTimeSeries;
		this.list = new ArrayList<>();
		this.timeSeriesAll = new InstantiatingConcurrentHashMap<String, AveragingTimeSeries>() {

			@Override
			protected AveragingTimeSeries create(String key) {
				return new AveragingTimeSeries(key, getMaxDay());
			}
		};
		this.timeSeriesAlives = new InstantiatingConcurrentHashMap<String, AveragingTimeSeries>() {

			@Override
			protected AveragingTimeSeries create(String key) {
				return new AveragingTimeSeries(key, getMaxDay());
			}
		};
	}
	
	@Override
	public void notifyConsumerCreated(IConsumer consumer) {
		ConsumerListener listener = new ConsumerListener(consumer);
		list.add(listener);
		consumer.addListener(listener);
	}

	@Override
	public void notifyDayEnded(IStatistics stats) {
		super.notifyDayEnded(stats);
		int day = stats.getDay();
		if (enableTimeSeries) {
			Iterator<ConsumerListener> iter = list.iterator();
			while (iter.hasNext()) {
				ConsumerListener cons = iter.next();
				if (cons.isAlive()) {
					timeSeriesAlives.get(cons.getType()).add(cons.getAverage());
				}
				timeSeriesAll.get(cons.getType()).add(cons.getAverage());
			}
			for (AveragingTimeSeries ts : timeSeriesAlives.values()) {
				ts.push(day);
			}
			for (AveragingTimeSeries ts : timeSeriesAll.values()) {
				ts.push(day);
			}
		}
	}

	@Override
	public void print(PrintStream out) {
		Collections.sort(list);
		int rank = 1;
		System.out.println("Rank\tType\tId\tAvg Utility");
		for (ConsumerListener l : list) {
			out.println(rank++ + "\t" + l);
		}
	}

	public Collection<Rank> getRanking() {
		HashMap<String, Rank> ranking = new HashMap<String, Rank>();
		for (ConsumerListener listener : list) {
			Rank rank = ranking.get(listener.getType());
			if (rank == null) {
				rank = new Rank(listener.getType(), listener.getAgent());
				ranking.put(listener.getType(), rank);
			}
			rank.add(listener.getAverage(), true);
		}
		ArrayList<Rank> list = new ArrayList<>(ranking.values());
		Collections.sort(list);
		return list;
	}

	class ConsumerListener implements IConsumerListener, Comparable<ConsumerListener> {

		private AgentRef agent;
		private IAverage averageUtility;

		public ConsumerListener(IConsumer agent) {
			this.agent = agent.getReference();
			this.averageUtility = /* agent.isMortal() ? new Average() : */new MovingAverage(0.8);
		}

		public boolean isAlive() {
			IConsumer cons = (IConsumer) getAgent();
			return cons.isAlive();
		}

		public Agent getAgent() {
			return (Agent) agent.get();
		}

		public String getType() {
			return getAgent().getType();
		}

		public double getAverage() {
			return averageUtility.getAverage();
		}

		@Override
		public void notifyConsuming(IConsumer inst, int age, Inventory inv, double utility) {
			averageUtility.add(utility);
		}

		@Override
		public void notifyRetiring(IConsumer inst, int age) {
		}

		@Override
		public void notifyInvested(IConsumer inst, double amount) {
		}

		@Override
		public void notifyDivested(IConsumer inst, double amount) {
		}

		@Override
		public int compareTo(ConsumerListener o) {
			return o.averageUtility.compareTo(averageUtility);
		}

		@Override
		public String toString() {
			IAgent agent = this.agent.get();
			return agent.getType() + "\t" + agent.getAgentId() + "\t" + averageUtility.getAverage();
		}

	}

	@Override
	public Collection<? extends Chart> getCharts() {
		throw new NotImplementedException();
	}

	@Override
	public Collection<TimeSeries> getTimeSeries() {
		ArrayList<TimeSeries> all = new ArrayList<TimeSeries>();
		all.addAll(TimeSeries.prefix("All ", TimeSeries.sort(AveragingTimeSeries.unwrap(timeSeriesAll.values()))));
		all.addAll(TimeSeries.prefix("Alive ", TimeSeries.sort(AveragingTimeSeries.unwrap(timeSeriesAlives.values()))));
		return all;
	}

}
