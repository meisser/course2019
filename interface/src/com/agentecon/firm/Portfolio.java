package com.agentecon.firm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.agentecon.agent.IAgent;
import com.agentecon.goods.IStock;
import com.agentecon.market.Bid;
import com.agentecon.production.IPriceProvider;
import com.agentecon.production.PriceUnknownException;

public class Portfolio implements Cloneable {

	private boolean consumer;
	protected IStock wallet;
	private double dividends;
	private boolean keepEmptyPositions;
	protected HashMap<Ticker, Position> inv;

	public Portfolio(IStock money, boolean consumer) {
		this.wallet = money;
		this.inv = new HashMap<>();
		this.dividends = 0.0;
		this.consumer = consumer;
		this.keepEmptyPositions = false;
	}

	/**
	 * Make the simulation slower as empty positions are not removed. This can be useful when posting limit orders for new stocks.
	 */
	public void setKeepEmptyPositions() {
		this.keepEmptyPositions = true;
	}

	public void enableLeverage(IAgent owner, IBank bank) {
		this.wallet = bank.openCreditAccount(owner, this, wallet);
	}

	public IStock getWallet() {
		return wallet;
	}

	public boolean sellAny(IAgent owner, IStockMarket market) {
		for (Position p : inv.values()) {
			Bid bid = market.getBid(p.getTicker());
			if (bid != null) {
				bid.accept(owner, wallet, p, p.getQuantity());
				if (p.isEmpty()) {
					disposePosition(p.getTicker());
				}
				return true;
			}
		}
		return false;
	}

	public void absorb(Portfolio other) {
		wallet.absorb(other.wallet);
		for (Position p : other.inv.values()) {
			Position existing = inv.get(p.getTicker());
			if (existing == null) {
				inv.put(p.getTicker(), p);
			} else {
				p.dispose(existing);
			}
		}
		other.inv.clear();
	}

	public void absorbPositions(double ratio, Portfolio other) {
		for (Position p : other.inv.values()) {
			Position myPosition = inv.get(p.getTicker());
			if (myPosition == null) {
				myPosition = p.createNewPosition(consumer);
				inv.put(p.getTicker(), myPosition);
			}
			myPosition.transfer(p, p.getAmount() * ratio);
		}
	}

	public void addPosition(Position pos) {
		if (pos != null) {
			Position prev = inv.put(pos.getTicker(), pos);
			if (prev != null && prev != pos) {
				prev.dispose(pos);
			}
		}
	}

	public Collection<Ticker> getPositionTickers() {
		return new ArrayList<>(inv.keySet());
	}

	public Collection<Position> getPositions() {
		return new ArrayList<>(inv.values());
	}

	public Position getPosition(Ticker ticker) {
		return inv.get(ticker);
	}

	public double notifyFirmClosed(Ticker t) {
		Position p = inv.remove(t);
		if (p == null) {
			return 0.0;
		} else {
			double amount = p.consume();
			p.dispose();
			return amount;
		}
	}

	public void disposePosition(Ticker t) {
		if (!keepEmptyPositions) {
			Position p = inv.remove(t);
			if (p != null) {
				p.dispose();
			}
		}
	}

	public void dispose() {
		for (Position p : inv.values()) {
			p.dispose();
		}
		this.inv.clear();
	}

	public boolean hasPositions() {
		return inv.size() > 0;
	}

	public double getAvailableBudget() {
		return wallet.getAmount();
	}

	public void collectDividends() {
		double money = wallet.getAmount();
		for (Position p : inv.values()) {
			p.collectDividend(wallet);
		}
		this.dividends = wallet.getAmount() - money;
	}

	public double getLatestDividendIncome() {
		return dividends;
	}

	public double calculateValue(IPriceProvider stats) {
		double value = wallet.getNetAmount();
		if (value >= 0.0) {
			value = 0.0; // no credit, positive amounts are counted in inventory
		}
		for (IStock stock : inv.values()) {
			try {
				value += stats.getPriceBelief(stock.getQuantity());
			} catch (PriceUnknownException e) {
			}
		}
		return value;
	}

	public Portfolio clone(IStock wallet) {
		try {
			Portfolio klon = (Portfolio) super.clone();
			klon.wallet = wallet;
			// TODO: duplicate positions
			return klon;
		} catch (CloneNotSupportedException e) {
			throw new java.lang.RuntimeException(e);
		}
	}

	@Override
	public String toString() {
		return wallet + ", " + inv.values();
	}

}
