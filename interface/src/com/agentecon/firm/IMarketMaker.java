/**
 * Created by Luzius Meisser on Jun 13, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.firm;

import com.agentecon.market.IPriceMakerMarket;

public interface IMarketMaker extends IShareholder, IFirm {
	
	/**
	 * This is the market maker's opportunity to post his offers to the market
	 * before the price takers can trade.
	 */
	public void postOffers(IPriceMakerMarket market);
	
	/**
	 * Market makers are automatically notified when a new firm has been created.
	 */
	public void notifyFirmCreated(IFirm firm);
	
}
