package com.agentecon.goods;

public class RelativeHiddenStock extends HiddenStock {

	private double fraction;

	public RelativeHiddenStock(IStock wrapped, double fraction) {
		this(wrapped, wrapped.getAmount() * fraction, fraction);
	}
	
	protected RelativeHiddenStock(IStock wrapped, double hidden, double fraction) {
		super(wrapped, hidden);
		this.fraction = fraction;
	}

	@Override
	public void addFreshlyProduced(double quantity) {
		super.hideMore(quantity * fraction);
		super.addFreshlyProduced(quantity);
	}

	@Override
	public void add(double quantity) {
		assert quantity >= 0.0;
		super.hideMore(quantity * fraction);
		super.add(quantity);
	}

	@Override
	public void transfer(IStock source, double amount) {
		assert amount >= 0;
		super.hideMore(amount * fraction);
		super.transfer(source, amount);
	}

	@Override
	public void absorb(IStock s) {
		super.hideMore(s.getAmount() * fraction);
		super.absorb(s);
	}

}
