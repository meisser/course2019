// Created by Luzius on May 10, 2014

package com.agentecon.learning;

import com.agentecon.util.Numbers;

public abstract class AdjustableBelief implements IBelief {

	public static final double DEFAULT_ACCURACY = 0.03;
	public static final double DEFAULT_PRICE = 2.0;

	public static final double MIN = 0.000001;
	public static final double MAX = 1000000;

	private double price;

	public AdjustableBelief() {
		this(DEFAULT_PRICE);
	}

	public AdjustableBelief(double initial) {
		this.price = initial;
	}

	public void adapt(boolean increase) {
		double factor = getFactor(increase);
		if (increase) {
			price = Math.min(MAX, price * factor);
		} else {
			price = Math.max(MIN, price / factor);
		}
	}

	@Override
	public void adaptWithCeiling(boolean increase, double max) {
		if (price > max) {
			price = max;
		} else {
			adapt(increase);
			if (price > max) {
				price = max;
			}
		}
	}

	@Override
	public void adaptWithFloor(boolean increase, double min) {
		if (price < min) {
			price = min;
		} else {
			adapt(increase);
			if (price < min) {
				price = min;
			}
		}
	}

	public void adapt(double towards, double weight) {
		this.price = price * (1 - weight) + towards * weight;
	}

	protected abstract double getFactor(boolean increase);

	public double getSensorDelta() {
		return getFactor(true) - 1.0;
	}

	public double getValue() {
		return price;
	}

	public boolean isProbablyUnobtainable() {
		return price >= MAX;
	}

	@Override
	public IBelief clone() {
		try {
			return (IBelief) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new java.lang.RuntimeException(e);
		}
	}

	@Override
	public String toString() {
		return Numbers.toString(price);
	}

}
