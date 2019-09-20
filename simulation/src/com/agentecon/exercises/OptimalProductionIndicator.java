package com.agentecon.exercises;

import java.util.ArrayList;

import com.agentecon.firm.production.CobbDouglasProductionWithFixedCost;
import com.agentecon.goods.Good;
import com.agentecon.goods.Quantity;
import com.agentecon.market.IMarketStatistics;
import com.agentecon.production.IProductionFunction;
import com.agentecon.util.Average;

public class OptimalProductionIndicator extends OptimalFirmCountIndicator {
	
	private Quantity fixed;
	
	public OptimalProductionIndicator(Quantity fixed, CobbDouglasProductionWithFixedCost prodFun, Good manHour) {
		this(fixed, prodFun, manHour, UNLIMITED);
	}

	public OptimalProductionIndicator(Quantity fixed, CobbDouglasProductionWithFixedCost prodFun, Good manHour, double maxFirms) {
		super(prodFun, manHour, maxFirms);
		this.fixed = fixed;
	}

	@Override
	public double getOptimum(IMarketStatistics stats) {
		double firms = super.getOptimum(stats);
		IProductionFunction prodFun = this.prodFun;
		ArrayList<Quantity> adjusted = new ArrayList<>();
		for (Good input: prodFun.getInputs()) {
			if (input.equals(fixed.getGood())) {
				adjusted.add(fixed);
			} else {
				Average latest = stats.getStats(input).getYesterday();
				adjusted.add(new Quantity(input, latest.getTotWeight() / firms));
			}
		}
		Quantity production = prodFun.calculateOutput(adjusted.toArray(new Quantity[adjusted.size()]));
		assert production.getGood().equals(getOutputGood());
		return production.getAmount() * firms;
	}

}
