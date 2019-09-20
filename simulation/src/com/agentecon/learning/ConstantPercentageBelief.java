package com.agentecon.learning;


public class ConstantPercentageBelief extends AdjustableBelief {

	private double delta;
	
	public ConstantPercentageBelief(){
		this(DEFAULT_ACCURACY);
	}
	
	public ConstantPercentageBelief(double delta) {
		super();
		this.delta = delta;
	}
	
	@Override
	protected double getFactor(boolean increase) {
		return increase ? 1.0 + delta : 1/(1.0 - delta);
	}
	
}
