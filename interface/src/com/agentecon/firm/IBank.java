package com.agentecon.firm;

import com.agentecon.agent.IAgent;
import com.agentecon.goods.IStock;

public interface IBank extends IAgent {

	public IStock openCreditAccount(IAgent owner, Portfolio portfolio, IStock baseWallet);

	public void manageCredit(IStockMarket dsm);

	public double getOutstandingCredit();

	public double getInterestRate();
	
	public double getHaircut();

}
