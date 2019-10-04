package com.agentecon.finance.bank;

import java.util.Collection;

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.Inheritance;
import com.agentecon.finance.PortfolioFirm;
import com.agentecon.firm.IStockMarket;
import com.agentecon.goods.IStock;
import com.agentecon.market.IStatistics;

public class CentralBank extends PortfolioFirm {

	private static final double DISTRIBUTION_FRACTION = 0.1;
	
	private IDistributionPolicy policy;

	public CentralBank(IDistributionPolicy policy, IAgentIdGenerator ids, Endowment end) {
		super(ids, end);
		this.policy = policy;
	}

	@Override
	public void managePortfolio(IStockMarket dsm) {
	}

	@Override
	protected double calculateDividends(IStatistics stats) {
		return 0;
	}

	public void distributeMoney(Collection<IConsumer> consumers) {
		IStock wallet = getMoney();
		policy.distribute(wallet.hideRelative(1.0 - DISTRIBUTION_FRACTION), consumers);
	}

//	public void inherit(Inheritance inh) {
//		getInventory().absorb(inh.getInventory());
//		getPortfolio().absorb(inh.getPortfolio());
//	}

}
