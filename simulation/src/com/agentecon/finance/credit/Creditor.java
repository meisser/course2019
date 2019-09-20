package com.agentecon.finance.credit;

import com.agentecon.agent.AgentRef;
import com.agentecon.agent.IAgent;
import com.agentecon.firm.IStockMarket;
import com.agentecon.firm.Portfolio;
import com.agentecon.goods.IStock;
import com.agentecon.market.IMarketStatistics;

public class Creditor {
	
	public static final double MAX_CREDIT = 1000000000;

	private AgentRef owner;
	private Portfolio collateral;
	private CreditAccount account;

	public Creditor(IAgent owner, Portfolio collateral, IStock wrappedWallet) {
		this.owner = owner.getReference();
		this.collateral = collateral;
		this.account = new CreditAccount(wrappedWallet);
	}

	public void chargeInterestAndUpdateCreditLimit(IStock bankWallet, IMarketStatistics stats, double haircut, double interest) throws CreditorBankruptException {
		if (owner.get().isAlive()) {
			double portfolioValue = collateral.calculateValue(stats);
			this.account.setCreditLimit(Double.MAX_VALUE);
			bankWallet.transfer(this.account, interest * account.getCreditUsed());
			this.account.setCreditLimit(Math.min(MAX_CREDIT, (1.0 - haircut) * portfolioValue));
		} else {
			throw new CreditorBankruptException(account.getCreditUsed());
		}
	}

	public boolean issueMarginCalls(IStockMarket market) {
		boolean soldShares = false;
		boolean canSell = true;
		IAgent agent = owner.get();
		while (account.isLimitExceeded() && canSell) {
			canSell = collateral.sellAny(agent, market);
			soldShares |= canSell;
		}
		return soldShares;
	}

	public CreditAccount getAccount() {
		return account;
	}
}
