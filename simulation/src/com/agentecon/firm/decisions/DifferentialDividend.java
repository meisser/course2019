package com.agentecon.firm.decisions;

import com.agentecon.production.PriceUnknownException;

/**
 * Choose spendings to maximize profits.
 * Pay out a constant fraction of cash holdings as dividend, thereby implicitly setting price levels.
 */
public class DifferentialDividend implements IFirmDecisions {

	public static double DIVIDEND_RATE = 0.1;
	

	public DifferentialDividend(){
	}
	
	public double calcCogs(IFinancials financials){
		double budget = financials.getCash() * 0.5;
		try {
			double idealCogs = financials.getIdealCogs();
			if (idealCogs < budget){
				return idealCogs;
			} else {
				return budget;
			}
		} catch (PriceUnknownException e) {
			return budget;
		}
	}

	@Override
	public double calcDividend(IFinancials metrics) {
		return metrics.getCash() - 800;
	}
	
	@Override
	public IFirmDecisions duplicate() {
		return new DifferentialDividend();
	}

}
