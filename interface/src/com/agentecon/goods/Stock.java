// Created by Luzius on Apr 22, 2014

package com.agentecon.goods;

import com.agentecon.util.Numbers;

public class Stock implements IStock {
	
	private Good good;
	private double amount;
	private double fresh;
	
	public Stock(Good good){
		this(good, 0);
	}

	public Stock(Good good, double initial) {
		assert good != null;
		this.good = good;
		this.amount = initial;
	}
	
	@Override
	public void addFreshlyProduced(double quantity) {
		add(quantity);
		this.fresh += quantity;
	}
	
	public void deprecate() {
		if (amount > fresh){
			double current = amount - fresh;
			double pers = good.getPersistence();
			double kept = pers * current;
			double loss = current - kept;
			assert Math.abs(loss - (1-pers) * current) < Numbers.EPSILON;
			this.amount = kept + fresh;
			this.fresh = 0.0;
		}
	}
	
	public IStock duplicate() {
		return new Stock(good, amount);
	}
	
	@Override
	public Good getGood(){
		return good;
	}
	
	@Override
	public double getAmount() {
		// TEMP
		if (amount < 0.00000000001d) {
			return 0.0;
		} else {
			return amount;
		}
	}
	
	@Override
	public double consume() {
		double amount = getAmount();
		remove(amount);
		return amount;
	}
	
	@Override
	public void remove(double quantity) {
		assert quantity >= 0.0;
//		assert Math.abs(quantity - amount) >= -Numbers.EPSILON; useless....
		if (quantity > amount || amount - quantity < Numbers.EPSILON){
			quantity = amount; // prevent negative values due to rounding errors
		}
		assert this.amount >= quantity;
		this.amount -= quantity;
	}

	@Override
	public void add(double quantity) {
		assert quantity >= 0.0;
		assert this.amount >= -quantity;
		this.amount += quantity;
	}
	
	@Override
	public void absorb(IStock s) {
		assert s.getGood() == getGood();
		assert this != s;
		add(s.consume());
	}

	@Override
	public String toString(){
		return Numbers.toString(amount) + " " + good.toString();
	}

}
