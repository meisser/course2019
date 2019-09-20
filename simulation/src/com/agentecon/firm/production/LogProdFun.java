package com.agentecon.firm.production;

import com.agentecon.consumer.Weight;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Inventory;
import com.agentecon.production.IPriceProvider;
import com.agentecon.production.PriceUnknownException;

public class LogProdFun extends AbstractProductionFunction {

	private static final double ADJUSTMENT = 1.0;

	public LogProdFun(Good output, Weight... weights) {
		super(output, weights);
	}

	@Override
	public double useInputs(Inventory inventory) {
		double production = 1.0;
		for (Weight input : inputs) {
			IStock in = inventory.getStock(input.good);
			production += input.weight * Math.log(ADJUSTMENT + (input.capital ? in.getAmount() : in.consume()));
		}
		return production;
	}
	
	@Override
	public double getCostOfMaximumProfit(Inventory inv, IPriceProvider prices) throws PriceUnknownException {
		// is this really correct?
		double totWeight = getTotalWeight();
		double outprice = prices.getPriceBelief(output);
		return outprice * totWeight;
	}

	@Override
	public double getExpenses(Good good, IPriceProvider price, double totalSpendings) throws PriceUnknownException {
		double offerPerWeight = totalSpendings / getTotalConsumedWeight();
		Weight weight = getWeight(good);
		assert !weight.capital : "this function does not help you with expenses on capital goods";
		return offerPerWeight * weight.weight - price.getPriceBelief(good) * ADJUSTMENT;
	}
	
	@Override
	public double getFixedCosts(IPriceProvider prices) {
		return 0.0;
	}

}
