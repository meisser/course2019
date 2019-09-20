package com.agentecon.firm;

public interface IRegister {
	
	public static final double SHARES_PER_COMPANY = 100;
	
	public default Position createPosition() {
		return createPosition(true);
	}
	
	public default Position createPosition(boolean consumer) {
		return createPosition();
	}
	
	public default double getLatestDividend() {
		return getAverageDividend();
	}
	
	public double getAverageDividend();
	
	public int getShareholderCount();
	
	public default double getTotalShareCount() {
		return SHARES_PER_COMPANY;
	}
	
	public default double getConsumerOwnedShare() {
		return getFreeFloatShares();
	}
	
	public default double getFreeFloatShares() {
		return getTotalShareCount();
	}

	public default double getDividendPerShare() {
		return getAverageDividend() / getFreeFloatShares();
	}

}
