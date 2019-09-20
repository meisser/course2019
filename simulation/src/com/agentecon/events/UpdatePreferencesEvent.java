// Created on May 21, 2015 by Luzius Meisser

package com.agentecon.events;

import com.agentecon.consumer.IConsumer;
import com.agentecon.world.ICountry;

public abstract class UpdatePreferencesEvent extends SimEvent {

	public UpdatePreferencesEvent(int step) {
		super(step, -1);
	}

	@Override
	public void execute(int day, ICountry sim) {
		for (IConsumer c : sim.getAgents().getRandomConsumers(getCardinality())) {
			update(c);
		}
	}

	protected abstract void update(IConsumer c);

	@Override
	public String toString(){
		return "Update preference event";
	}
	
}
