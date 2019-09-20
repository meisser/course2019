package com.agentecon.metric.variants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.agentecon.ISimulation;
import com.agentecon.agent.IAgent;
import com.agentecon.firm.IFirm;
import com.agentecon.firm.IShareholder;
import com.agentecon.firm.Portfolio;
import com.agentecon.firm.Position;
import com.agentecon.firm.Ticker;
import com.agentecon.market.IStatistics;
import com.agentecon.metric.SimStats;
import com.agentecon.metric.series.Chart;
import com.agentecon.metric.series.Line;
import com.agentecon.metric.series.TimeSeries;
import com.agentecon.util.InstantiatingConcurrentHashMap;
import com.agentecon.util.InstantiatingHashMap;

public class OwnershipStats extends SimStats {

	private boolean details;
	private Map<String, Map<String, TimeSeries>> structure;

	public OwnershipStats(ISimulation agents, boolean details) {
		super(agents);
		this.details = details;
		this.structure = new InstantiatingConcurrentHashMap<String, Map<String, TimeSeries>>() {

			@Override
			protected Map<String, TimeSeries> create(String key) {
				return new InstantiatingConcurrentHashMap<String, TimeSeries>() {

					@Override
					protected TimeSeries create(String key) {
						return new TimeSeries(key, new Line(), getMaxDay());
					}
				};
			}
		};
	}

	@Override
	public void notifyDayEnded(IStatistics stats) {
		super.notifyDayEnded(stats);
		int day = stats.getDay();
		if (day % 10 == 0) {
			HashMap<String, OwnershipStructure> owners = new InstantiatingHashMap<String, OwnershipStats.OwnershipStructure>() {

				@Override
				protected OwnershipStructure create(String key) {
					return new OwnershipStructure(key);
				}
			};
			HashMap<Ticker, Double> total = new HashMap<>();
			for (IShareholder pc : getAgents().getShareholders()) {
//				String ownerType = "Unclaimed Inheritance";
				if (pc instanceof IAgent) {
					String ownerType = ((IAgent) pc).getType();
					// boolean isRetiree = pc instanceof IConsumer && ((IConsumer) pc).isRetired();
					Portfolio pf = pc.getPortfolio();
					for (Position pos : pf.getPositions()) {
						Double before = total.get(pos.getTicker());
						double beforeValue = before == null ? 0.0 : before.doubleValue();
						total.put(pos.getTicker(), beforeValue + pos.getAmount());
						String ownedType = details ? pos.getTicker().getName() : pos.getTicker().getType();
						owners.get(ownedType).include(ownerType, pos.getAmount());
						// if (isRetiree) {
						// owners.get(ownedType).include("Retiree", pos.getAmount());
						// }
					}
				}
			}
			for (IFirm firm : getAgents().getFirms()) {
				double selfOwned = firm.getShareRegister().getTotalShareCount() - firm.getShareRegister().getFreeFloatShares();
				owners.get(details ? firm.getTicker().getName() : firm.getType()).include("self", selfOwned);
			}
//			total.forEach(new BiConsumer<Ticker, Double>() {
//
//				@Override
//				public void accept(Ticker t, Double u) {
//					IFirm firm = (IFirm) getAgents().getAgent(t.getNumer());
//					assert Math.abs(u.doubleValue() - firm.getShareRegister().getFreeFloatShares()) < 0.0001;
//				}
//			});
			for (OwnershipStructure os : owners.values()) {
				os.push(day, structure.get(os.type));
			}
		}
	}

	public class OwnershipStructure {

		private String type;
		private Map<String, Double> owners;

		public OwnershipStructure(String type) {
			this.type = type;
			this.owners = new InstantiatingHashMap<String, Double>() {

				@Override
				protected Double create(String key) {
					return 0.0d;
				}
			};
		}

		public void push(int day, Map<String, TimeSeries> hashMap) {
			for (Map.Entry<String, Double> e : owners.entrySet()) {
				hashMap.get(e.getKey()).set(day, e.getValue());
			}
		}

		public void include(String ownerType, double amount) {
			owners.put(ownerType, owners.get(ownerType) + amount);
		}

	}

	@Override
	public Collection<? extends Chart> getCharts() {
		ArrayList<Chart> charts = new ArrayList<>();
		for (Map.Entry<String, Map<String, TimeSeries>> owned : structure.entrySet()) {
			Collection<TimeSeries> ts = owned.getValue().values();
			Chart ch = new Chart(owned.getKey() + " Owners", "Owners of an average firm of type " + owned.getKey(), ts);
			ch.setStacking("percent");
			charts.add(ch);
		}
		return charts;
	}

	@Override
	public Collection<TimeSeries> getTimeSeries() {
		ArrayList<TimeSeries> list = new ArrayList<>();
		for (Map.Entry<String, Map<String, TimeSeries>> owned : structure.entrySet()) {
			Collection<TimeSeries> ts = owned.getValue().values();
			list.addAll(TimeSeries.prefix(owned.getKey() + " owner", ts));
		}
		return list;
	}

}
