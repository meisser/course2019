// Created by Luzius on May 6, 2014

package com.agentecon.consumer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Random;

import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Inventory;
import com.agentecon.market.IOffer;
import com.agentecon.util.Numbers;

public class LogUtilWithAdjustment extends AbstractWeightedUtil {

	public static final double ADJUSTMENT = 1.0; // to avoid negative utility

	public LogUtilWithAdjustment(Weight[] weights, Weight... moreWeights) {
		super(weights, moreWeights);
	}

	public LogUtilWithAdjustment(Weight... weights) {
		super(weights);
	}

	public double getUtility(Collection<IStock> goods) {
		double u = 0.0;
		for (IStock s : goods) {
			double weight = getWeight(s.getGood());
			if (weight > 0.0) {
				u += Math.log(s.getAmount() + ADJUSTMENT) * weight;
			}
		}
		return u;
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
				endowment += (inv.getStock(offer.getGood()).getAmount() + ADJUSTMENT) * offer.getPrice().getPrice();
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
				double target = getWeight(good) * endowment / totweight / offer.getPrice().getPrice() - ADJUSTMENT;
				if ((target > present && offer.isBid()) || (target < present && !offer.isBid())) {
					// We want more of something that is not for sale or we want less of something there are no bids for
					// Should happen rarely
					ignorelist.add(good);
					return getOptimalAllocation(inv, prices, ignorelist);
				} else if (target < 0.0){
					target = 0.0;
					if (Numbers.equals(target, present)){
						ignorelist.add(good);
						return getOptimalAllocation(inv, prices, ignorelist);
					}
				}
				targetAmounts[pos++] = Math.max(0.0, target);
			}
		}
		return targetAmounts;
	}

	public LogUtilWithAdjustment wiggle(Random rand) {
		return new LogUtilWithAdjustment(super.copyWeights(rand));
	}
	
}
