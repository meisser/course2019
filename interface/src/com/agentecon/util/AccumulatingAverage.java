package com.agentecon.util;

public class AccumulatingAverage {

	private double weight;
	private double sum;
	private IAverage avg;
	private IAverage stability;

	public AccumulatingAverage() {
		this.avg = new Average();
		this.stability = new Average();
		this.reset();
	}

	public AccumulatingAverage(double movingMemory) {
		this.avg = new MovingAverage(movingMemory);
		this.stability = new MovingAverage(movingMemory);
		this.reset();
	}

	public void add(double inc) {
		this.add(1.0, inc);
	}

	public void add(double weight, double inc) {
		this.weight += weight;
		this.sum += inc * weight;
	}

	public double getWeight() {
		return weight;
	}

	public void reset() {
		sum = 0.0;
		weight = 0.0;
	}

	public double flush() {
		if (weight > 0.0) {
			double temp = sum / weight;
			if (avg instanceof Average) {
				((Average) avg).add(weight, temp);
			} else {
				avg.add(temp);
			}
			stability.add(Math.sqrt(avg.getVariance()) / avg.getAverage());
			reset();
			return temp;
		} else {
			return 0.0;
		}
	}

	public boolean isStable() {
		return stability.getAverage() < 0.01;
	}

	public IAverage getWrapped() {
		return avg;
	}

	@Override
	public String toString() {
		return Numbers.toString(avg.getAverage()) + " (std: " + Numbers.toString(Math.sqrt(avg.getVariance())) + (isStable() ? ")" : ", instable)");// + " stability: " + stability.getAverage();
	}

}
