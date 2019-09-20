package com.agentecon.consumer;

import java.util.Collection;
import java.util.HashSet;

import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Inventory;
import com.agentecon.market.IOffer;

public class CobbDouglasUtil extends AbstractWeightedUtil {

	public CobbDouglasUtil(Weight... weights) {
		super(weights);
		normalizeWeights(1.0);
	}

	@Override
	public double getUtility(Collection<IStock> quantities) {
		int found = 0;
		double util = 1.0;
		for (IStock s : quantities) {
			double weight = getWeight(s.getGood());
			if (weight != 0.0){
				found++;
				util *= Math.pow(s.getAmount(), weight); 
			}
		}
		if (found < getWeightCount()) {
			return 0;
		} else {
			return util;
		}
	}

	public double[] getOptimalAllocation(Inventory inv, Collection<IOffer> prices) {
		return getOptimalAllocation(inv, prices, new HashSet<Good>());
	}

	private double[] getOptimalAllocation(Inventory inv, Collection<IOffer> prices, HashSet<Good> ignorelist) {
		IStock money = inv.getMoney();
		double endowment = money.getAmount();
		double totweight = getWeight(money.getGood());

		// Note that goods in the inventory that have no price can be safely ignored
		// as one cannot buy or sell them anyway. Also, they do not influence the
		// quantities of other goods with log utility.
		for (IOffer offer : prices) {
			if (!ignorelist.contains(offer.getGood())) {
				endowment += inv.getStock(offer.getGood()).getAmount() * offer.getPrice().getPrice();
				totweight += getWeight(offer.getGood());
			}
		}
		double[] targetAmounts = new double[prices.size()];
		int pos = 0;
		for (IOffer offer : prices) {
			Good good = offer.getGood();
			double present = inv.getStock(good).getAmount();
			if (ignorelist.contains(good)) {
				targetAmounts[pos++] = present;
			} else {
				double target = getWeight(good) * endowment / totweight / offer.getPrice().getPrice();
				if ((target > present && offer.isBid()) || (target < present && !offer.isBid())) {
					// We want more of something that is not for sale or we want less of something there are no bids for
					ignorelist.add(good);
					return getOptimalAllocation(inv, prices, ignorelist);
				}
				targetAmounts[pos++] = target;
			}
		}
		return targetAmounts;
	}

}
