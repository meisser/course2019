package com.agentecon.firm.production;

import com.agentecon.consumer.Weight;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Inventory;
import com.agentecon.goods.Quantity;
import com.agentecon.production.IPriceProvider;
import com.agentecon.production.PriceUnknownException;

public class CobbDouglasProductionWithFixedCost extends CobbDouglasProduction {

	private Quantity fixedCost;

	public CobbDouglasProductionWithFixedCost(Good output, Quantity fixedCost, Weight... weights) {
		this(output, PRODUCTIVITY, fixedCost, weights);
	}

	public CobbDouglasProductionWithFixedCost(Good output, double constantFactor, Quantity fixedCost, Weight... weights) {
		super(output, constantFactor, weights);
		this.fixedCost = fixedCost;
		assert !getWeight(fixedCost.getGood()).capital : "Fixed costs for capital goods not yet supported";
	}

	@Override
	public double getFixedCost(Good good) {
		return fixedCost.getGood().equals(good) ? fixedCost.getAmount() : 0.0;
	}

	@Override
	public double getFixedCosts(IPriceProvider prices) throws PriceUnknownException {
		return prices.getPriceBelief(fixedCost);
	}

	/**
	 * This does not change with fixed costs as the fixed costs are deducted from profits.
	 */
	public double getReturnsToScaleExcludingCapital() {
		return super.getReturnsToScaleExcludingCapital();
	}

	@Override
	public double useInputs(Inventory inventory) {
		IStock fixedGood = inventory.getStock(fixedCost.getGood());
		if (fixedGood.getAmount() <= fixedCost.getAmount()) {
			fixedGood.consume();
			return 0.0;
		} else {
			fixedGood.remove(fixedCost.getAmount());
			return super.useInputs(inventory);
		}
	}

	@Override
	public double getCostOfMaximumProfit(Inventory inv, IPriceProvider prices) throws PriceUnknownException {
		double costsExcludingFixedCosts = super.getCostOfMaximumProfit(inv, prices);
		if (costsExcludingFixedCosts == Double.MAX_VALUE) {
			return costsExcludingFixedCosts;
		} else {
			double profitBeforeFixedCosts = costsExcludingFixedCosts / getTotalConsumedWeight() * getProfitAndCapitalShare();
			double fixedCosts = getFixedCosts(prices);
			if (fixedCosts >= profitBeforeFixedCosts) {
				return 0.0;
			} else {
				return costsExcludingFixedCosts + fixedCosts;
			}
		}
	}

	@Override
	public double getExpenses(Good good, IPriceProvider prices, double totalSpendings) throws PriceUnknownException {
		double fixedCosts = getFixedCosts(prices);
		double spendingsAfterCoveringFixedCosts = totalSpendings - fixedCosts;
		if (good.equals(fixedCost.getGood())) {
			return super.getExpenses(good, prices, spendingsAfterCoveringFixedCosts) + fixedCosts;
		} else {
			return super.getExpenses(good, prices, spendingsAfterCoveringFixedCosts);
		}
	}

}
