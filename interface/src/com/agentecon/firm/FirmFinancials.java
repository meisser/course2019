package com.agentecon.firm;

import com.agentecon.market.GoodStats;
import com.agentecon.market.IStatistics;
import com.agentecon.util.IAverage;

public class FirmFinancials {

	private IFirm firm;
	private final GoodStats stats;
	
	private IStatistics general;
	
	public FirmFinancials(IFirm firm, IStatistics stats) {
		this.firm = firm;
		this.stats = stats.getStockMarketStats().getStats(firm.getTicker());
		this.general = stats;
	}
	
	/**
	 * How many days this firm existed already.
	 */
	public int getFirmAge() {
		return firm.getAge();
	}
	
	/**
	 * The type string of the firm, e.g. "team100-Farm".
	 */
	public String getType() {
		return firm.getType();
	}
	
	/**
	 * The total value of all the assets (land, potatoes, stocks, etc.) owned by the firm at the latest market prices.
	 */
	public double getAssets() {
		return firm.getWealth(general);
	}
	
	/**
	 * Latest total dividends paid out to shareholders.
	 */
	public double getLatestDividend() {
		return firm.getShareRegister().getLatestDividend();
	}
	
	/**
	 * The number of shares not owned by the firm itself.
	 */
	public double getOutstandingShares() {
		return firm.getShareRegister().getFreeFloatShares();
	}
	
	/**
	 * The net cash flows. This is the same as profits in the absence of capital gains/losses.
	 */
	public double getCashflow() {
		return getTotalIncomingCash() - getTotalOutgoingCash();
	}
	
	/**
	 * All cash inflows, including:
	 * - dividend income
	 * - sales of goods
	 * - sales of shares
	 */
	public double getTotalIncomingCash() {
		Ticker t = firm.getTicker();
		double shareSales = general.getStockMarketStats().getFirmStats(t).getSales();
		return shareSales + getSales() + getDividendIncome();
	}
	
	/**
	 * All cash outflow, including:
	 * - spendings on goods
	 * - spendings on shares
	 * 
	 * Dividend payments are not included.
	 */
	public double getTotalOutgoingCash() {
		Ticker t = firm.getTicker();
		double sharePurchases = general.getStockMarketStats().getFirmStats(t).getSpendings();
		return sharePurchases + getCostsOfGoodsSold();
	}
	
	/**
	 * The latest income from selling goods on the goods market. 
	 */
	public double getSales() {
		Ticker t = firm.getTicker();
		return general.getGoodsMarketStats().getFirmStats(t).getSales();
	}
	
	/**
	 * The latest amount spent on goods on the goods market.
	 * @return
	 */
	public double getCostsOfGoodsSold() {
		Ticker t = firm.getTicker();
		return general.getGoodsMarketStats().getFirmStats(t).getSpendings();
	}
	
	/**
	 * The latest income from dividends.
	 */
	public double getDividendIncome() {
		if (firm instanceof IShareholder) {
			return ((IShareholder)firm).getPortfolio().getLatestDividendIncome();
		} else {
			return 0.0;
		}
	}
	
	/**
	 * A long term exponentially weighted average of the share price.
	 * Also allows to find out volatility.
	 */
	public IAverage getPriceLongtermAverage() {
		return stats.getMovingAverage();
	}

	/**
	 * The volume-weighted average price at which its shares traded yesterday.
	 */
	public double getSharePrice() {
		return stats.getYesterday().getAverage();
	}

	/**
	 * Yesterday's trading volume (number of shares).
	 */
	public double getTradingVolume() {
		return stats.getYesterday().getTotWeight();
	}

	/**
	 * The average daily dividend paid per share.
	 */
	public double getDailyDividendPerShare() {
		IRegister register = firm.getShareRegister();
		return register.getDividendPerShare();
	}

	/**
	 * Calculates free-float market capitalization (ignoring the shares held by the company itself).
	 */
	public double getMarketCapitalization() {
		return getPriceLongtermAverage().getAverage() * firm.getShareRegister().getFreeFloatShares();
	}

}
