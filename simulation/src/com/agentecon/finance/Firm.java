package com.agentecon.finance;

import com.agentecon.agent.Agent;
import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.consumer.IConsumer;
import com.agentecon.firm.FirmListeners;
import com.agentecon.firm.IFirm;
import com.agentecon.firm.IFirmListener;
import com.agentecon.firm.IShareholder;
import com.agentecon.firm.IStockMarket;
import com.agentecon.firm.Portfolio;
import com.agentecon.firm.Position;
import com.agentecon.firm.Ticker;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Inventory;
import com.agentecon.market.IStatistics;

public abstract class Firm extends Agent implements IFirm {

	private final Ticker ticker;
	private final ShareRegister register;
	private final FirmListeners monitor;

	public Firm(IAgentIdGenerator ids, IShareholder owner, Endowment end) {
		this(ids, end);
		Position ownerPosition = this.register.createPosition(owner instanceof IConsumer);
		this.register.claimCompanyShares(ownerPosition);
		owner.getPortfolio().addPosition(ownerPosition);
	}

	public Firm(IAgentIdGenerator ids, Endowment end) {
		super(ids, end);
		this.ticker = new Ticker(getType(), getAgentId());
		this.register = new ShareRegister(ticker, getDividendWallet());
		this.monitor = new FirmListeners();
	}

	@Override
	public ShareRegister getShareRegister() {
		return register;
	}

	@Override
	public Ticker getTicker() {
		return ticker;
	}

	public void addFirmMonitor(IFirmListener prodmon) {
		this.monitor.add(prodmon);
	}

	@Override
	public void inherit(Position pos) {
		register.inherit(pos);
	}

	@Override
	public void raiseCapital(IStockMarket stockmarket) {
		raiseCapital(stockmarket, 0.0);
	}
	
	public double raiseCapital(IStockMarket stockmarket, double valuation) {
		return register.raiseCapital(stockmarket, this, getDividendWallet(), valuation);
	}

	protected double calculateDividends(int day) {
		assert false;
		return 0;
	}
	
	protected double calculateDividends(IStatistics stats) {
		return calculateDividends(stats.getDay());
	}

	public boolean considerBankruptcy(IStatistics stats) {
		super.age();
		return false;
	}
	
	@Override
	public final void payDividends(IStatistics stats) {
		double dividend = calculateDividends(stats);
		// pay at most 20% of the available cash
		IStock dividendWallet = getDividendWallet();
		if (dividend > 0) {
//			double consumerOwned = getShareRegister().getConsumerOwnedShare();
//			dividend /= consumerOwned;
			dividend = Math.min(dividend, dividendWallet.getAmount() * 0.2);
		} else {
			dividend = 0.0; // cannot pay a negative dividend
		}
		monitor.reportDividend(this, dividend);
		register.payDividend(dividendWallet, dividend);
	}

	public final double dispose(Inventory inv, Portfolio shares) {
		inv.absorb(super.dispose());
		if (this instanceof IShareholder) {
			IShareholder meAsShareholder = (IShareholder) this;
			shares.absorb(meAsShareholder.getPortfolio());
		}
		return register.getFreeFloatShares();
	}

	protected IStock getDividendWallet() {
		return getMoney();
	}

}
