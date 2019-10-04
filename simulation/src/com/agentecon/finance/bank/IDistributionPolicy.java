package com.agentecon.finance.bank;

import java.util.Collection;

import com.agentecon.consumer.IConsumer;
import com.agentecon.goods.IStock;

public interface IDistributionPolicy {
	
	public void distribute(IStock wallet, Collection<IConsumer> consumers);

}
