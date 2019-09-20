// Created on May 28, 2015 by Luzius Meisser

package com.agentecon.production;

import com.agentecon.goods.Quantity;

public interface IProducerListener {

	public void notifyProduced(IProducer inst, Quantity[] inputs, Quantity output);
	
	public void reportResults(IProducer inst, double revenue, double cogs, double expectedProfits);
}
