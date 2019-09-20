package com.agentecon.exercises;

import com.agentecon.firm.production.CobbDouglasProductionWithFixedCost;
import com.agentecon.goods.Good;
import com.agentecon.market.GoodStats;
import com.agentecon.market.IMarketStatistics;
import com.agentecon.sim.IOptimalityIndicator;

public class OptimalFirmCountIndicator implements IOptimalityIndicator {
	
	protected static final double UNLIMITED = 1000;
	
	private double maximum;
	private Good variableInput;
	protected CobbDouglasProductionWithFixedCost prodFun;
	
	public OptimalFirmCountIndicator(CobbDouglasProductionWithFixedCost prodFun, Good variableInput) {
		this(prodFun, variableInput, UNLIMITED);
	}

	public OptimalFirmCountIndicator(CobbDouglasProductionWithFixedCost prodFun, Good variableInput, double maximum) {
		this.prodFun = prodFun;
		this.maximum = maximum;
		this.variableInput = variableInput;
	}

	@Override
	public double getOptimum(IMarketStatistics stats) {
		GoodStats manhours = stats.getStats(variableInput);
		double laborShare = prodFun.getWeight(variableInput).weight;
		double fixedCosts = prodFun.getFixedCost(variableInput);
		double opt = manhours.getYesterday().getTotWeight() / fixedCosts * (1 - laborShare);
		return Math.max(1.0, Math.min(maximum, opt));
	}

	@Override
	public Good getOutputGood() {
		return prodFun.getOutput();
	}

}
