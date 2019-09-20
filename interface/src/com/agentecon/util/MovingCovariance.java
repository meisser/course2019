package com.agentecon.util;

public class MovingCovariance {

	private double memory;
	private MovingAverage x, y;
	private double cov;

	public MovingCovariance(double memory) {
		this.memory = memory;
		this.x = new MovingAverage(memory);
		this.y = new MovingAverage(memory);
	}

	public void add(double x, double y) {
		double oldMeanX = this.x.getAverage();
		double oldMeanY = this.y.getAverage();
		this.x.add(x);
		this.y.add(y);
		double newMeanX = this.x.getAverage();
		double newMeanY = this.y.getAverage();
		double adjustment = (newMeanX - oldMeanX) * (newMeanY - oldMeanY);
		this.cov = memory * (cov + adjustment) + (1 - memory) * (x - newMeanX) * (y - newMeanY);
	}

	public double getCovariance() {
		return cov;
	}

	public double getCorrelation() {
		return getCovariance() / Math.sqrt(x.getVariance() * y.getVariance());
	}

	public double getRegressionFactor() {
		return getCovariance() / x.getVariance(); // see "Mathematical Statistics and Data Analysis" by John A. Rice, page 561
	}

	public IAverage getX() {
		return x;
	}

	public IAverage getY() {
		return y;
	}

}
