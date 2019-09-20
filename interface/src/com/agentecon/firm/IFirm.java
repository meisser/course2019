package com.agentecon.firm;

import com.agentecon.agent.IAgent;
import com.agentecon.goods.Inventory;
import com.agentecon.market.IStatistics;

public interface IFirm extends IAgent {

	public IRegister getShareRegister();

	public Ticker getTicker();
	
	public void inherit(Position pos);
	
	public void raiseCapital(IStockMarket stockmarket);

	@Deprecated
	public default void payDividends(int day) {
		assert false; // should never be called
	}
	
	public default void payDividends(IStatistics stats) {
		payDividends(stats.getDay());
	}
	
	public void addFirmMonitor(IFirmListener monitor);

	/**
	 * Returns true if this firm wants to go bankrupt and distribute all its belongings to its shareholders.
	 */
	public boolean considerBankruptcy(IStatistics stats);

	public double dispose(Inventory inv, Portfolio shares);
	
}
