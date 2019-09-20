package com.agentecon.finance;

import java.util.ArrayList;

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.configuration.CapitalConfiguration;
import com.agentecon.consumer.IMarketParticipant;
import com.agentecon.firm.IRegister;
import com.agentecon.firm.IShareholder;
import com.agentecon.firm.IStockMarket;
import com.agentecon.firm.Portfolio;
import com.agentecon.firm.Ticker;
import com.agentecon.goods.IStock;
import com.agentecon.market.Ask;
import com.agentecon.market.IPriceTakerMarket;

public class DefaultInvestmentFund extends Firm implements IShareholder, IMarketParticipant {

	private double reserve;
	private TradingPortfolio portfolio;

	public DefaultInvestmentFund(IAgentIdGenerator world, Endowment end) {
		super(world, end);
		this.reserve = 1000;
		this.portfolio = new TradingPortfolio(getMoney(), false);
	}

	public void managePortfolio(IStockMarket dsm) {
		IStock money = getMoney().hide(reserve);
		portfolio.invest(dsm, this, money.getAmount() * 0.05);
		portfolio.sell(dsm, this, 0.005);
	}

	protected void investIn(IStockMarket dsm, IStock money, String type, double totalBudget) {
		ArrayList<Ticker> farms = new ArrayList<>();
		double totalCap = 0;
		for (Ticker t : dsm.getTradedStocks()) {
			if (t.getType().contains(type)) {
				Ask ask = dsm.getAsk(t);
				if (ask != null) {
					totalCap += ask.getPrice().getPrice() * IRegister.SHARES_PER_COMPANY;
					farms.add(t);
				}
			}
		}
		for (Ticker t : farms) {
			Ask ask = dsm.getAsk(t);
			portfolio.invest(t, dsm, this, ask.getPrice().getPrice() / totalCap * totalBudget);
		}
	}

	@Override
	public Portfolio getPortfolio() {
		return portfolio;
	}

	@Override
	protected double calculateDividends(int day) {
		double cash = getMoney().getAmount();
		if (cash < 10000) {
			return 0.0;
		} else if (cash < 50000) {
			return (cash - 10000) * 0.01;
		} else {
			return 400 + (cash - 50000) * 0.05;
		}
	}

	@Override
	public void tradeGoods(IPriceTakerMarket market) {
		// In case one of the farms we have invested in went bankrupt, we might have ended up
		// with some land in our inventory. In that case, let's try to sell it on the goods market.
		market.sellSome(this, getMoney(), getInventory().getStock(CapitalConfiguration.LAND));
	}

	@Override
	public String toString() {
		return getTicker() + " with " + portfolio;
	}
	
}
