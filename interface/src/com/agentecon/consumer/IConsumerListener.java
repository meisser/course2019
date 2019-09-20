package com.agentecon.consumer;

import com.agentecon.consumer.IConsumer;
import com.agentecon.goods.Inventory;

public interface IConsumerListener {
	
	public void notifyConsuming(IConsumer inst, int age, Inventory inv, double utility);
	
	public void notifyRetiring(IConsumer inst, int age);
	
	public void notifyInvested(IConsumer inst, double amount);
	
	public void notifyDivested(IConsumer inst, double amount);

}
