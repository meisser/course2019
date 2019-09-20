package com.agentecon.production;

import com.agentecon.goods.Good;
import com.agentecon.goods.Quantity;

public interface IPriceProvider {

	/**
	 * Returns the price for the given good or throws an exception if no price is known.
	 * The price is a moving average of the volume-weighted daily average price.
	 */
	public double getPriceBelief(Good good) throws PriceUnknownException;

	/**
	 * Convenience method for
	 * getPriceBelief(quantity.getGood()) * quantity.getAmount()
	 */
	public default double getPriceBelief(Quantity quantity) throws PriceUnknownException {
		return getPriceBelief(quantity.getGood()) * quantity.getAmount();
	}

}
