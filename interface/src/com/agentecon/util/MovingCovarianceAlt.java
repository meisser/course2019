package com.agentecon.util;

/**
 * Covariance based on Cov(X,Y) = E[XY] - E[X]*E[Y]
 */
public class MovingCovarianceAlt {
	
	private MovingAverage x, y;
	private MovingAverage xy;
	
	public MovingCovarianceAlt(double factor){
		this.x = new MovingAverage(factor);
		this.y = new MovingAverage(factor);
		this.xy = new MovingAverage(factor);
	}
	
	public void add(double x, double y){
		this.x.add(x);
		this.y.add(y);
		this.xy.add(x * y);
	}
	
	public double getCovariance(){
		return xy.getAverage() - x.getAverage() * y.getAverage();
	}
	
	public double getCorrelation(){
		return Math.min(1.0, Math.max(-1.0, getCovariance() / Math.sqrt(x.getVariance() * y.getVariance())));
	}

}
