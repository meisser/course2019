package com.agentecon.metric.variants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import com.agentecon.ISimulation;
import com.agentecon.consumer.IConsumer;
import com.agentecon.firm.IFirm;
import com.agentecon.firm.IShareholder;
import com.agentecon.firm.Position;
import com.agentecon.market.IMarketStatistics;
import com.agentecon.market.IStatistics;
import com.agentecon.metric.SimStats;
import com.agentecon.metric.series.Chart;
import com.agentecon.metric.series.TimeSeries;
import com.agentecon.production.PriceUnknownException;

public class ValuationStats extends SimStats {

	private TimeSeries innerValue;
	private TimeSeries outerValue;
	private TimeSeries valueRatio;

	public ValuationStats(ISimulation agents) {
		super(agents);
		this.innerValue = new TimeSeries("Inner Value", getMaxDay());
		this.outerValue = new TimeSeries("Outer Value", getMaxDay());
		this.valueRatio = new TimeSeries("Ratio", getMaxDay());
	}

	@Override
	public void notifyDayEnded(IStatistics stats) {
		Collection<? extends IShareholder> holders = getAgents().getShareholders();
		double outerValue = 0.0;
		double innerValue = 0.0;
		IMarketStatistics stockStats = stats.getStockMarketStats();
		for (IShareholder holder : holders) {
			if (holder instanceof IFirm) {
				// innerValue += holder.getMoney().getAmount();
			}
			for (Position pos : holder.getPortfolio().getPositions()) {
				try {
					double value = pos.getAmount() * stockStats.getPriceBelief(pos.getTicker());
					IFirm heldCompany = getAgents().getFirm(pos.getTicker());
					if (holder instanceof IFirm) {
						// only count real companies they hold
						if (heldCompany instanceof IFirm) {
							innerValue += value;
						}
					} else {
						assert holder instanceof IConsumer;
						// Count all financial companies for outer value
						if (heldCompany instanceof IShareholder) {
							outerValue += value;
						}
					}
				} catch (PriceUnknownException e) {
				}
			}
		}
		if (!Double.isNaN(innerValue) && !Double.isNaN(outerValue) && innerValue != 0.0) {
			int day = stats.getDay();
			this.innerValue.set(day, innerValue);
			this.outerValue.set(day, outerValue);
			this.valueRatio.set(day, outerValue / innerValue);
			// double realOuterValue = outerValue / market.getIndex();
			// if (!Double.isNaN(realOuterValue)) {
			// this.realOuterValue.set(day, realOuterValue);
			// }
			// System.out.println(day + "\t" + innerValue + "\t" + outerValue + "\t" + outerValue / innerValue + "\t" + realOuterValue);
		}
	}

	@Override
	public Collection<? extends Chart> getCharts() {
		return Arrays.asList(new Chart("Financial Sector Value", "Outer: Combined market cap of all financial firms. Inner: non-financial shares they own.", innerValue, outerValue),
				new Chart("Financial Sector Value Ratio", "Outer: Combined market cap of all financial firms. Inner: non-financial shares they own.", valueRatio));
	}

	@Override
	public Collection<TimeSeries> getTimeSeries() {
		ArrayList<TimeSeries> list = new ArrayList<>();
		list.add(innerValue);
		list.add(outerValue);
		return list;
	}

}
