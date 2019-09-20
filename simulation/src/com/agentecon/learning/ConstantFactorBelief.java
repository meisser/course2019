package com.agentecon.learning;


public class ConstantFactorBelief extends AdjustableBelief {

	private double factor;
	
	public ConstantFactorBelief(double start, double delta) {
		super(start);
		this.factor = 1.0 + delta;
	}
	
	@Override
	protected double getFactor(boolean increase) {
		return factor;
	}

}
