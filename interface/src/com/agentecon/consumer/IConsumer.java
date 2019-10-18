// Created on Jun 1, 2015 by Luzius Meisser

package com.agentecon.consumer;

import com.agentecon.agent.IAgent;
import com.agentecon.goods.IStock;

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
	
	/**
	 * Receive an inheritance.
	 */
	public void inherit(Inheritance removeFirst);
	
	public boolean isRetired();
	
	public IUtility getUtilityFunction();
	
	public void addListener(IConsumerListener listener);

	public default void receiveInterest(IStock wallet, double distributionPerConsumer) {
		getMoney().transfer(wallet, distributionPerConsumer);
	}

	
}
