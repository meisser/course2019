package com.agentecon.firm.sensor;

public class SensorAccuracy {
	
	public static final double MIN = 0.04;
	public static final double MAX = 0.2;
	public static final double DEFAULT = MAX / 2;
	
	private double accuracy;
	
	public SensorAccuracy(){
		this(DEFAULT);
	}
	
	public SensorAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}

	public double getOfferSize() {
		return getAccuracy();
	}

	public double getAccuracy() {
		return accuracy;
	}

	public void lessAccurate() {
		accuracy = Math.min(MAX, accuracy * 2);
	}
	
	int count = 0;

	public void moreAccurate() {
		accuracy = Math.max(MIN, accuracy / 1.005);		
	}
	
}
