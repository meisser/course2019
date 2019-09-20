// Created by Luzius on Apr 22, 2014

package com.agentecon.market;

import java.util.Collection;

import com.agentecon.agent.IAgent;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Quantity;

public interface IPriceTakerMarket extends IMarket {

	public Collection<IOffer> getBids();

	public Collection<IOffer> getAsks();

	public Collection<IOffer> getOffers(IPriceFilter bidAskFilter);

	public default Ask getAsk(Good good) {
		return (Ask) getOffer(good, false);
	}

	public default Bid getBid(Good good) {
		return (Bid) getOffer(good, true);
	}

	public default IOffer getOffer(Good good, boolean bid) {
		return bid ? getBid(good) : getAsk(good);
	}

	public default void sellSome(IAgent who, IStock wallet, IStock good) {
		sellSome(who, wallet, good, 1.0);
	}

	/**
	 * Convenience method to sell some of the good if possible
	 */
	public default void sellSome(IAgent who, IStock wallet, IStock good, double fraction) {
		if (good.hasSome()) {
			IOffer offer = getOffer(good.getGood(), true);
			if (offer != null) {
				offer.accept(who, wallet, good, new Quantity(good.getGood(), good.getAmount() * fraction));
			}
		}
	}

	/**
	 * Convenience method to buy some of the good if possible
	 */
	public default void buySome(IAgent who, IStock wallet, IStock good) {
		IOffer offer = getOffer(good.getGood(), false);
		if (offer != null) {
			offer.accept(who, wallet, good, good.getQuantity());
		}
	}

}
