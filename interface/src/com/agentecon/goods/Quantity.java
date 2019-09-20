package com.agentecon.goods;

import com.agentecon.util.Numbers;

public class Quantity {
	
	private final Good good;
	private final double amount;
	
	public Quantity(Good good, double amount){
		this.good = good;
		this.amount = amount;
		assert this.good != null;
		assert this.amount >= 0.0;
	}

	public double getAmount() {
		return amount;
	}

	public Good getGood() {
		return good;
	}
	
	public boolean hasSome() {
		return amount > Numbers.EPSILON;
	}
	
	@Override
	public String toString(){
		return Numbers.toString(amount) + " " + good;
	}

}
