package com.agentecon.finance.bank;

import java.util.Collection;

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.consumer.IConsumer;
import com.agentecon.finance.PortfolioFirm;
import com.agentecon.firm.IStockMarket;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.market.IStatistics;
import com.agentecon.production.PriceUnknownException;

public class CentralBank extends PortfolioFirm {

	private static final double DISTRIBUTION_FRACTION = 0.2;

	private Good indexGood;
	private IDistributionPolicy policy;

	public CentralBank(IDistributionPolicy policy, IAgentIdGenerator ids, Endowment end, Good index) {
		super(ids, end);
		this.indexGood = index;
		this.policy = policy;
	}

	@Override
	public void managePortfolio(IStockMarket dsm) {
	}

	@Override
	protected double calculateDividends(IStatistics stats) {
		return getMoney().getAmount() * 0.01;
	}

	public void distributeMoney(Collection<IConsumer> consumers, IStatistics stats) {
		IStock wallet = getMoney();
		double distribution = DISTRIBUTION_FRACTION;
		try {
			double price = stats.getGoodsMarketStats().getPriceBelief(indexGood);
			double diff = price - 1.0;
			double factor = Math.min(0.1, Math.abs(diff));
			if (diff > 0.0) {
				wallet.remove(factor * wallet.getAmount());
				distribution -= factor;
			} else if (diff < 0.0) {
				wallet.add(factor * wallet.getAmount());
				distribution += factor;
			}
		} catch (PriceUnknownException e) {
		}
		policy.distribute(wallet.hideRelative(1.0 - distribution), consumers);
	}

}
