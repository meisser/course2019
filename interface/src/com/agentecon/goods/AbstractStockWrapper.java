package com.agentecon.goods;

public abstract class AbstractStockWrapper implements IStock {

	protected IStock wrapped;

	public AbstractStockWrapper(IStock wrapped) {
		this.wrapped = wrapped;
	}
	
	@Override
	public double getAmount() {
		return wrapped.getAmount();
	}
	
	@Override
	public double consume() {
		double amount = getAmount();
		remove(amount);
		return amount;
	}
	
	@Override
	public void remove(double quantity) {
		assert quantity >= 0;
		wrapped.remove(quantity);
	}
	
	@Override
	public void addFreshlyProduced(double quantity) {
		wrapped.addFreshlyProduced(quantity);
	}

	@Override
	public void add(double quantity) {
		wrapped.add(quantity);
	}

	@Override
	public void absorb(IStock s) {
		wrapped.absorb(s);
	}

	@Override
	public void deprecate() {
		wrapped.deprecate();
	}
	
	@Override
	public Good getGood() {
		return wrapped.getGood();
	}
	
	@Override
	public IStock duplicate() {
		// it is actually not trivial to correctly duplicate this
		// as we would also need to have a pointed to the duplicate
		// of the wrapped stock
		throw new RuntimeException("Not implemented");
	}
	
	@Override
	public String toString() {
		return "wrapped stock of " + wrapped; 
	}
	
}