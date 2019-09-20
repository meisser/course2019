// Created on May 28, 2015 by Luzius Meisser

package com.agentecon.metric.variants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import com.agentecon.ISimulation;
import com.agentecon.firm.IFirm;
import com.agentecon.goods.Good;
import com.agentecon.goods.Quantity;
import com.agentecon.market.IStatistics;
import com.agentecon.metric.SimStats;
import com.agentecon.metric.series.IAgentType;
import com.agentecon.metric.series.TimeSeries;
import com.agentecon.metric.series.TimeSeriesCollector;
import com.agentecon.production.IProducer;
import com.agentecon.production.IProducerListener;
import com.agentecon.util.InstantiatingHashMap;

/**
 * Compares what the firms of one type produced with what they could have produced given the input factors they acquired.
 */
public class ProductionDetailStats extends SimStats {

	private Map<Good, TimeSeriesCollector> collectors;

	public ProductionDetailStats(ISimulation sim) {
		super(sim);
		this.collectors = new InstantiatingHashMap<Good, TimeSeriesCollector>() {

			@Override
			protected TimeSeriesCollector create(Good key) {
				return new TimeSeriesCollector(getMaxDay());
			}
		};
	}

	protected void addSingleInput(List<Quantity> list, Quantity input) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getGood().equals(input.getGood())) {
				list.set(i, new Quantity(input.getGood(), input.getAmount() + list.get(i).getAmount()));
				return;
			}
		}
		list.add(input);
	}

	@Override
	public void notifyFirmCreated(IFirm firm) {
		if (firm instanceof IProducer) {
			IProducer prod = (IProducer) firm;
			prod.addProducerMonitor(new IProducerListener() {

				@Override
				public void reportResults(IProducer inst, double revenue, double cogs, double expectedProfits) {
				}

				@Override
				public void notifyProduced(IProducer inst, Quantity[] inputs, Quantity output) {
					TimeSeriesCollector collector = collectors.get(output.getGood());
					collector.record(getDay(), new IAgentType() {

						@Override
						public boolean isFirm() {
							return true;
						}

						@Override
						public boolean isConsumer() {
							return false;
						}

						@Override
						public String[] getTypeKeys() {
							return new String[] {};
						}

						@Override
						public String getIndividualKey() {
							return inst.getName();
						}
					}, output.getAmount());
				}
			});
		}
	}

	@Override
	public void notifyDayEnded(IStatistics stats) {
		for (TimeSeriesCollector collector : collectors.values()) {
			collector.flushDay(stats.getDay(), false);
		}
	}

	@Override
	public Collection<TimeSeries> getTimeSeries() {
		ArrayList<TimeSeries> all = new ArrayList<>();
		collectors.entrySet().forEach(new Consumer<Entry<Good, TimeSeriesCollector>>() {

			@Override
			public void accept(Entry<Good, TimeSeriesCollector> t) {
				all.addAll(TimeSeries.prefix(t.getKey().getName() + " production by ", t.getValue().getTimeSeries()));
			}
		});
		return all;
	}

}
