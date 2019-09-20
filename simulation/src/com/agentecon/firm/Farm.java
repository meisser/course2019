/**
 * Created by Luzius Meisser on Jun 19, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.firm;

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.exercises.FarmingConfiguration;
import com.agentecon.finance.Producer;
import com.agentecon.firm.decisions.ExpectedRevenueBasedStrategy;
import com.agentecon.firm.decisions.IFinancials;
import com.agentecon.firm.decisions.IFirmDecisions;
import com.agentecon.firm.production.CobbDouglasProduction;
import com.agentecon.goods.IStock;
import com.agentecon.learning.MarketingDepartment;
import com.agentecon.market.IPriceMakerMarket;
import com.agentecon.market.IStatistics;
import com.agentecon.production.IProductionFunction;
import com.agentecon.production.PriceUnknownException;

public class Farm extends Producer {

	private IFirmDecisions strategy;
	private MarketingDepartment marketing;

	public Farm(IAgentIdGenerator id, IShareholder owner, IStock money, IStock land, IProductionFunction prodFun, IStatistics stats) {
		super(id, owner, prodFun, stats.getMoney());
		this.strategy = new ExpectedRevenueBasedStrategy((CobbDouglasProduction)prodFun);
		this.marketing = new MarketingDepartment(getMoney(), stats.getGoodsMarketStats(), getStock(FarmingConfiguration.MAN_HOUR), getStock(FarmingConfiguration.POTATOE));
		getStock(land.getGood()).absorb(land);
		getMoney().absorb(money);
		assert getMoney().getAmount() > 0;
	}
	
	public Farm(IAgentIdGenerator id, Endowment end, IProductionFunction prodFun) {
		this(id, end, prodFun, null);
	}

	public Farm(IAgentIdGenerator id, Endowment end, IProductionFunction prodFun, IStatistics stats) {
		super(id, end, prodFun);
		this.strategy = new ExpectedRevenueBasedStrategy((CobbDouglasProduction)prodFun);
		this.marketing = new MarketingDepartment(getMoney(), stats == null ? null : stats.getGoodsMarketStats(), getStock(FarmingConfiguration.MAN_HOUR), getStock(FarmingConfiguration.POTATOE));
	}

	protected IStock getLand() {
		return getInventory().getStock(FarmingConfiguration.LAND);
	}

	protected double getInitialBudget(IStatistics stats) {
		try {
			if (stats != null) {
				return stats.getGoodsMarketStats().getPriceBelief(FarmingConfiguration.MAN_HOUR) * 10;
			} else {
				return 100;
			}
		} catch (PriceUnknownException e) {
			return 100;
		}
	}

	@Override
	public void offer(IPriceMakerMarket market) {
		double budget = strategy.calcCogs(getFinancials());
		marketing.createOffers(market, this, budget);
	}

	@Override
	public void adaptPrices() {
		marketing.adaptPrices();
	}

	@Override
	public void produce() {
		super.produce();
	}

	protected IFinancials getFinancials() {
		return marketing.getFinancials(getInventory(), getProductionFunction());
	}

	@Override
	protected double calculateDividends(int day) {
		return strategy.calcDividend(getFinancials());
	}

	private int daysWithoutProfit = 0;

	@Override
	public boolean considerBankruptcy(IStatistics stats) {
		super.considerBankruptcy(stats);
		IFinancials fin = marketing.getFinancials(getInventory(), getProductionFunction());
		double profits = fin.getProfits();
		if (profits <= 0.01) {
			daysWithoutProfit++;
		} else {
			daysWithoutProfit = 0;
		}
		return daysWithoutProfit > 100;
	}

}
