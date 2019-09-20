/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.firm;

import com.agentecon.agent.IAgent;
import com.agentecon.consumer.Weight;
import com.agentecon.firm.decisions.IFinancials;
import com.agentecon.firm.production.CobbDouglasProduction;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Inventory;
import com.agentecon.goods.Quantity;
import com.agentecon.market.IDiscountRate;
import com.agentecon.market.IOffer;
import com.agentecon.market.IPriceTakerMarket;

public class InvestmentStrategy {

	private static final double INVESTMENT_MULTIPLIER = 10;
	
	private CobbDouglasProduction prodFun;
	private IDiscountRate discountRate;

	public InvestmentStrategy(CobbDouglasProduction prodFun, IDiscountRate iDiscountRate) {
		this.prodFun = prodFun;
		this.discountRate = iDiscountRate;
	}

	public double invest(IAgent agent, Inventory inv, IFinancials financials, IPriceTakerMarket market) {
		double revenue = financials.getExpectedRevenue();
		double investments = 0.0;
		for (Weight input : prodFun.getInputWeigths()) {
			if (input.capital) {
				Good inputGood = input.good;
				investments += invest(agent, inv.getMoney(), inv.getStock(inputGood), revenue * input.weight, market);
			}
		}
		return investments;
	}

	private double invest(IAgent agent, IStock money, IStock input, double targetDailySpending, IPriceTakerMarket market) {
		if (agent.getAge() > 100) {
			double invested = considerBuying(agent, money, input, targetDailySpending, market);
			double divested = considerSelling(agent, money, input, targetDailySpending, market);
			return invested - divested;
		} else {
			return 0.0;
		}
	}

	protected double considerBuying(IAgent agent, IStock money, IStock input, double targetDailySpending, IPriceTakerMarket market) {
		double actuallyInvested = 0.0;
		while (money.getAmount() > 100) {
			IOffer offer = market.getOffer(input.getGood(), false);
			if (offer != null) {
				double dailyDiscounting = offer.getPrice().getPrice() * input.getAmount() * discountRate.getCurrentDiscountRate();
				double targetInvestment = INVESTMENT_MULTIPLIER * (targetDailySpending - dailyDiscounting);
				if (targetInvestment > actuallyInvested) {
					double moneyBefore = money.getAmount();
					offer.accept(agent, money, input, new Quantity(input.getGood(), (targetInvestment - actuallyInvested) / offer.getPrice().getPrice()));
					actuallyInvested += moneyBefore - money.getAmount();
				} else {
					break;
				}
			} else {
				break;
			}
		}
		return actuallyInvested;
	}

	protected double considerSelling(IAgent agent, IStock money, IStock input, double targetDailySpending, IPriceTakerMarket market) {
		double actuallyDivested = 0.0;
		while (true) {
			IOffer offer = market.getOffer(input.getGood(), true);
			if (offer != null) {
				double dailyDiscounting = offer.getPrice().getPrice() * input.getAmount() * discountRate.getCurrentDiscountRate();
				double targetDivestment = INVESTMENT_MULTIPLIER * (dailyDiscounting - targetDailySpending);
				if (targetDivestment > actuallyDivested) {
					double moneyBefore = money.getAmount();
					offer.accept(agent, money, input, new Quantity(input.getGood(), (targetDivestment - actuallyDivested) / offer.getPrice().getPrice()));
					actuallyDivested += money.getAmount() - moneyBefore;
				} else {
					return actuallyDivested;
				}
			} else {
				return actuallyDivested;
			}
		}
	}

}
