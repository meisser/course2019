// Created on May 29, 2015 by Luzius Meisser

package com.agentecon.consumer;

import com.agentecon.goods.Good;
import com.agentecon.util.Numbers;

public class Weight {
	
	public final Good good;
	public final double weight;
	public final boolean capital;

	public Weight(Good good, double weight) {
		this(good, weight, false);
	}
	
	public Weight(Good good, double weight, boolean capital) {
		this.good = good;
		this.weight = weight;
		this.capital = capital;
		assert weight != 0.0;
	}
	
	@Override
	public String toString(){
		return Numbers.toString(weight) + " " + good;
	}
}
