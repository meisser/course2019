package com.agentecon.finance;

import java.util.ArrayList;

import com.agentecon.agent.IAgent;
import com.agentecon.finance.credit.CreditAccount;
import com.agentecon.finance.stockpicking.IStockPickingStrategy;
import com.agentecon.firm.IStockMarket;
import com.agentecon.firm.Portfolio;
import com.agentecon.firm.Position;
import com.agentecon.firm.Ticker;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Quantity;
import com.agentecon.market.Bid;
import com.agentecon.production.IPriceProvider;
import com.agentecon.production.PriceUnknownException;
import com.agentecon.util.Numbers;

public class TradingPortfolio extends Portfolio {

	public TradingPortfolio(IStock money, boolean consumer) {
		super(money, consumer);
	}

	public double getCreditUsed() {
		if (wallet instanceof CreditAccount) {
			return ((CreditAccount) wallet).getCreditUsed();
		} else {
			return 0.0;
		}
	}

	public double getLeverageRatio(double haircut) {
		double creditUsed = getCreditUsed();
		double moneyAvailable = getAvailableBudget();
		double totalAvailableCredit = creditUsed + moneyAvailable;
		double portfolioValueAccordingToBankCalculation = totalAvailableCredit / (1.0 - haircut);
		double ownCapital = portfolioValueAccordingToBankCalculation - creditUsed;
		return portfolioValueAccordingToBankCalculation / ownCapital;
	}

	public double getCombinedValue(IPriceProvider prices, int timeHorizon) throws PriceUnknownException {
		return getSubstanceValue(prices) + getEarningsValue(timeHorizon);
	}

	public double getEarningsValue(int timeHorizon) {
		return getLatestDividendIncome() * timeHorizon;
	}

	public double getSubstanceValue(IPriceProvider prices) throws PriceUnknownException {
		double value = wallet.getAmount();
		for (Position p : inv.values()) {
			value += p.getAmount() * prices.getPriceBelief(p.getTicker());
		}
		return value;
	}

	public double sell(Ticker ticker, IStockMarket stocks, IAgent owner, double fraction) {
		double moneyBefore = wallet.getAmount();
		Position pos = inv.get(ticker);
		if (pos != null) {
			stocks.sell(owner, pos, getWallet(), pos.getAmount() * fraction);
			if (pos.isEmpty()) {
				disposePosition(ticker);
			}
		}
		return wallet.getAmount() - moneyBefore;
	}

	public double sell(IStockMarket stocks, IAgent owner, double fraction) {
		double moneyBefore = wallet.getAmount();
		double sharesToSell = 0.0;
		for (Ticker ticker : new ArrayList<>(inv.keySet())) {
			Position pos = inv.get(ticker);
			sharesToSell += pos.getAmount() * fraction;
			double actuallySold = stocks.sell(owner, pos, wallet, sharesToSell);
			sharesToSell -= actuallySold;
			if (pos.isEmpty()) {
				disposePosition(ticker);
			}
		}
		return wallet.getAmount() - moneyBefore;
	}

	public double divest(IStockPickingStrategy strategy, IStockMarket stocks, IAgent owner, double targetProceeds) {
		double moneyBefore = wallet.getAmount();
		double proceeds = wallet.getAmount() - moneyBefore;
		while (Numbers.isBigger(targetProceeds, proceeds)) {
			Ticker any = strategy.findStockToSell(getPositionTickers(), stocks);
			Position pos = any == null ? null : getPosition(any);
			if (pos == null) {
				break;
			} else if (pos.isEmpty()) {
				disposePosition(any);
			} else {
				Bid bid = stocks.getBid(any);
				if (bid == null) {
					break;
				} else {
					double sharesToSell = targetProceeds / bid.getPrice().getPrice();
					bid.accept(owner, wallet, pos, new Quantity(any, sharesToSell));
					proceeds = wallet.getAmount() - moneyBefore;
					if (pos.isEmpty()) {
						disposePosition(any);
					}
				}
			}
		}
		return proceeds;
	}

	/**
	 * Invest according to the default strategy, weighting the chances of choosing a stock by its market capitalization. This is similar to what an Index-ETF does.
	 */
	public double invest(IStockMarket stocks, IAgent owner, double budget) {
		return invest(new IStockPickingStrategy() {

			@Override
			public Ticker findStockToBuy(IStockMarket stocks) {
				return stocks.getRandomStock(true);
			}
		}, stocks, owner, budget);
	}

	/**
	 * Same as "invest", but with a less sophisticated name.
	 */
	public double buy(Ticker t, IStockMarket dsm, IAgent owner, double budget) {
		return invest(t, dsm, owner, budget);
	}

	public double invest(Ticker t, IStockMarket dsm, IAgent owner, double budget) {
		return invest(new IStockPickingStrategy() {

			@Override
			public Ticker findStockToBuy(IStockMarket stocks) {
				return t;
			}
		}, dsm, owner, budget);
	}

	public double invest(IStockPickingStrategy strategy, IStockMarket stocks, IAgent owner, double budget) {
		double moneyBefore = wallet.getAmount();
		budget = Math.min(moneyBefore, budget);
		if (Numbers.isBigger(budget, 0.0)) {
			Ticker any = strategy.findStockToBuy(stocks);
			if (any != null && stocks.hasAsk(any)) {
				double before = wallet.getAmount();
				Position pos = getPosition(any);
				addPosition(stocks.buy(owner, any, pos, wallet, budget));
				double spent = before - wallet.getAmount();
				invest(strategy, stocks, owner, budget - spent);
			}
		}
		return moneyBefore - wallet.getAmount();
	}

	@Override
	public TradingPortfolio clone(IStock money) {
		return (TradingPortfolio) super.clone(money);
	}

}
