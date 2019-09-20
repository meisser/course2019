// Created on May 22, 2015 by Luzius Meisser

package com.agentecon.market;

import com.agentecon.goods.Good;

public interface IPriceFilter {

	public boolean isAskPricePreferred(Good good);

	public boolean isOfInterest(Good good);
}
