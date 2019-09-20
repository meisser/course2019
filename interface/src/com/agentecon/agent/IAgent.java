// Created on Jun 1, 2015 by Luzius Meisser

package com.agentecon.agent;

import com.agentecon.firm.IShareholder;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Inventory;
import com.agentecon.market.IStatistics;

public interface IAgent extends Cloneable {

	/**
	 * A human-readable type of the agent for display purposes. "Fred's Consumer"
	 */
	public String getType();
	
	/**
	 * A name of that specific instance, usually consisting of type and id, e.g. "Fred's Consumer 13".
	 */
	public String getName();
	
	/**
	 * A convenience method to access the money of an agent. Also accessible through its inventory. 
	 */
	public IStock getMoney();

	/**
	 * The inventory of the agent, including its money.
	 */
	public Inventory getInventory();
	
	public default double getWealth(IStatistics stats) {
		double value = getInventory().calculateValue(stats.getGoodsMarketStats());
		if (this instanceof IShareholder) {
			value += ((IShareholder)this).getPortfolio().calculateValue(stats.getStockMarketStats());
		}
		return value;
	}
	
	public boolean isAlive();
	
	/**
	 * Convenience method for adding a listener of the right type, e.g. addProducerMonitor for producers.
	 */
	public void addListener(Object listener);
	
	
	/// CLONING RELATED FEATURES, NORMALLY NOT USED
	
	/**
	 * Returns an object that always holds the latest instance of that version.
	 * 
	 * Note that when using advanced features, such as market replays (wiggles),
	 * agents may get cloned and or discarded. The instance returned by agentref
	 * is always the current one. In order to avoid memory leaks, it is not
	 * recommended to store direct references to agents in long-lived contexts.
	 */
	public AgentRef getReference();
	
	/**
	 * A unique id of that agent. The id stays the same when cloning an agent.
	 */
	public int getAgentId();
	
	public IAgent clone();

	public int getAge();

}
