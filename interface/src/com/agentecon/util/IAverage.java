package com.agentecon.util;

public interface IAverage extends Comparable<IAverage>{

	public double getAverage();

	public double getVariance();

	public void add(double value);

	default public int compareTo(IAverage other) {
		return Double.compare(getAverage(), other.getAverage());
	}
	
}
