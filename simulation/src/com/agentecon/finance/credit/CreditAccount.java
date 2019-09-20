package com.agentecon.finance.credit;

import com.agentecon.goods.AbstractStockWrapper;
import com.agentecon.goods.IStock;
import com.agentecon.util.Numbers;

public class CreditAccount extends AbstractStockWrapper {
	
	private double creditUsed;
	private double creditLimit;

	public CreditAccount(IStock wrapped) {
		super(wrapped);
	}

	public double getCreditUsed() {
		return creditUsed;
	}

	public void setCreditLimit(double newLimit) {
		this.creditLimit = newLimit;
	}

	public boolean isLimitExceeded() {
		return creditUsed > creditLimit;
	}

	@Override
	public double getNetAmount() {
		return super.getAmount() - creditUsed;
	}

	@Override
	public void deprecate() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public double getAmount() {
		if (isLimitExceeded()) {
			return 0.0;
		} else {
			return super.getAmount() + creditLimit - creditUsed;
		}
	}

	@Override
	public double consume() {
		if (getNetAmount() > 0.0) {
			return super.consume();
		} else {
			return 0.0;
		}
	}

	@Override
	public void remove(double quantity) {
		assert Math.abs(quantity - getAmount()) >= -Numbers.EPSILON;
		double available = super.getAmount();
		if (available >= quantity) {
			super.remove(quantity);
		} else {
			if (available > 0) {
				super.remove(available);
				quantity -= available;
			}
			creditUsed += quantity;
		}
		assert creditUsed <= creditLimit + Numbers.EPSILON;
	}

	@Override
	public void add(double quantity) {
		if (creditUsed > quantity) {
			creditUsed -= quantity;
		} else if (creditUsed <= 0) {
			super.add(quantity);
		} else {
			super.add(quantity - creditUsed);
			creditUsed = 0;
		}
	}

	@Override
	public IStock duplicate() {
		// it is actually not trivial to correctly duplicate this
		throw new RuntimeException("Not implemented");
	}

}
