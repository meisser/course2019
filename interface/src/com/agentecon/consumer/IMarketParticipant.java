package com.agentecon.consumer;

import com.agentecon.agent.IAgent;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Inventory;
import com.agentecon.market.IOffer;
import com.agentecon.market.IPriceTakerMarket;

public interface IMarketParticipant extends IAgent {

	/**
	 * Buy and sell goods on the market in a hopefully optimal way given the offers provided by the market makers of the goods market. This method is invoked on all IConsumers before consumption.
	 */
	public void tradeGoods(IPriceTakerMarket market);

	/**
	 * Convenience method for the agents that implement this interface.
	 */
	public default double sellSomeGoods(IPriceTakerMarket market) {
		Inventory inv = getInventory();
		IStock money = inv.getMoney();
		double proceeds = 0.0;
		for (IStock stock : inv.getAll()) {
			if (!stock.getGood().equals(money.getGood())) {
				IOffer offer = market.getOffer(stock.getGood(), true);
				if (offer != null) {
					proceeds += offer.accept(this, money, stock, stock.getQuantity());
				}
			}
		}
		return proceeds;
	}

}
