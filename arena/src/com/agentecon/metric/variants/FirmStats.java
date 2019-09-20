package com.agentecon.metric.variants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import com.agentecon.ISimulation;
import com.agentecon.agent.IAgent;
import com.agentecon.firm.IFirm;
import com.agentecon.firm.IFirmListener;
import com.agentecon.goods.Quantity;
import com.agentecon.metric.SimStats;
import com.agentecon.metric.series.Chart;
import com.agentecon.metric.series.TimeSeries;
import com.agentecon.production.IProducer;
import com.agentecon.production.IProducerListener;

public class FirmStats extends SimStats {

	private int day;
	private HashMap<String, FirmTimeSeries> data;

	public FirmStats(ISimulation sim) {
		super(sim);
		this.data = new HashMap<>();
	}

	@Override
	public void notifyDayStarted(int day) {
		this.day = day;
	}

	@Override
	public void notifyAgentCreated(IAgent firm) {
		if (data.containsKey(firm.getName())) {
			// skip
		} else if (firm instanceof IFirm) {
			data.put(firm.getName(), new FirmTimeSeries((IFirm) firm));
		}
	}

	@Override
	public Collection<? extends Chart> getCharts() {
		ArrayList<Chart> charts = new ArrayList<>();
		for (FirmTimeSeries ts : data.values()) {
			charts.add(ts.createChart());
		}
		return charts;
	}

	class FirmTimeSeries implements IFirmListener, IProducerListener {

		private String name;
		private TimeSeries dividends, cogs, revenue, cash;

		public FirmTimeSeries(IFirm firm) {
			this.name = firm.getName();
			this.dividends = new TimeSeries("Dividends", getMaxDay());
			this.cogs = new TimeSeries("Cost of goods sold", getMaxDay());
			this.revenue = new TimeSeries("Revenue", getMaxDay());
			this.cash = new TimeSeries("Cash", getMaxDay());
			firm.addFirmMonitor(this);
		}

		public Chart createChart() {
			if (cogs.isInteresting()) {
				return new Chart(name, "Results for producer " + name, Arrays.asList(revenue, cogs, cash, dividends));
			} else {
				return new Chart(name, "Results for firm " + name, Arrays.asList(cash, dividends));
			}
		}

		@Override
		public void reportDividend(IFirm comp, double amount) {
			this.dividends.set(day, amount);
			this.cash.set(day, comp.getMoney().getAmount());
		}

		@Override
		public void reportResults(IProducer comp, double revenue, double cogs, double profits) {
			this.cogs.set(day, cogs);
			this.revenue.set(day, revenue);
		}

		@Override
		public void notifyProduced(IProducer inst, Quantity[] inputs, Quantity output) {
		}
	}

	@Override
	public Collection<TimeSeries> getTimeSeries() {
		ArrayList<TimeSeries> list = new ArrayList<>();
		return list;
	}

}
