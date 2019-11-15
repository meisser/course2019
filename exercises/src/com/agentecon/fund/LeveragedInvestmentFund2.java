package com.agentecon.fund;

import java.util.ArrayList;

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.configuration.CapitalConfiguration;
import com.agentecon.consumer.IMarketParticipant;
import com.agentecon.finance.Firm;
import com.agentecon.finance.TradingPortfolio;
import com.agentecon.finance.stockpicking.HighestYieldPickingStrategy;
import com.agentecon.firm.IShareholder;
import com.agentecon.firm.IStockMarket;
import com.agentecon.firm.Portfolio;
import com.agentecon.firm.Position;
import com.agentecon.firm.Ticker;
import com.agentecon.goods.IStock;
import com.agentecon.market.Ask;
import com.agentecon.market.Bid;
import com.agentecon.market.IPriceTakerMarket;
import com.agentecon.market.IStatistics;
import com.agentecon.production.PriceUnknownException;

public class LeveragedInvestmentFund2 extends Firm implements IShareholder, IMarketParticipant {

	private static final double RESERVE = 1000; // money that is not touched for buying stocks

	private boolean leverageEnabled;
	private TradingPortfolio portfolio;

	public LeveragedInvestmentFund2(IAgentIdGenerator world, Endowment end) {
		super(world, end);
		this.leverageEnabled = false;
		this.portfolio = new TradingPortfolio(getMoney().hide(RESERVE), false);
	}

	@Override
	public void managePortfolio(IStockMarket dsm) {
		if (!leverageEnabled && dsm.getLeverageProvider() != null) {
			getPortfolio().enableLeverage(this, dsm.getLeverageProvider());
			leverageEnabled = true;
		}
		portfolio.invest(new HighestYieldPickingStrategy(dsm.getLeverageProvider().getInterestRate()), dsm, this, portfolio.getAvailableBudget() * 0.05);
		portfolio.sell(dsm, this, 0.005);

		// Sample code: invest 1000 in each farm
		 for (Ticker ticker: findFirms(dsm, "Farm")) {
		 	portfolio.buy(ticker, dsm, this, 1000);
		 }

		// Sample code: sell half of all market maker shares (if any) and retrieve some data for a particular position
		 for (Position pos: portfolio.getPositions()) {
			 try {
				Ticker ticker = pos.getTicker(); // that's the short name (ticker) of the shares in this position
				 double shares = pos.getAmount(); // that's how many of these shares we have
				 double price = dsm.getMarketStatistics().getPriceBelief(ticker); // a moving average of actually transacted prices (if any)
				 Ask ask = dsm.getAsk(ticker);
				 Bid bid = dsm.getBid(ticker);
				 if (ask != null && bid != null) {
					 double askPrice = ask.getPrice().getPrice();
					 double bidPrice = bid.getPrice().getPrice();
					 double spread = askPrice - bidPrice;
				 }
				 if (ask != null && ask.getPrice().getPrice() < price) {
					 // If the current ask price is below the moving average, spend up to 1% of the available money on this stock
					 double budget = portfolio.getWallet().getAmount();
					 portfolio.buy(ticker, dsm, this, 0.01 * budget);
				 }
				 if (pos.getTicker().getType().contains("MarketMaker")) {
					 // Try to sell all market maker shares
					 portfolio.sell(pos.getTicker(), dsm, this, 1.0);
				 }
			} catch (PriceUnknownException e) {
				// In this case, market statistics could not return a valid price. Do nothing.
			}
		 }
		 
		 double interestRate = dsm.getLeverageProvider().getInterestRate();
		 double haircut = dsm.getLeverageProvider().getHaircut();
		 double collateralRate = 1.0 - haircut; // when your shares have a value of 1000, you can borrow up collateralRate*1000
		 double maxLeverage = 1.0 / haircut; // e.g. with a haircut of 20%, one can reach up to 5-fold leverage over time

		 int day = getAge() + 1000; // calculate the day given that we got founded on day 1000
		 
		 // Print out a tab-separated table of day and portfolio value that can be copied from the console into excel
		 // Please do not upload print statements to the server. The intended way to run this is by running the Simulation
		 // class directly, and not the SimulationServer.
		 System.out.println(day + "\t" + portfolio.calculateValue(dsm.getMarketStatistics()));
	}

	/**
	 * Helper function to find all firms that contain the given string in their type name, e.g. "DefaultFarm", "CreditBank", "LeveragedInvestmentFund", "MarketMaker". Also works with strings like
	 * "team101" on the server.
	 */
	protected ArrayList<Ticker> findFirms(IStockMarket dsm, String type) {
		ArrayList<Ticker> firms = new ArrayList<>();
		for (Ticker t : dsm.getTradedStocks()) {
			if (t.getType().contains(type)) {
				firms.add(t);
			}
		}
		return firms;
	}

	@Override
	public Portfolio getPortfolio() {
		return portfolio;
	}

	@Override
	// Dividend Policy: this is something you might want to adjust
	protected double calculateDividends(IStatistics stats) {
		// Maybe you want a dividend that is related to the portfolio value?
		double portfolioValue = portfolio.calculateValue(stats.getStockMarketStats());
		// Maybe you want to pay out a dividend from the available credit? (does not sound very sustainable)
		double creditAvailable = getPortfolio().getWallet().getAmount();
		// Maybe you want to know who the shareholders are?
		double consumerOwnerPercentage = getShareRegister().getConsumerOwnedShare();

		// default formula... feel free to change
		double cash = getMoney().getAmount();
		if (cash < 10000) {
			return 0.0;
		} else if (cash < 50000) {
			return (cash - 10000) * 0.01;
		} else {
			return 400 + (cash - 50000) * 0.1;
		}
	}

	@Override
	public void tradeGoods(IPriceTakerMarket market) {
		// In case one of the farms we have invested in went bankrupt, we might have ended up
		// with some land in our inventory. In that case, let's try to sell it on the goods market.
		market.sellSome(this, getMoney(), getInventory().getStock(CapitalConfiguration.LAND));
	}

	@Override
	protected IStock getDividendWallet() {
		IStock creditWallet = portfolio.getWallet();
		IStock regularWallet = getMoney();
		// Use whichever wallet has more money available for dividend payments
		if (regularWallet.getAmount() > creditWallet.getAmount()) {
			return regularWallet;
		} else {
			return creditWallet;
		}
	}
	
	@Override
	public boolean considerBankruptcy(IStatistics stats) {
		super.considerBankruptcy(stats);
		return getWealth(stats) <= 0.0;
	}

	@Override
	public String toString() {
		return getTicker() + " with " + portfolio;
	}

}
