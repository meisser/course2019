/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.learning;

import java.util.Random;

public class QuadraticMaximizer extends RecursiveLeastSquares implements IControl {

	private Random rand;
	private double latest;
	private double min, max;

	public QuadraticMaximizer(double memory, long randSeed, double min, double max) {
		super(memory, 3);
		this.rand = new Random(randSeed);
		this.min = min;
		this.max = max;
		this.latest = 0.0;
	}

	public void adjustBounds(double min, double max) {
		this.min = min;
		this.max = max;
	}

	public double getCurrentInput() {
		double a = weights.get(0, 2);
		double b = weights.get(0, 1);
		double optimum = -b / (2 * a); // extremum for function of form a*x*x + b*x + c
		if (min <= optimum && optimum <= max) {
			// Adding random noise helps against getting stuck
			this.latest = optimum + (rand.nextDouble() - 0.5) / 50;
			return this.latest;
		} else if (min <= max) {
			this.latest = rand.nextDouble() * (max - min) + min;
			return this.latest;
		} else {
			this.latest = Double.NaN;
			return 0.0;
		}
	}
	
	@Override
	public void reportOutput(double output) {
		update(this.latest, output);
	}

	public void update(double latest, double result) {
		if (Double.isFinite(this.latest)) {
			super.update(new Matrix(1.0, latest, latest * latest), result);
		}
	}

}
