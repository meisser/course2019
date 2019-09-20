package com.agentecon.events;

import java.util.Random;

import com.agentecon.world.ICountry;

/**
 * Random event has its own random number generator that is initialized
 * right at the beginning, making it less sensitive to random other changes.
 */
public abstract class RandomEvent extends SimEvent {
	
	protected Random rand;

	public RandomEvent(int step, int interval) {
		super(step, interval, 1);
	}
	
	@Override
	public void init(Random rand) {
		this.rand = new Random(rand.nextLong());
	}

	@Override
	public void execute(int day, ICountry sim) {

	}

}
