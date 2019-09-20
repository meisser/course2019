// Created on Jun 3, 2015 by Luzius Meisser

package com.agentecon.learning;

import com.agentecon.util.Numbers;

public class HardcodedBelief implements IBelief {
	
	private double price;

	public HardcodedBelief(double price) {
		this.price = price;
	}

	@Override
	public double getValue() {
		return price;
	}

	@Override
	public void adapt(boolean increasePrice) {
	}

	@Override
	public boolean isProbablyUnobtainable() {
		return false;
	}
	
	@Override
	public HardcodedBelief clone(){
		return this;
	}
	
	@Override
	public String toString(){
		return Numbers.toString(price) + "$";
	}

	@Override
	public void adaptWithCeiling(boolean increasePrice, double max) {
	}

	@Override
	public void adaptWithFloor(boolean increasePrice, double min) {
	}

}
