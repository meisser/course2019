package com.agentecon.finance;

import java.util.Iterator;
import java.util.LinkedList;

import com.agentecon.agent.IAgent;
import com.agentecon.firm.IRegister;
import com.agentecon.firm.IStockMarket;
import com.agentecon.firm.Position;
import com.agentecon.firm.Ticker;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Quantity;
import com.agentecon.market.Bid;
import com.agentecon.util.MovingAverage;
import com.agentecon.util.Numbers;

public class ShareRegister implements IRegister {

	private Ticker ticker;
	private Position rootPosition;
	private double latestDividend;
	private MovingAverage dividend;
	private LinkedList<Position> all;

	public ShareRegister(Ticker ticker, IStock wallet) {
		this.ticker = ticker;
		this.all = new LinkedList<>();
		this.latestDividend = 0.0;
		this.dividend = new MovingAverage(0.8);
		this.rootPosition = new Position(this, ticker, wallet.getGood(), SHARES_PER_COMPANY, false);
		this.all.add(rootPosition);
	}

	public void claimCompanyShares(Position owner) {
		owner.transfer(rootPosition, rootPosition.getAmount() / 2);
	}

	public double raiseCapital(IStockMarket dsm, IAgent owner, IStock wallet, double minAcceptableValuation) {
		double moneyBefore = wallet.getAmount();
		if (!rootPosition.isEmpty()) {
			assert rootPosition.getUncollectedDividends() == 0.0;
			Bid bid = dsm.getBid(getTicker());
			if (bid != null && bid.getPrice().getPrice() > minAcceptableValuation / SHARES_PER_COMPANY) {
				double volume = Math.min(rootPosition.getAmount(), bid.getAmount() / 2);
				bid.accept(owner, wallet, rootPosition, new Quantity(rootPosition.getGood(), volume));
			}
		}
		return wallet.getAmount() - moneyBefore;
	}

	public void payDividend(IStock sourceWallet, double totalDividends) {
		this.dividend.add(totalDividends);
		this.latestDividend = totalDividends;
		
		if (!Numbers.equals(calculateTotalShares(), SHARES_PER_COMPANY)) {
			double diff = calculateTotalShares() - SHARES_PER_COMPANY;
			if (diff > 0) {
				rootPosition.add(diff);
			}
		}

		double eligibleShares = getFreeFloatShares();
		Iterator<Position> iter = all.iterator();
		while (iter.hasNext()) {
			Position pos = iter.next();
			if (pos.isDisposed()) {
				iter.remove();
			} else if (pos == rootPosition){
				// no dividend for ourselves
				assert rootPosition.getUncollectedDividends() == 0.0;
			} else if (pos.getAmount() > 0.0){
				pos.receiveDividend(sourceWallet, totalDividends / eligibleShares);
			}
		}
	}
	
	@Override
	public double getLatestDividend() {
		return latestDividend;
	}

	@Override
	public double getAverageDividend() {
		return dividend.getAverage();
	}
	
	public double getConsumerOwnedShare() {
		double total = 0.0;
		for (Position p: all) {
			if (p.isConsumerPosition()) {
				total += p.getAmount();
			}
		}
		return total / SHARES_PER_COMPANY;
	}

	@Override
	public Position createPosition(boolean consumer) {
		Position pos = new Position(this, getTicker(), rootPosition.getCurrency(), 0.0, consumer);
		all.add(pos);
		return pos;
	}

	public void inherit(Position pos) {
		pos.dispose(rootPosition);
	}

	public Ticker getTicker() {
		return ticker;
	}

	private double calculateTotalShares() {
		double tot = 0.0;
		for (Position p : all) {
			tot += p.getAmount();
		}
		return tot;
	}

	public int getShareholderCount() {
		return all.size();
	}

	@Override
	public double getFreeFloatShares() {
		return SHARES_PER_COMPANY - rootPosition.getAmount();
	}

	@Override
	public String toString() {
		return ticker + " has " + all.size() + " shareholders and pays " + dividend;
	}

}
