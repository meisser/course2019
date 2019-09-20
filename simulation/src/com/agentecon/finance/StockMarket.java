package com.agentecon.finance;

import java.util.Collection;
import java.util.function.Predicate;

import com.agentecon.firm.IBank;
import com.agentecon.firm.IFirm;
import com.agentecon.firm.IMarketMaker;
import com.agentecon.firm.IShareholder;
import com.agentecon.firm.Ticker;
import com.agentecon.market.IMarketStatistics;
import com.agentecon.market.IStatistics;
import com.agentecon.market.MarketStatistics;
import com.agentecon.sim.SimulationListeners;
import com.agentecon.world.Agents;
import com.agentecon.world.Country;

public class StockMarket {
	
	private static final boolean ALLOW_FUNDS_BUYING_FUNDS = true;

	private Country country;
	private MarketStatistics stockStats;
	private SimulationListeners listeners;

	public StockMarket(Country world, SimulationListeners listeners) {
		this.listeners = listeners;
		this.country = world;
		this.stockStats = new MarketStatistics();
	}

	public void trade(int day, IStatistics stats) {
		Agents ags = country.getAgents();
		for (IFirm firm : ags.getFirms()) {
			firm.payDividends(stats);
		}
		for (IShareholder shareholder : ags.getShareholders()) {
			shareholder.getPortfolio().collectDividends();
		}
		Collection<IMarketMaker> mms = ags.getRandomMarketMakers();
		runDailyMarket(day, ags, mms, stats);
	}

	protected void runDailyMarket(int day, Agents ags, Collection<IMarketMaker> mms, IStatistics stats) {
		FinancialMarketData financials = new FinancialMarketData(ags, stats);
		IBank bank = ags.getBank();
		DailyStockMarket dsm = new DailyStockMarket(financials, bank, country.getRand());
		stockStats.notifyMarketOpened();
		dsm.addMarketListener(stockStats);
		listeners.notifyStockMarketOpened(dsm);

		for (IMarketMaker mm : mms) {
			mm.postOffers(dsm);
		}

		dsm.notifyOffersPosted();

		// System.out.println(day + " trading stats " + dsm.getTradingStats());
		for (IFirm pc : ags.getFirms()) {
			pc.raiseCapital(dsm);
		}
		if (bank != null) {
			bank.manageCredit(dsm);
		}
		if (ALLOW_FUNDS_BUYING_FUNDS) {
			runMarket(ags, dsm);
		} else {
			runFilteredMarket(ags, dsm);
		}
		dsm.close(day);
	}

	protected void runMarket(Agents ags, DailyStockMarket dsm) {
		for (IShareholder con : ags.getRandomShareholders()) {
			con.managePortfolio(dsm);
		}
	}

	protected void runFilteredMarket(Agents ags, DailyStockMarket dsm) {
		Predicate<Ticker> fundFilter = createFundFilter();
		DailyStockMarketFilter filtered = new DailyStockMarketFilter(dsm, fundFilter);
		for (IShareholder con : ags.getRandomShareholders()) {
			if (con instanceof IFirm && fundFilter.test(((IFirm) con).getTicker())) {
				con.managePortfolio(filtered);
			} else {
				con.managePortfolio(dsm);
			}
		}
	}

	protected Predicate<Ticker> createFundFilter() {
		return new Predicate<Ticker>() {

			@Override
			public boolean test(Ticker t) {
				return t.getType().contains("InvestmentFund");
			}
		};
	}

	public IMarketStatistics getStats() {
		return stockStats;
	}

}
