package com.agentecon.firm;

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.consumer.IMarketParticipant;
import com.agentecon.firm.production.CobbDouglasProduction;
import com.agentecon.goods.IStock;
import com.agentecon.market.IDiscountRate;
import com.agentecon.market.IPriceTakerMarket;
import com.agentecon.market.IStatistics;

public class DefaultFarm extends Farm implements IMarketParticipant {
	
	protected static final double DISCOUNT_RATE = 0.01;
	
	private InvestmentStrategy investments;

	public DefaultFarm(IAgentIdGenerator id, IShareholder owner, IStock money, IStock land, CobbDouglasProduction prodFun, IStatistics stats) {
		super(id, owner, money, land, prodFun, stats);
		this.investments = new InvestmentStrategy(prodFun, stats.getDiscountRate());
	}

	public DefaultFarm(IAgentIdGenerator id, Endowment end, CobbDouglasProduction prodFun) {
		super(id, end, prodFun);
		this.investments = new InvestmentStrategy(prodFun, new IDiscountRate() {
			
			@Override
			public double getCurrentDiscountRate() {
				return DISCOUNT_RATE;
			}
		});
	}

	@Override
	public void tradeGoods(IPriceTakerMarket market) {
		this.investments.invest(this, getInventory(), getFinancials(), market);
	}

}
