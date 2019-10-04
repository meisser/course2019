package com.agentecon.finance;

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.firm.IShareholder;
import com.agentecon.firm.Portfolio;

public abstract class PortfolioFirm extends Firm implements IShareholder {

	private Portfolio portfolio;

	public PortfolioFirm(IAgentIdGenerator ids, IShareholder owner, Endowment end) {
		super(ids, owner, end);
		this.portfolio = new Portfolio(getMoney(), false);
	}

	public PortfolioFirm(IAgentIdGenerator ids, Endowment end) {
		super(ids, end);
		this.portfolio = new Portfolio(getMoney(), false);
	}

	public Portfolio getPortfolio() {
		return portfolio;
	}
	
	@Override
	public String toString() {
		return getTicker() + " with " + portfolio;
	}

}