package com.agentecon.finance;

import java.util.HashMap;

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.consumer.IMarketParticipant;
import com.agentecon.firm.IFirm;
import com.agentecon.firm.IMarketMaker;
import com.agentecon.firm.IStockMarket;
import com.agentecon.firm.Portfolio;
import com.agentecon.firm.Position;
import com.agentecon.firm.Ticker;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.ISubStock;
import com.agentecon.market.IPriceMakerMarket;
import com.agentecon.market.IPriceTakerMarket;
import com.agentecon.production.IPriceProvider;
import com.agentecon.production.PriceUnknownException;
import com.agentecon.util.Average;

public class MarketMaker extends Firm implements IMarketMaker, IPriceProvider, IMarketParticipant {

	private Portfolio portfolio;
	private HashMap<Ticker, MarketMaking> priceBeliefs;

	public MarketMaker(IAgentIdGenerator id, IStock money) {
		super(id, new Endowment(money));
		this.portfolio = new Portfolio(getMoney(), false);
		this.priceBeliefs = new HashMap<Ticker, MarketMaking>();
	}

	@Override
	public void tradeGoods(IPriceTakerMarket market) {
		IMarketParticipant.super.sellSomeGoods(market);
	}

	@Override
	public void managePortfolio(IStockMarket dsm) {
	}

	public void postOffers(IPriceMakerMarket dsm) {
		for (MarketMaking e : priceBeliefs.values()) {
			e.trade(dsm, this);
		}
	}

	@Override
	public double notifyFirmClosed(Ticker ticker) {
		this.priceBeliefs.remove(ticker);
		return IMarketMaker.super.notifyFirmClosed(ticker);
	}

	public void notifyFirmCreated(IFirm firm) {
		Position pos = firm.getShareRegister().createPosition(false);
		portfolio.addPosition(pos);
		MarketMaking price = createPriceBelief(getMoney(), pos);
		MarketMaking prev = priceBeliefs.put(pos.getTicker(), price);
		assert prev == null;
	}

	protected MarketMaking createPriceBelief(IStock wallet, Position pos) {
		ISubStock subWallet = wallet.createSubAccount(1.0, 1.0 / (priceBeliefs.size() + 10));
		return new MarketMaking(subWallet, pos) {
			@Override
			protected void increaseSpread() {
				super.increaseSpread();
				if (subWallet.getAmount() > 1.0) {
					subWallet.pushToParent(0.02);
				}
			}
		};
	}

	@Override
	public double getPriceBelief(Good good) throws PriceUnknownException {
		return getPrice(good);
	}

	public double getPrice(Good output) {
		return priceBeliefs.get(output).getPrice();
	}

	public double getBid(Ticker ticker) {
		return priceBeliefs.get(ticker).getBidPrice();
	}

	public double getAsk(Ticker ticker) {
		return priceBeliefs.get(ticker).getAskPrice();
	}

	public Average getAverageOwnershipShare() {
		Average avg = new Average();
		for (Ticker t : priceBeliefs.keySet()) {
			Position pos = portfolio.getPosition(t);
			avg.add(pos.getOwnershipShare());
		}
		return avg;
	}

	private Average getIndex() {
		Average avg = new Average();
		for (MarketMaking mmp : priceBeliefs.values()) {
			avg.add(mmp.getPrice());
		}
		return avg;
	}

	@Override
	protected double calculateDividends(int day) {
		double tiedCash = 0.0;
		for (MarketMaking mm: priceBeliefs.values()) {
			tiedCash += mm.getBoundCash();
		}
		double cash = getMoney().getAmount();
		return (cash - tiedCash) / 10;
//		if (cash < 500000) {
//			return 0.0;
//		} else if (cash < 1000000) {
//			return cash * 0.01;
//		} else {
//			return cash * 0.05;
//		}
		// double receivedDividend = getPortfolio().getLatestDividendIncome();
		// double portfolioValue = getPortfolio().calculateValue(this);
		// double targetCash = Math.max(MIN_CASH, OFFER_FRACTION * portfolioValue / BUDGET_FRACTION);
		// double excessCash = cash - targetCash;
		// double excessAssets = calculateExcessAssets(TARGET_OWNER_SHIP_SHARE);
		// double ownerShipShare = getAverageOwnershipShare().getAverage();
		// double ownerShipBasedDividend = cash * (ownerShipShare - TARGET_OWNER_SHIP_SHARE);
		// return Math.max(0, Math.max(excessCash / 10, ownerShipBasedDividend));
	}

	@Override
	public MarketMaker clone() {
		return this; // TEMP todo
	}

	@Override
	public Portfolio getPortfolio() {
		return portfolio;
	}

	@Override
	public String toString() {
		return getType() + " with " + getMoney() + ", holding " + getAverageOwnershipShare() + ", price index: " + getIndex().toFullString() + ", dividend " + getShareRegister().getAverageDividend();
	}

}
