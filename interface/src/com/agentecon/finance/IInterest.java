package com.agentecon.finance;

public interface IInterest {
	
	public double getInterestRate();
	
	public double getAverageDiscountRate();
	
	public default double getMedianDiscountRate() {
		return getAverageDiscountRate();
	}
	
	public default double getMaxDiscountRate() {
		return getAverageDiscountRate();
	}
	
	public default double getMinDiscountRate() {
		return getAverageDiscountRate();
	}

}
