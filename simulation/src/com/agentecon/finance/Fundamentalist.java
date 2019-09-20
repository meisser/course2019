package com.agentecon.finance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.PriorityQueue;

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.firm.IRegister;
import com.agentecon.firm.IShareholder;
import com.agentecon.firm.IStockMarket;
import com.agentecon.firm.Portfolio;
import com.agentecon.firm.Position;
import com.agentecon.firm.Ticker;
import com.agentecon.goods.IStock;
import com.agentecon.market.Bid;

public class Fundamentalist extends Firm implements IShareholder {
	
	private static final boolean ALLOWED_TO_BUY_OTHER_FUNDAMENTALISTS = true;

	private static final double CASH_RESERVES = 1000;

	private double reserve;
	private Portfolio portfolio;

	public Fundamentalist(IAgentIdGenerator world, Endowment end) {
		super(world, end);
		this.portfolio = new Portfolio(getMoney(), false);
	}

	private double calcInnerValue(IStockMarket dsm) {
		double innerValue = getMoney().getAmount();
		YieldComparator yieldComp2 = new YieldComparator(dsm, false);
		for (Position pos : portfolio.getPositions()) {
			if (dsm.hasBid(pos.getTicker())) {
				innerValue += yieldComp2.getPrice(pos.getTicker()) * pos.getAmount();
			}
		}
		return innerValue;
	}

	public void managePortfolio(IStockMarket dsm) {
		IStock money = getMoney().hide(reserve);

		double outerValue = calcOuterValue(dsm);
		double innerValue = calcInnerValue(dsm);
		boolean buyingAllowed = 1.5 * outerValue > innerValue;
		boolean sellingAllowed = outerValue < 1.5 * innerValue;

		Collection<Ticker> comps = dsm.getTradedStocks();
		PriorityQueue<Ticker> queue = getOfferQueue(dsm, comps);
		int count = queue.size() / 5;
		if (sellingAllowed) {
			sellBadShares(money, dsm, queue, count);
		}
		while (queue.size() > count) {
			queue.poll();
		}
		if (buyingAllowed) {
			buyGoodShares(money, dsm, queue);
		}
	}

	protected void sellBadShares(IStock money, IStockMarket dsm, PriorityQueue<Ticker> queue, int count) {
		for (int i = 0; i < count; i++) {
			Ticker pc = queue.poll();
			Position pos = portfolio.getPosition(pc);
			if (pos != null && !pos.isEmpty()) {
				dsm.sell(this, pos, money, pos.getAmount());
				if (pos.isEmpty()) {
					portfolio.disposePosition(pos.getTicker());
				}
			}
		}
	}

	protected void buyGoodShares(IStock money, IStockMarket dsm, PriorityQueue<Ticker> queue) {
		ArrayList<Ticker> list = new ArrayList<>(queue);
		for (int i = list.size() - 1; i >= 0 && !money.isEmpty(); i--) {
			Ticker pc = list.get(i);
			Position pos = portfolio.getPosition(pc);
			Position pos2 = dsm.buy(this, pc, pos, money, money.getAmount());
			portfolio.addPosition(pos2);
		}
	}

	private double price = 10.0;

	protected double calcOuterValue(IStockMarket dsm) {
		Bid bid = dsm.getBid(getTicker());
		if (bid != null) {
			price = bid.getPrice().getPrice();
		}
		return price * IRegister.SHARES_PER_COMPANY;
	}

	protected Ticker findWorstPosition(IStockMarket dsm) {
		Collection<Position> pos = portfolio.getPositions();
		if (pos.isEmpty()) {
			return null;
		} else {
			PriorityQueue<Ticker> queue = new PriorityQueue<>(pos.size(), new YieldComparator(dsm, false));
			for (Position p : pos) {
				if (dsm.hasBid(p.getTicker())) {
					queue.add(p.getTicker());
				}
			}
			return queue.peek();
		}
	}

	protected PriorityQueue<Ticker> getOfferQueue(IStockMarket dsm, Collection<Ticker> comps) {
		PriorityQueue<Ticker> queue = new PriorityQueue<>(comps.size(), new YieldComparator(dsm, true));
		for (Ticker pc : comps) {
			if (dsm.hasAsk(pc)) {
				if (!ALLOWED_TO_BUY_OTHER_FUNDAMENTALISTS && pc.getType().equals(getTicker().getType())){
					// do not buy fundamentalist shares
				} else if (pc.equals(getTicker())){
					// do not buy own shares
				} else {
					queue.add(pc);
				}
			}
		}
		return queue;
	}

	@Override
	protected double calculateDividends(int day) {
		double excessCash = getMoney().getAmount() - CASH_RESERVES;
		if (excessCash > 0) {
			double dividend = excessCash / 3;
			this.reserve = excessCash - dividend;
			return dividend;
		} else {
			this.reserve = 0.0;
			return 0.0;
		}
	}

	@Override
	public Fundamentalist clone() {
		return this; // TEMP todo
	}

	@Override
	public Portfolio getPortfolio() {
		return portfolio;
	}

	public String toString() {
		return getTicker() + " with " + portfolio;
	}

}
