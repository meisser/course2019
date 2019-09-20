// Created on May 22, 2015 by Luzius Meisser

package com.agentecon.market;

import com.agentecon.agent.IAgent;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Quantity;

public interface IOffer {

	/**
	 * The price of the goods
	 */
	public Price getPrice();

	/**
	 * The type of the goods
	 */
	public Good getGood();
	
	/**
	 * The amount of goods offered to buy or sell
	 */
	public double getAmount();

	/**
	 * Convenience method for getAmount() == 0
	 */
	public boolean isUsed();
	
	/**
	 * Is this a bid or ask?
	 * 
	 * @return true if this is a bid offer, i.e. an offer to buy the given good
	 */
	public boolean isBid();
	
	/**
	 * Accept the offer up to the given limit (or the amount available, or the amount
	 * that is affordable with the given wallet, whichever is lower).
	 * 
	 * Wallet content and stock will be adjusted accordingly.
	 * 
	 * @return the amount actually exchanged
	 */
	public double accept(IAgent acceptingAgent, IStock wallet, IStock stock, Quantity limit);

	/**
	 * Return the better one of the two offers (pricewise).
	 * Or any of them if equally good.
	 */
	public IOffer getBetterOne(IOffer other);

}
