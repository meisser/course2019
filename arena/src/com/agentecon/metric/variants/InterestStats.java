package com.agentecon.metric.variants;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import com.agentecon.ISimulation;
import com.agentecon.finance.IInterest;
import com.agentecon.metric.SimStats;
import com.agentecon.metric.series.TimeSeries;

public class InterestStats extends SimStats {

	private IInterest interest;
	private TimeSeries interestRate;
	private TimeSeries discountRate;

	public InterestStats(ISimulation sim) {
		super(sim);
		this.interest = sim.getConfig().getInterest();
		this.interestRate = new TimeSeries("Interest Rate", getMaxDay());
		this.discountRate = new TimeSeries("Discount Rate", getMaxDay());
	}
	
	@Override
	public void notifyDayEnded(int day) {
		if (interest != null) {
			this.interestRate.set(day, this.interest.getInterestRate());
			this.discountRate.set(day, this.interest.getAverageDiscountRate());
		}
	}

	@Override
	public Collection<TimeSeries> getTimeSeries() {
		if (interest == null) {
			return Collections.EMPTY_LIST;
		} else {
			return Arrays.asList(discountRate, interestRate);
		}
	}

}
