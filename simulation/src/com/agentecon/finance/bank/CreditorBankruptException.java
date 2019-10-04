package com.agentecon.finance.bank;

public class CreditorBankruptException extends Exception {

	private double creditUsed;

	public CreditorBankruptException(double creditUsed) {
		this.creditUsed = creditUsed;
	}

	public double getLostMoney() {
		return creditUsed;
	}

}
