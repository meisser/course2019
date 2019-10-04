package com.agentecon.firm.production;

import com.agentecon.consumer.Weight;
import com.agentecon.goods.Good;
import com.agentecon.goods.Inventory;
import com.agentecon.production.IPriceProvider;
import com.agentecon.production.PriceUnknownException;

public class CobbDouglasProduction extends AbstractProductionFunction {

	public static final double PRODUCTIVITY = 10;

	private double constantFactor;
	private double inputMultiplier;

	public CobbDouglasProduction(Good output, Weight... weights) {
		this(output, PRODUCTIVITY, weights);
	}

	public CobbDouglasProduction(Good output, double constantFactor, Weight... weights) {
		this(output, constantFactor, 1.0, weights);
	}

	public CobbDouglasProduction(Good output, double constantFactor, double inputMultiplier, Weight... weights) {
		super(output, weights);
		this.constantFactor = constantFactor;
		this.inputMultiplier = inputMultiplier;
	}

	public CobbDouglasProduction scaleInputsDownAndOutputsUp(double factor) {
		return new CobbDouglasProduction(output, constantFactor * factor, 1.0 / factor, getInputWeigths());
	}

	public CobbDouglasProduction adjustReturnsToScale(double returnsToScale) {
		double current = getReturnsToScale();
		double factor = returnsToScale / current;
		Weight[] newWeights = new Weight[inputs.length];
		for (int i = 0; i < newWeights.length; i++) {
			newWeights[i] = new Weight(inputs[i].good, inputs[i].weight * factor);
		}
		return new CobbDouglasProduction(getOutput(), constantFactor, newWeights);
	}

	public double getReturnsToScale() {
		return super.getTotalWeight();
	}
	
	public double getInputMultiplier() { 
		return inputMultiplier;
	}

	/**
	 * This is equivalent to the share of revenue that goes into acquiring non-capital inputs
	 */
	public double getReturnsToScaleExcludingCapital() {
		return super.getTotalConsumedWeight();
	}

	public double getProfitAndCapitalShare() {
		return 1.0 - super.getTotalConsumedWeight();
	}

	@Override
	public double useInputs(Inventory inventory) {
		double production = 1.0;
		for (Weight input : inputs) {
			double amount = getInput(inventory, input.good);
			production *= Math.pow(amount, input.weight);
			if (!input.capital) {
				inventory.getStock(input.good).consume();
			}
		}
		production = Math.max(constantFactor * production, 1.0);
		return production;
	}

	@Override
	public double getCostOfMaximumProfit(Inventory inv, IPriceProvider prices) throws PriceUnknownException {
		if (getReturnsToScaleExcludingCapital() >= 1.0) {
			// increasing returns to scale
			return Double.MAX_VALUE;
		} else {
			double totWeight = getTotalConsumedWeight();
			double outprice = prices.getPriceBelief(output);
			double prod = getCBHelperProduct(getMultiplier(inv), prices);
			double factor = Math.pow(outprice * prod, 1 / (1 - totWeight));
			return totWeight * factor;
		}
	}

	private double getMultiplier(Inventory inv) {
		double multiplier = constantFactor;
		for (Weight w : inputs) {
			if (w.capital) {
				multiplier *= Math.pow(getInput(inv, w.good), w.weight);
			}
		}
		return multiplier;
	}

	private double getInput(Inventory inv, Good good) {
		return inv.getStock(good).getAmount() * inputMultiplier;
	}

	private double getCBHelperProduct(double constantFactor, IPriceProvider prices) throws PriceUnknownException {
		double tot = constantFactor;
		for (Weight in : inputs) {
			if (!in.capital) {
				double price = prices.getPriceBelief(in.good);
				if (Double.isInfinite(price)) {
					// skip, not obtainable
				} else {
					tot *= Math.pow(in.weight / price, in.weight);
				}
			}
		}
		return tot;
	}

	@Override
	public double getExpenses(Good good, IPriceProvider prices, double totalSpendings) throws PriceUnknownException {
		double offerPerWeight = totalSpendings / getTotalConsumedWeight();
		return offerPerWeight * getWeight(good).weight;
	}

	@Override
	public double getFixedCosts(IPriceProvider prices) throws PriceUnknownException {
		return 0.0;
	}

}
