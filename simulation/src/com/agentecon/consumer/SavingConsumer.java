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
import com.agentecon.firm.IStockMarket;
import com.agentecon.goods.Inventory;
import com.agentecon.market.IPriceTakerMarket;
import com.agentecon.research.IFounder;

/**
 * Unlike the Hermit, the farmer can decide to work at other farms and to buy from others. To formalize these relationships, the farmer does not produce himself anymore, but instead uses his land to
 * found a profit-maximizing firm.
 */
public class SavingConsumer extends RetiringConsumer implements IFounder {

	private double savings;

	public SavingConsumer(IAgentIdGenerator id, int maxAge, Endowment end, IUtility utility) {
		super(id, maxAge, end, utility);
		this.savings = 0.0;
	}

	@Override
	public void managePortfolio(IStockMarket stocks) {
		boolean retired = isRetired();
		if (retired) {
			int daysLeft = getMaxAge() - getAge() + 1;
			double consumptionToday = this.savings / daysLeft;
			this.savings -= consumptionToday;
		} else {
			double dividends = getPortfolio().getLatestDividendIncome(); // how much dividends did we get today?
			double workFraction = 1.0d / getMaxAge() * getRetirementAge(); // 80%
			double retirementFraction = 1 - workFraction; // 20%
			this.savings += (getDailySpendings() - dividends) / workFraction * retirementFraction;
		}
	}

	@Override
	protected void trade(Inventory inv, IPriceTakerMarket market) {
		// We hide the savings, which we want to keep for the future
		Inventory inventoryWithoutSavings = inv.hide(getMoney().getGood(), savings);
		super.trade(inventoryWithoutSavings, market);
	}

}
