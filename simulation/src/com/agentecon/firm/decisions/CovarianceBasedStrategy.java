/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.firm.decisions;

import com.agentecon.util.MovingCovariance;

/**
 * Responsible for financial planning and steering the firms size by adjusting its leverage and its dividends.
 */
public class CovarianceBasedStrategy implements IFirmDecisions {

	private static final double SPENDING_FRACTION = 0.2;

	private int age = 0;
	private MovingCovariance cov;

	public CovarianceBasedStrategy() {
		this.cov = new MovingCovariance(0.95);
	}
	
	@Override
	public double calcDividend(IFinancials financials) {
		if (++age > 3) {
			double profits = financials.getExpectedRevenue() - financials.getLatestCogs();
			double sizeBeforeProfits = financials.getCash() - profits;
			this.cov.add(sizeBeforeProfits, profits);

//			System.out.println("size\t" + sizeBeforeProfits + "\tprofits\t" + profits + "\t" + cov.getCovariance() + "\t" + cov.getCorrelation());
			if (age > 20) {
				if (this.cov.getCorrelation() > 0.0) {
					return profits * 0.9;
				} else {
					return profits * 1.1;
				}
			} else {
				// don't pay dividends for the first twenty rounds
				return 0;
			}
		} else {
			return 0.0;
		}
		//
		// double targetSize = calculateTargetSize(size);
		// double targetSize = financials.getIdealCogs() / SPENDING_FRACTION;
		// // once ideal size is reached, all profits are distributed
		// double dividend = profits + size - targetSize; // once ideal size is reached
		//// System.out.println("Size " + size + " target " + targetSize + ", dividend: " + dividend);
		// return strat.calcDividend(financials) - 100;
	}
	//
	// private double calculateTargetSize(double currentSize){
	// double correlation = cov.getCorrelation(); // between -1 and 1
	// double adjustmentFactor = 1 + Math.abs(correlation/10);
	// if (correlation > 0){
	// return currentSize * adjustmentFactor; // grow
	// } else {
	// return currentSize / adjustmentFactor; // shrink
	// }
	// }

	@Override
	public double calcCogs(IFinancials financials) {
		double cash = financials.getCash();
		if (age < 10){
			return cash * SPENDING_FRACTION * 3;
		} else {
			return cash * SPENDING_FRACTION;
		}
	}

	@Override
	public IFirmDecisions duplicate() {
		throw new RuntimeException("Not implemented");
	}

}
