package com.agentecon.firm.decisions;

public interface IFirmDecisions {
	
	public double calcDividend(IFinancials metrics);

	public double calcCogs(IFinancials financials);

	public IFirmDecisions duplicate();

}
