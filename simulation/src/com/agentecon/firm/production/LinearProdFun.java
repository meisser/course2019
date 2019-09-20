package com.agentecon.firm.production;

import com.agentecon.consumer.Weight;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Inventory;
import com.agentecon.learning.IBelief;
import com.agentecon.production.IPriceProvider;

public class LinearProdFun extends AbstractProductionFunction {

	public LinearProdFun(Good output, Weight weight) {
		super(output, weight);
	}

	@Override
	public double useInputs(Inventory inventory) {
		double production = 0.0;
		for (Weight input : inputs) {
			IStock in = inventory.getStock(input.good);
			production += input.weight * Math.max(1.0, input.capital ? in.getAmount() : in.consume());
		}
		return production;
	}

	public boolean shouldProduce(IBelief inputPrice, IBelief outputPrice) {
		double weight = inputs[0].weight;
		return weight * outputPrice.getValue() > inputPrice.getValue();
	}

	@Override
	public double getCostOfMaximumProfit(Inventory inv, IPriceProvider prices) {
		return Double.MAX_VALUE; // actually wrong, could also be zero depending on output price
	}

	@Override
	public double getExpenses(Good good, IPriceProvider price, double totalSpendings) {
		return Double.MAX_VALUE;
	}
	
	@Override
	public double getFixedCosts(IPriceProvider prices) {
		return 0.0;
	}
}
