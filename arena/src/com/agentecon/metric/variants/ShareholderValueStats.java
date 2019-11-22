package com.agentecon.metric.variants;

import java.util.Collection;
import java.util.HashMap;

import com.agentecon.ISimulation;
import com.agentecon.agent.IAgent;
import com.agentecon.consumer.IConsumer;
import com.agentecon.firm.IFirm;
import com.agentecon.firm.IFirmListener;
import com.agentecon.firm.Ticker;
import com.agentecon.goods.Good;
import com.agentecon.market.IMarket;
import com.agentecon.market.IMarketListener;
import com.agentecon.market.IStatistics;
import com.agentecon.metric.SimStats;
import com.agentecon.metric.series.TickerType;
import com.agentecon.metric.series.TimeSeries;
import com.agentecon.metric.series.TimeSeriesCollector;
import com.agentecon.util.InstantiatingHashMap;

public class ShareholderValueStats extends SimStats implements IMarketListener {

	protected TimeSeriesCollector collector;
	private HashMap<Good, Holding> holdings;

	public ShareholderValueStats(ISimulation agents, boolean integral, boolean details) {
		super(agents);
		this.holdings = new InstantiatingHashMap<Good, ShareholderValueStats.Holding>() {

			@Override
			protected Holding create(Good key) {
				return new Holding();
			}
		};
		this.collector = new TimeSeriesCollector(details, getMaxDay(), integral);
	}

	@Override
	public void notifyStockMarketOpened(IMarket market) {
		market.addMarketListener(this);
	}

	@Override
	public void notifyTraded(IAgent seller, IAgent buyer, Good good, double quantity, double payment) {
		if (seller instanceof IConsumer) {
			assert !(buyer instanceof IConsumer);
			Holding h = holdings.get(good);
			double gains = h.notifySold(quantity, payment);
			collector.record(getDay(), new TickerType((Ticker) good), gains);
		} else if (buyer instanceof IConsumer) {
			Holding h = holdings.get(good);
			h.notifyBought(quantity, payment);
		}
	}

	@Override
	public void notifyFirmCreated(IFirm firm) {
		firm.addFirmMonitor(new IFirmListener() {

			@Override
			public void reportDividend(IFirm comp, double amount) {
				collector.record(getDay(), comp, amount * comp.getShareRegister().getConsumerOwnedShare());
			}

		});
	}

	@Override
	public void notifyDayEnded(IStatistics stats) {
		super.notifyDayEnded(stats);
		int day = stats.getDay();
		collector.reportZeroIfNoData();
		collector.flushDay(day, false);
	}

	@Override
	public Collection<TimeSeries> getTimeSeries() {
		return collector.getTimeSeries();
	}

	class Holding {

		private double units;
		private double acquisitionCosts;

		public void notifyBought(double quantity, double payment) {
			this.units += quantity;
			this.acquisitionCosts += payment;
		}

		public double notifySold(double quantity, double payment) {
			double costs = quantity / units * acquisitionCosts;
			this.units -= quantity;
			this.acquisitionCosts -= costs;
			return payment - costs;
		}

	}

}
