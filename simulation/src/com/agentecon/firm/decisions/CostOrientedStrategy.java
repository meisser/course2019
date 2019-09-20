package com.agentecon.firm.decisions;

import com.agentecon.production.PriceUnknownException;

public class CostOrientedStrategy implements IFirmDecisions {

	public CostOrientedStrategy() {
	}

	@Override
	public double calcDividend(IFinancials metrics) {
		try {
			double targetCash = metrics.getIdealCogs() * 5;
			return Math.max(0.0, metrics.getCash() - targetCash) / 5;
		} catch (PriceUnknownException e) {
			return 0.0;
		}
	}

	@Override
	public double calcCogs(IFinancials financials) {
		double budget = Math.max(financials.getFixedCosts() * 2, financials.getCash() / 5);
		if (financials.getCash() < budget / 2) {
			return 0.0;
		} else {
			return budget;
		}
	}

	@Override
	public IFirmDecisions duplicate() {
		return this;
	}

}
