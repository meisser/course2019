package com.agentecon.learning;

/**
 * Implements dynamic exponential search as desribed in the paper
 * "An Agent-Based Simulation of the Stolper-Samuelson Effect", Journal of Computational Economics
 * 
 * See also the illustration in ExpSearchBelief.pdf as well as my blog post:
 * http://meissereconomics.com/2016/08/09/StolperSamuelson.html#main
 */
public class ExpSearchBelief extends AdjustableBelief {

	public static final double MAX_ADAPTION_FACTOR = 0.5;
	public static final double MIN_ADAPTION_FACTOR = 0.005;

	private double speed;
	private double delta;
	private boolean direction;
	private int sameDirectionInARow;

	public ExpSearchBelief(double initialDelta, double initialPrice) {
		super(initialPrice);
		this.delta = initialDelta;
		this.sameDirectionInARow = 0;
		this.speed = 1.1;
	}

	public ExpSearchBelief(double initialPrice) {
		this(DEFAULT_ACCURACY, initialPrice == 0.0 ? DEFAULT_PRICE : initialPrice);
	}
	
	public ExpSearchBelief() {
		this(DEFAULT_ACCURACY, DEFAULT_PRICE);
	}
	
	protected double getMax(){
		return MAX_ADAPTION_FACTOR;
	}
	
	@Override
	protected double getFactor(boolean increase) {
		if (increase == direction) {
			sameDirectionInARow++;
			if (sameDirectionInARow > 0 && sameDirectionInARow % 2 == 0) {
				delta = Math.min(getMax(), delta * speed);
			}
		} else {
			sameDirectionInARow = 0;
			direction = increase;
			delta = Math.max(MIN_ADAPTION_FACTOR, delta / speed);
		}
		double f = 1.0 + delta;
		return f;
	}
	
	@Override
	public String toString() {
		return super.toString(); // + " at factor " + factor;
	}

}
