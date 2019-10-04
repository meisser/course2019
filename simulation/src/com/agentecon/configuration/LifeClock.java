package com.agentecon.configuration;

import java.util.Random;

public class LifeClock {

	private static final long MAX_LIFE = 10000;

	private Random rand;
	private double deathRate;
	private boolean random;

	public LifeClock(int lifeExpectancy, int seed) {
		this(lifeExpectancy, seed, true);
	}
	
	public LifeClock(int lifeExpectancy, int seed, boolean random) {
		this.rand = new Random(seed);
		this.deathRate = 1.0d / lifeExpectancy;
		this.random = random;
	}

	public int getRandomLifeLength() {
		if (random) {
			double length = -Math.log(1.0 - rand.nextDouble()) / deathRate;
			assert length >= 0;
			return (int) Math.min(MAX_LIFE, Math.round(length));
		} else {
			return (int) Math.round(1.0/deathRate);
		}
	}

}
