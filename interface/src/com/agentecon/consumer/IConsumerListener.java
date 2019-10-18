package com.agentecon.consumer;

import com.agentecon.consumer.IConsumer;
import com.agentecon.goods.Inventory;

public interface IConsumerListener {
	
	public default void notifyConsuming(IConsumer inst, int age, Inventory inv, double utility) {}
	
	public default void notifyRetiring(IConsumer inst, int age) {}
	
	public default void notifyInvested(IConsumer inst, double amount) {}
	
	public default void notifyDivested(IConsumer inst, double amount) {}
	
	public default void notifyDied(IConsumer inst) {}

}
