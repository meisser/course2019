/**
 * Created by Luzius Meisser on Jun 19, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.consumer;

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.exercises.FarmingConfiguration;
import com.agentecon.finance.stockpicking.LimitedSampleYieldPickingStrategy;
import com.agentecon.firm.IStockMarket;
import com.agentecon.goods.Good;
import com.agentecon.goods.Inventory;
import com.agentecon.market.IPriceTakerMarket;
import com.agentecon.util.Numbers;

/**
 * Unlike the Hermit, the farmer can decide to work at other farms and to buy from others. To formalize these relationships, the farmer does not produce himself anymore, but instead uses his land to
 * found a profit-maximizing firm.
 */
public class InvestingConsumer extends RetiringConsumer {

	private static final double DISCOUNT_RATE = 0.995;

	private static final double CAPITAL_BUFFER = 0.80;
	public static final double MINIMUM_WORKING_HOURS = 8;

	private Good manhours;

	public InvestingConsumer(IAgentIdGenerator id, int maxAge, Endowment end, IUtility utility) {
		super(id, maxAge, end, utility);
		this.manhours = end.getDaily()[0].getGood();
		assert this.manhours.equals(FarmingConfiguration.MAN_HOUR);
	}

	@Override
	public void managePortfolio(IStockMarket stocks) {
		boolean retired = isRetired();
		int daysLeft = getMaxAge() - getAge() + 1;
		if (retired) {
			double proceeds = getPortfolio().sell(stocks, this, 1.0d / daysLeft);
			listeners.notifyDivested(this, proceeds); // notify listeners for inflow / outflow statistics
		} else {
			int daysToRetirement = getRetirementAge() - getAge();
			double dividends = getPortfolio().getLatestDividendIncome();
			double constantFactor = Numbers.geometricSum(DISCOUNT_RATE, daysToRetirement);
			double consumption = getDailySpendings();
			double optimalSavings = (consumption * (daysLeft - 1) - dividends / (1 - DISCOUNT_RATE)) / constantFactor + dividends - consumption;
//			double actualInvestment = getPortfolio().invest(new EqualWeightStockPickingStrategy(), stocks, this, optimalSavings);
			double actualInvestment = getPortfolio().invest(new LimitedSampleYieldPickingStrategy(3), stocks, this, optimalSavings);
			listeners.notifyInvested(this, actualInvestment); // notify listeners for inflow / outflow statistics
		}
	}
	
		@Override
	protected void trade(Inventory inv, IPriceTakerMarket market) {
//		IStock myLand = getStock(FarmingConfiguration.LAND);
//		if (myLand.getAmount() < 10) {
//			market.sellSome(this, getMoney(), myLand);
//		}
		Inventory reducedInv = inv.hideRelative(getMoney().getGood(), CAPITAL_BUFFER);
		super.workAtLeast(market, MINIMUM_WORKING_HOURS);
		super.trade(reducedInv, market);
	}

}
