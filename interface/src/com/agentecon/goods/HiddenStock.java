package com.agentecon.goods;

import com.agentecon.util.Numbers;

public class HiddenStock extends AbstractStockWrapper implements IStock {
	
	protected double hidden;

	public HiddenStock(IStock wrapped, double hidden) {
		super(wrapped);
		this.hidden = hidden;
		assert wrapped != null;
	}
	
	protected void hideMore(double more){
		this.hidden += more;
	}

	@Override
	public double getAmount() {
		return Math.max(0, super.getAmount() - hidden);
	}

	@Override
	public String toString(){
		return Numbers.toString(getAmount()) + " hiding " + wrapped;
	}

}
