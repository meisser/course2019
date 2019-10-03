package com.agentecon.configuration;

import java.util.Random;

public class LifeClock {

	private static final long MAX_LIFE = 10000;
	
	private Random rand;
	private double deathRate;

	public LifeClock(int lifeExpectancy, int seed) {
		this.rand = new Random(seed);
		this.deathRate = 1.0d / lifeExpectancy;
	}

	public int getRandomLifeLength() {
		double length = Math.log(rand.nextDouble() - 1.0) / deathRate;
		assert length >= 0;
		return (int) Math.min(MAX_LIFE, Math.round(length));
	}
	
}
