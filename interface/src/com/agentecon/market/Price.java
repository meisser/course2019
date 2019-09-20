// Created by Luzius on May 4, 2014

package com.agentecon.market;

import com.agentecon.goods.Good;
import com.agentecon.util.Numbers;

public class Price implements Comparable<Price> {
	
	public static final double MIN_PRICE = Numbers.EPSILON;
	
	private Good good;
	private double price;
	
	public Price(Good good, double price) {
		assert good != null;
		this.good = good;
		this.price = Math.max(price, MIN_PRICE);
	}

	public double getAmountAt(double money) {
		return money / price;
	}
	
	public boolean isAbove(Price price) {
		assert this.good.equals(price.good);
		return this.price > price.price;
	}
	
	public double getPrice() {
		return price;
	}
	
	public Good getGood() {
		return good;
	}
	
	/**
	 * Compares two prices with the given accuracy
	 * For example, 1.05 and 1.00 are equal with accuracy 0.05.
	 * However, 0.95 and 1.00 are not equal as 1.00 / 1.05 > 0.95.
	 * 
	 * @param accuracy in percent, e.g. 0.05
	 * @return whether both prices about the same
	 */
	public boolean equals(Price p, double accuracy) {
		if (p.price < price){
			return p.equals(this, accuracy);
		} else {
			return price * (1.0 + accuracy) >= p.price;
		}
	}
	
	public final int compareTo(Price o) {
		return Double.compare(price, o.price);
	}

	@Override
	public String toString(){
		return good + " at " + Numbers.toString(price);
	}
	
}
