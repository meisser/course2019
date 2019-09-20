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
import com.agentecon.exercises.HermitConfiguration;
import com.agentecon.finance.Firm;
import com.agentecon.finance.stockpicking.HighestYieldPickingStrategy;
import com.agentecon.firm.DefaultFarm;
import com.agentecon.firm.IFirm;
import com.agentecon.firm.IStockMarket;
import com.agentecon.firm.production.CobbDouglasProduction;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Inventory;
import com.agentecon.goods.Quantity;
import com.agentecon.market.IPriceTakerMarket;
import com.agentecon.market.IStatistics;
import com.agentecon.production.IPriceProvider;
import com.agentecon.production.IProductionFunction;
import com.agentecon.production.PriceUnknownException;
import com.agentecon.research.IFounder;
import com.agentecon.research.IInnovation;
import com.agentecon.util.Numbers;

/**
 * Unlike the Hermit, the farmer can decide to work at other farms and to buy from others. To formalize these relationships, the farmer does not produce himself anymore, but instead uses his land to
 * found a profit-maximizing firm.
 */
public class InvestingConsumer extends MortalConsumer implements IFounder {

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
			double actualInvestment = getPortfolio().invest(new HighestYieldPickingStrategy(), stocks, this, optimalSavings);
			listeners.notifyInvested(this, actualInvestment); // notify listeners for inflow / outflow statistics
		}
	}
	
	@Override
	public IFirm considerCreatingFirm(IStatistics statistics, IInnovation research, IAgentIdGenerator id) {
		IStock myLand = getStock(FarmingConfiguration.LAND);
		if (myLand.getAmount() >= 10 && statistics.getRandomNumberGenerator().nextDouble() < 0.02 && getMoney().getAmount() > 10) {
			// I have plenty of land and feel lucky, let's see if we want to found a farm
			IProductionFunction prod = research.createProductionFunction(FarmingConfiguration.POTATOE);
			if (checkProfitability(statistics.getGoodsMarketStats(), myLand, prod)) {
				IStock startingCapital = getMoney().hideRelative(0.5);
				Firm farm = new DefaultFarm(id, this, startingCapital, myLand, (CobbDouglasProduction) prod, statistics);
				farm.getInventory().getStock(manhours).transfer(getStock(manhours), 14);
				return farm;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	private boolean checkProfitability(IPriceProvider prices, IStock myLand, IProductionFunction prod) {
		try {
			Quantity hypotheticalInput = getStock(manhours).hideRelative(0.5).getQuantity();
			Quantity output = prod.calculateOutput(new Quantity(HermitConfiguration.MAN_HOUR, 12), myLand.getQuantity());
			double profits = prices.getPriceBelief(output) - prices.getPriceBelief(hypotheticalInput);
			return profits > 0;
		} catch (PriceUnknownException e) {
			return true; // market is dead, maybe we are lucky
		}
	}

	@Override
	protected void trade(Inventory inv, IPriceTakerMarket market) {
		IStock myLand = getStock(FarmingConfiguration.LAND);
		if (myLand.getAmount() < 10) {
			market.sellSome(this, getMoney(), myLand);
		}
		Inventory reducedInv = inv.hideRelative(getMoney().getGood(), CAPITAL_BUFFER);
		super.workAtLeast(market, MINIMUM_WORKING_HOURS);
		super.trade(reducedInv, market);
	}

}
