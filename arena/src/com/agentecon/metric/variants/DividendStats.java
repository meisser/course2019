package com.agentecon.metric.variants;

import java.util.Collection;

import com.agentecon.ISimulation;
import com.agentecon.firm.IFirm;
import com.agentecon.firm.IFirmListener;
import com.agentecon.market.IStatistics;
import com.agentecon.metric.SimStats;
import com.agentecon.metric.series.TimeSeries;
import com.agentecon.metric.series.TimeSeriesCollector;

public class DividendStats extends SimStats {

	private TimeSeriesCollector collector;
	private boolean consumer;

	public DividendStats(ISimulation agents, boolean consumer, boolean details) {
		super(agents);
		this.consumer = consumer;
		this.collector = new TimeSeriesCollector(details, getMaxDay());
	}

	@Override
	public void notifyFirmCreated(IFirm firm) {
		firm.addFirmMonitor(new IFirmListener() {

			@Override
			public void reportDividend(IFirm comp, double amount) {
				if (consumer) {
					double priceLevel = getStats().getGoodsMarketStats().getPriceIndex();
					if (!Double.isNaN(priceLevel)) {
						collector.record(getDay(), comp, amount * comp.getShareRegister().getConsumerOwnedShare() / priceLevel);
					}
				} else {
					collector.record(getDay(), comp, amount);
				}
			}

		});
	}

	@Override
	public void notifyDayEnded(IStatistics stats) {
		super.notifyDayEnded(stats);
		int day = stats.getDay();
		collector.reportZeroIfNoData();
		collector.flushDay(day, true);
	}

	@Override
	public Collection<TimeSeries> getTimeSeries() {
		return collector.getTimeSeries();
	}

}
