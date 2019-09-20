package com.agentecon.consumer;

import com.agentecon.goods.Inventory;
import com.agentecon.util.AbstractListenerList;

public class ConsumerListeners extends AbstractListenerList<IConsumerListener> implements IConsumerListener {

	@Override
	public void notifyConsuming(IConsumer inst, int age, Inventory inv, double utility) {
		for (IConsumerListener l: list){
			l.notifyConsuming(inst, age, inv, utility);
		}
	}

	@Override
	public void notifyRetiring(IConsumer inst, int age) {
		for (IConsumerListener l: list){
			l.notifyRetiring(inst, age);
		}		
	}

	@Override
	public void notifyInvested(IConsumer inst, double amount) {
		for (IConsumerListener l: list){
			l.notifyInvested(inst, amount);
		}
	}

	@Override
	public void notifyDivested(IConsumer inst, double amount) {
		for (IConsumerListener l: list){
			l.notifyDivested(inst, amount);
		}
	}

}
