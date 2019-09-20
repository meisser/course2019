package com.agentecon.firm.decisions;

import com.agentecon.firm.production.CobbDouglasProduction;

public class ExpectedRevenueBasedStrategy implements IFirmDecisions {

	private double laborshare;
	private double profitshare;

	public ExpectedRevenueBasedStrategy(double laborshare) {
		this.laborshare = laborshare;
		this.profitshare = 1.0 - laborshare;
	}
	
	public ExpectedRevenueBasedStrategy(CobbDouglasProduction prodFun) {
		this(prodFun.getTotalConsumedWeight());
	}

	protected double getLaborShare(){
		return laborshare;
	}

	@Override
	public IFirmDecisions duplicate() {
		return new ExpectedRevenueBasedStrategy(laborshare);
	}

	public double calcCogs(IFinancials financials) {
		return financials.getCash() / 5.0 + financials.getFixedCosts();
	}

	@Override
	public double calcDividend(IFinancials financials) {
		return financials.getExpectedRevenue() * profitshare - financials.getFixedCosts();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " with profitshare " + profitshare;
	}

}
