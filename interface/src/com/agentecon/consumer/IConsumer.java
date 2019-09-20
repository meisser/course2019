// Created on Jun 1, 2015 by Luzius Meisser

package com.agentecon.consumer;

import com.agentecon.agent.IAgent;
import com.agentecon.firm.Portfolio;
import com.agentecon.goods.Inventory;

public interface IConsumer extends IAgent, IMarketParticipant {
	
	/**
	 * Called every morning.
	 */
	public void collectDailyEndowment();

	/**
	 * Time to consume, called once per day in the evening after trading goods.
	 * @return the utility gained from consumption.
	 */
	public double consume();
	
	public boolean isMortal();

	/**
	 * Get one day older and die if the maximum age is reached.
	 * In case of death, the inventory and the portfolio are returned
	 * so they can be inherited by others.
	 */
	public default Inheritance considerDeath() {
		return null;
	}
	
	@Deprecated
	public default Inventory considerDeath(Portfolio inheritance) {
		Inheritance inh = considerDeath();
		inheritance.absorb(inh.getPortfolio());
		return inh.getInventory();
	}
	
	/**
	 * Receive an inheritance.
	 */
	public default void inherit(Inheritance removeFirst) {
		throw new RuntimeException("not implemented");
	}
	
	public boolean isRetired();
	
	public IUtility getUtilityFunction();
	
	public void addListener(IConsumerListener listener);

	
}
