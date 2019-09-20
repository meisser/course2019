package com.agentecon.market;

import com.agentecon.goods.Good;

public class FirmStats {
	
	private double sales;
	private double spendings;

	public void notifySold(Good good, double quantity, double payment) {
		this.sales += payment;
	}

	public void notifyBought(Good good, double quantity, double payment) {
		this.spendings += payment;
	}
	
	public double getSales() {
		return sales;
	}
	
	public double getSpendings() {
		return spendings;
	}

}
