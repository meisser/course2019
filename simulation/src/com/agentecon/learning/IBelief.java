// Created by Luzius on Jun 22, 2014

package com.agentecon.learning;

public interface IBelief extends Cloneable {

	public double getValue();
	
	public void adapt(boolean increase);
	
	public void adaptWithCeiling(boolean increase, double max);
	
	public void adaptWithFloor(boolean increase, double min);

	/**
	 * The price has reached its upper plausible limit. Probably there is none of that good in the market at all.
	 */
	public boolean isProbablyUnobtainable();
	
	public IBelief clone();

}
