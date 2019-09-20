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
import com.agentecon.firm.IFirm;
import com.agentecon.firm.IFirmListener;
import com.agentecon.market.IStatistics;
import com.agentecon.metric.SimStats;
import com.agentecon.metric.series.AveragingTimeSeries;
import com.agentecon.metric.series.Chart;
import com.agentecon.metric.series.TimeSeries;
import com.agentecon.util.InstantiatingConcurrentHashMap;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class FirmRanking extends SimStats {

	private boolean enableTimeSeries;
	private Map<String, AveragingTimeSeries> timeSeries;
	private ArrayList<FirmListener> list;

	public FirmRanking(ISimulation sim, boolean enableTimeSeries) {
		super(sim);
		this.enableTimeSeries = enableTimeSeries;
		this.list = new ArrayList<>();
		this.timeSeries = new InstantiatingConcurrentHashMap<String, AveragingTimeSeries>() {

			@Override
			protected AveragingTimeSeries create(String key) {
				return new AveragingTimeSeries(key, getMaxDay());
			}
		};
	}

	public double getPriceIndex() {
		return getStats().getGoodsMarketStats().getPriceIndex();
	}

	@Override
	public void notifyFirmCreated(IFirm firm) {
		FirmListener listener = new FirmListener(firm);
		list.add(listener);
		firm.addListener(listener);
	}

	@Override
	public void notifyDayEnded(IStatistics stats) {
		super.notifyDayEnded(stats);
		int day = stats.getDay();
		if (enableTimeSeries) {
			Iterator<FirmListener> iter = list.iterator();
			while (iter.hasNext()) {
				FirmListener cons = iter.next();
				timeSeries.get(cons.getType()).add(cons.getTotalDividends());
			}
			for (AveragingTimeSeries ts : timeSeries.values()) {
				ts.pushSum(day);
			}
		}
	}

	@Override
	public void print(PrintStream out) {
		Collections.sort(list);
		int rank = 1;
		System.out.println("Rank\tType\tId\tDividends");
		for (FirmListener l : list) {
			out.println(rank++ + "\t" + l);
		}
	}

	public Collection<Rank> getRanking() {
		HashMap<String, Rank> ranking = new HashMap<String, Rank>();
		for (FirmListener listener : list) {
			Rank rank = ranking.get(listener.getType());
			if (rank == null) {
				rank = new Rank(listener.getType(), listener.getAgent());
				ranking.put(listener.getType(), rank);
			}
			rank.add(listener.getTotalDividends(), false);
		}
		ArrayList<Rank> list = new ArrayList<>(ranking.values());
		Collections.sort(list);
		for (Rank rank : list) {
			rank.roundScore();
		}
		return list;
	}

	class FirmListener implements IFirmListener, Comparable<FirmListener> {

		private AgentRef agent;
		private double totalDividends;

		public FirmListener(IFirm agent) {
			this.agent = agent.getReference();
			this.totalDividends = 0.0;
		}

		public Agent getAgent() {
			return (Agent) agent.get();
		}

		public String getType() {
			return getAgent().getType();
		}

		public double getTotalDividends() {
			return totalDividends;
		}

		@Override
		public void reportDividend(IFirm inst, double amount) {
			double consumerOwnership = inst.getShareRegister().getConsumerOwnedShare();
			double weightedDividend = consumerOwnership * amount / getPriceIndex();
			if (Double.isFinite(weightedDividend)) {
				this.totalDividends += weightedDividend;
			}
		}

		@Override
		public int compareTo(FirmListener o) {
			return Double.compare(totalDividends, o.totalDividends);
		}

		@Override
		public String toString() {
			IAgent agent = this.agent.get();
			return agent.getType() + "\t" + agent.getAgentId() + "\t" + getTotalDividends();
		}

	}

	@Override
	public Collection<? extends Chart> getCharts() {
		throw new NotImplementedException();
	}

	@Override
	public Collection<TimeSeries> getTimeSeries() {
		return TimeSeries.sort(AveragingTimeSeries.unwrap(timeSeries.values()));
	}

}
