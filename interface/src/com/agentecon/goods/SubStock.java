package com.agentecon.goods;

import com.agentecon.util.Numbers;

public class SubStock extends AbstractStockWrapper implements ISubStock {

	private double amount;
	private double minimum;

	public SubStock(IStock wrapped, double minimum, double amount) {
		super(wrapped);
		this.minimum = minimum;
		this.amount = amount;
	}

	@Override
	public double getAmount() {
		return Math.min(super.getAmount(), amount);
	}

	@Override
	public void remove(double quantity) {
		assert quantity >= 0.0;
		if (quantity > getAmount()){
			quantity = getAmount(); // prevent negative values due to rounding errors
		}
		super.remove(quantity);
		this.amount -= quantity;
		this.ensureMinimum();
		assert this.amount >= 0.0;
	}

	private final void ensureMinimum() {
		if (this.amount < minimum) {
			this.amount = Math.min(super.getAmount(), minimum);
		}
	}

	@Override
	public void add(double quantity) {
		super.add(quantity);
		this.amount += quantity;
		this.ensureMinimum();
	}

	@Override
	public void addFreshlyProduced(double quantity) {
		super.addFreshlyProduced(quantity);
		this.amount += quantity;
		this.ensureMinimum();
	}
	
	@Override
	public void deprecate() {
		double before = wrapped.getAmount();
		wrapped.deprecate();
		double after = wrapped.getAmount();
		this.amount *= (after / before);
		this.ensureMinimum();
	}

	@Override
	public void absorb(IStock source) {
		this.amount += source.getAmount();
		super.absorb(source);
		this.ensureMinimum();
	}

	@Override
	public void pushToParent(double fraction) {
		this.amount /= (fraction + 1.0);
		this.ensureMinimum();
		assert this.amount >= 0.0;
	}

	@Override
	public String toString() {
		return Numbers.toShortString(amount) + " within " + super.toString();
	}
	
}
