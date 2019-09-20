package com.agentecon.finance.credit;

import java.util.ArrayList;
import java.util.Iterator;

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgent;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.finance.Firm;
import com.agentecon.firm.IBank;
import com.agentecon.firm.IStockMarket;
import com.agentecon.firm.Portfolio;
import com.agentecon.goods.IStock;
import com.agentecon.market.IMarketStatistics;

public class CreditBank extends Firm implements IBank {

	private static final double HAIRCUT = 0.2;
	private static final double INTEREST = 0.0001;

	private double creditLoss;
	private ArrayList<Creditor> creditors;

	public CreditBank(IAgentIdGenerator gen, Endowment end) {
		super(gen, end);
		this.creditLoss = 0.0;
		this.creditors = new ArrayList<>();
	}

	public void manageCredit(IStockMarket market) {
		boolean marginCallsIssued = true;
		while (marginCallsIssued) {
			marginCallsIssued = false;
			updateCreditLines(market.getMarketStatistics());
			marginCallsIssued |= issueMarginCalls(market);
		}
	}

	private void updateCreditLines(IMarketStatistics stats) {
		Iterator<Creditor> iter = creditors.iterator();
		while (iter.hasNext()) {
			try {
				Creditor account = iter.next();
				account.chargeInterestAndUpdateCreditLimit(getMoney(), stats, HAIRCUT, INTEREST);
			} catch (CreditorBankruptException e) {
				this.creditLoss += e.getLostMoney();
				iter.remove();
			}
		}
	}

	private boolean issueMarginCalls(IStockMarket market) {
		boolean callIssued = false;
		for (Creditor account : creditors) {
			callIssued |= account.issueMarginCalls(market);
		}
		return callIssued;
	}

	@Override
	public CreditAccount openCreditAccount(IAgent owner, Portfolio portfolio, IStock baseWallet) {
		Creditor creditor = new Creditor(owner, portfolio, baseWallet);
		creditors.add(creditor);
		return creditor.getAccount();
	}

	@Override
	protected double calculateDividends(int day) {
		if (creditLoss > getMoney().getAmount()) {
			creditLoss -= getMoney().getAmount();
			getMoney().consume();
		} else if (creditLoss == 0) {
		} else if (creditLoss < getMoney().getAmount()){
			getMoney().remove(creditLoss);
			creditLoss = 0.0;
		}
		return getMoney().getAmount() / 10;
	}

	@Override
	public double getOutstandingCredit() {
		double credit = 0;
		for (Creditor c : creditors) {
			credit += c.getAccount().getCreditUsed();
		}
		return credit;
	}

	@Override
	public double getInterestRate() {
		return INTEREST;
	}

	@Override
	public double getHaircut() {
		return HAIRCUT;
	}

}
