// Created on Jun 23, 2015 by Luzius Meisser

package com.agentecon.util;

import java.util.Collection;

public class Average implements Cloneable, IAverage {

	private double weight;
	private double sum, squaredSum;
	private double min = Double.MAX_VALUE, max = Double.MIN_VALUE;

	public Average() {
	}

	public Average(Collection<Double> initial) {
		for (Double d : initial) {
			add(d);
		}
	}

	public void add(Average other) {
		add(other.weight, other);
	}

	public void add(double weight, Average other) {
		double ratio = weight / other.weight;
		if (Double.isFinite(ratio)) {
			this.weight += weight;
			this.sum += other.sum * ratio;
			this.squaredSum += other.squaredSum * ratio;
			this.min = Math.min(min, other.min);
			this.max = Math.max(max, other.max);
			assert Double.isFinite(sum);
		}
	}

	public void add(double x) {
		add(1.0, x);
	}

	public void add(double weight, double x) {
		assert Double.isFinite(weight);
		assert Double.isFinite(x);
		this.weight += weight;
		this.sum += weight * x;
		this.squaredSum += weight * x * x;
		this.min = Math.min(min, x);
		this.max = Math.max(max, x);
		assert Double.isFinite(sum);
	}

	public double getTotal() {
		return sum;
	}

	public double getTotWeight() {
		return weight;
	}

	public boolean hasValue() {
		return weight > 0.0;
	}

	public double getAverage() {
		return sum / weight;
	}

	public double getVariance() {
		double avg = getAverage();
		return squaredSum / weight - avg * avg;
	}

	public double getMin() {
		return min;
	}

	public double getMax() {
		return max;
	}

	public String toFullString() {
		return getAverage() + " (min " + getMin() + ", max " + getMax() + ")";
	}

	@Override
	public Average clone() {
		try {
			return (Average) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new java.lang.RuntimeException(e);
		}
	}

	@Override
	public String toString() {
		return "Avg: " + getAverage();
	}

}
