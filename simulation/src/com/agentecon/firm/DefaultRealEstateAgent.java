/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.firm;

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.configuration.CapitalConfiguration;
import com.agentecon.finance.Producer;
import com.agentecon.firm.sensor.SensorInputFactor;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Quantity;
import com.agentecon.learning.ConstantFactorBelief;
import com.agentecon.learning.ExpSearchBelief;
import com.agentecon.learning.IBelief;
import com.agentecon.market.Ask;
import com.agentecon.market.Bid;
import com.agentecon.market.IPriceMakerMarket;
import com.agentecon.market.Price;
import com.agentecon.production.IProductionFunction;

/**
 * There are only four real estate agent in the simulation, two for each team.
 * 
 * They start with an inventory of 1000 taler and 10 units of land.
 * 
 * All four real estate agents share the same production function, and the production function has a memory! So production gets harder and harder with every function call...
 */
public class DefaultRealEstateAgent extends Producer {

	private static final Good LAND = CapitalConfiguration.LAND;

	private static final double DISTRIBUTION_RATIO = 0.01;
	private static final double LAND_SELL_RATIO = 0.01;

	private InputFactor input;
	private IBelief landPrice;

	private Ask currentAsk;

	public DefaultRealEstateAgent(IAgentIdGenerator id, Endowment end, IProductionFunction prodFun) {
		super(id, end, prodFun);

		assert prodFun.getInputs().length == 1;
		Good manhour = prodFun.getInputs()[0];
		this.input = new SensorInputFactor(getInventory().getStock(manhour), new ExpSearchBelief(10));
		this.landPrice = new ConstantFactorBelief(100, 0.03);
	}

	private IStock getLand() {
		return getInventory().getStock(LAND);
	}

	@Override
	public void offer(IPriceMakerMarket market) {
		IStock money = getMoney();
		IStock land = getLand();

		double marginalCost = getMarginalCost();
		double priceBelief = landPrice.getValue();
		boolean exploration = marginalCost > priceBelief;

		// if we can buy land below marginal costs, buy as much as possible
		Price bidPrice = new Price(LAND, Math.min(marginalCost, priceBelief) * 0.9);
		market.offer(new Bid(this, money, land, bidPrice, bidPrice.getAmountAt(money.getAmount())));

		if (land.hasSome()) {
			Price askPrice = new Price(LAND, priceBelief);
			double amount = exploration ? 0.01 : LAND_SELL_RATIO * land.getAmount() ;
			currentAsk = new Ask(this, money, land, askPrice, amount);
			market.offer(currentAsk);
		} else {
			currentAsk = null;
		}

		if (exploration) {
			// buy only very little man hours for price exploration
			this.input.createOffers(market, this, getMoney(), 1);
		} else {
			// buy some man-hours to produce additional land
			this.input.createOffers(market, this, getMoney(), getMoney().getAmount() / 3);
		}
	}

	/**
	 * Is it actually still worth producing anything at current prices?
	 */
	protected double getMarginalCost() {
		double potentialInvestment = 1000;
		double manHourPrice = input.getPrice();
		Quantity manhours = new Quantity(input.getGood(), potentialInvestment / manHourPrice);
		IProductionFunction prodFun = getProductionFunction();
		Quantity landWeCouldProduce = prodFun.calculateOutput(manhours);
		return landWeCouldProduce.getAmount() / potentialInvestment;
	}

	@Override
	public void adaptPrices() {
		input.adaptPrice();
		if (currentAsk != null) {
			landPrice.adapt(currentAsk.isUsed());
		}
	}

	@Override
	public void produce() {
		super.produce(); // just use all available man-hours to produce some land
	}

	@Override
	protected double calculateDividends(int day) {
		if (day < 3000) {
			return 1;
		} else {
			return getMoney().getAmount() * DISTRIBUTION_RATIO;
		}
	}

}
