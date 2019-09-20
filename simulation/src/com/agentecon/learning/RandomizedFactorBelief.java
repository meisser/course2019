package com.agentecon.learning;

import java.util.Random;

public class RandomizedFactorBelief extends AdjustableBelief {

	private Random rand;
	private double maxRandDelta;

	public RandomizedFactorBelief(Random rand, double delta) {
		this.rand = rand;
		this.maxRandDelta = 2 * delta; // so E[randomized delta] = delta
	}

	@Override
	protected double getFactor(boolean increase) {
		return 1.0 + maxRandDelta * rand.nextDouble();
	}

}
